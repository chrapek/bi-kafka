package com.pubnub.topology

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.pubnub.models.AggregatedPublish
import com.pubnub.models.ChannelCount
import com.pubnub.models.MessagePublishModel
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.json.JsonConverter
import java.time.Duration

class Topology {
    companion object {
        @JvmStatic
        val jsonMapper: ObjectMapper = ObjectMapper().registerModule(
            JavaTimeModule(),
        ).registerModule(
            KotlinModule.Builder().build(),
        ).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

        val jsonConverter = JsonConverter().apply {
            configure(mapOf("schemas.enable" to true), false)
        }

        val channelCountSchema: Schema = SchemaBuilder.struct()
            .field("channel", Schema.STRING_SCHEMA)
            .field("count", Schema.INT64_SCHEMA)
            .build()

        val aggregatedPublishSchema: Schema = SchemaBuilder.struct()
            .field("subscribeKey", Schema.STRING_SCHEMA)
            .field("channelsCount", SchemaBuilder.array(channelCountSchema))
            .field("startDate", Schema.INT64_SCHEMA)
            .field("endDate", Schema.INT64_SCHEMA)
            .build()

        val messageSerde = Serdes.serdeFrom(
            { _, message -> jsonMapper.writeValueAsBytes(message) },
            { _, message -> jsonMapper.readValue<MessagePublishModel>(message) },
        )

//        val aggregatedMessageSerdeMap = Serdes.serdeFrom(
//            { _, message -> jsonMapper.writeValueAsBytes(message) },
//            { _, message -> jsonMapper.readValue<AggregatedPublish>(message) },
//        )

        val aggregatedMessageSerde: Serde<AggregatedPublish> = Serdes.serdeFrom(
            { _, message ->
                val structChannelsCount = message.channelsCount.map {
                    Struct(channelCountSchema)
                        .put("channel", it.channel)
                        .put("count", it.count)
                }
                val struct = Struct(aggregatedPublishSchema)
                    .put("subscribeKey", message.subscribeKey)
                    .put("channelsCount", structChannelsCount)
                    .put("startDate", message.startDate)
                    .put("endDate", message.endDate)
                jsonConverter.fromConnectData("publish-aggregated", aggregatedPublishSchema, struct)
            },
            { _, bytes ->
                val struct = jsonConverter.toConnectData("publish-aggregated", bytes).value() as Struct
                val channelsCount = struct.getArray<ChannelCount>("channelsCount").filterIsInstance<Struct>().map { structItem ->
                    ChannelCount(
                        channel = structItem.getString("channel"),
                        count = structItem.getInt64("count")
                    )
                }
                AggregatedPublish(
                    subscribeKey = struct.getString("subscribeKey"),
                    channelsCount = channelsCount,
                    startDate = struct.getInt64("startDate"),
                    endDate = struct.getInt64("endDate")
                )
            }
        )
    }
    fun buildTopology(): Topology {
        val builder = StreamsBuilder()

        val publish = builder.stream("publish", Consumed.with(Serdes.String(), messageSerde))

        publish.groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
            .aggregate(
                { AggregatedPublish(channelsCount = listOf()) },
                { key, msg, aggregatedMsg ->
                    val channelsCount = aggregatedMsg.channelsCount.toMutableList<ChannelCount>()
                    val existingChannelCount = channelsCount.find { it.channel == msg.channel }

                    if (existingChannelCount != null) {
                        channelsCount.remove(existingChannelCount)
                        channelsCount.add(ChannelCount(existingChannelCount.channel, existingChannelCount.count + 1L))
                    } else {
                        channelsCount.add(ChannelCount(msg.channel, 1L))
                    }

                    AggregatedPublish(
                        subscribeKey = msg.subscribeKey,
                        channelsCount = channelsCount
                    )
                },
                Materialized.with(Serdes.String(), aggregatedMessageSerde)
            )
            .toStream()
            .map { key, value ->
                val aggregatedPublish = AggregatedPublish(
                    subscribeKey = value.subscribeKey,
                    channelsCount = value.channelsCount,
                    startDate = key.window().start(),
                    endDate = key.window().end()
                )
                KeyValue(key.key() + "@" + key.window().start() + "->" + key.window().end(), aggregatedPublish)
            }
            .peek { key, value -> println("Outgoing ${key} ---- ${value}") }
            .to("publish-aggregated")

        return builder.build();
    }
}