package com.pubnub.topology

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.pubnub.models.AggregatedPublish
import com.pubnub.models.MessagePublishModel
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.*
import java.time.Duration

class Topology {
    companion object {
        @JvmStatic
        val jsonMapper: ObjectMapper = ObjectMapper().registerModule(
            JavaTimeModule(),
        ).registerModule(
            KotlinModule.Builder().build(),
        ).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

        val messageSerde = Serdes.serdeFrom(
            { _, message -> jsonMapper.writeValueAsBytes(message) },
            { _, message -> jsonMapper.readValue<MessagePublishModel>(message) },
        )

        val aggregatedMessageSerde = Serdes.serdeFrom(
            { _, message -> jsonMapper.writeValueAsBytes(message) },
            { _, message -> jsonMapper.readValue<AggregatedPublish>(message) },
        )
    }
    fun buildTopology(): Topology {
        val builder = StreamsBuilder()

        val publish = builder.stream("publish", Consumed.with(Serdes.String(), messageSerde))

        publish.groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
            .aggregate(
                { AggregatedPublish(channelsCount = mapOf<String, Long>()) },
                { key, msg, aggregatedMsg ->
                    val channelsCount = aggregatedMsg.channelsCount.toMutableMap()
                    channelsCount[msg.channel] = channelsCount.getOrDefault(msg.channel, 0L) + 1L
                    AggregatedPublish(channelsCount = channelsCount)
                },
                Materialized.with(Serdes.String(), aggregatedMessageSerde)
            )
            .toStream()
//            .print(Printed.toSysOut())
            .map { key, value -> KeyValue(key.key() + "@" + key.window().start() + "->" + key.window().end(), value) }
            .peek({key, value -> println("Outgoing ${key} ---- ${value}") })
            .to("publish-aggregated", Produced.with(Serdes.String(), aggregatedMessageSerde))

        return builder.build();
    }
}