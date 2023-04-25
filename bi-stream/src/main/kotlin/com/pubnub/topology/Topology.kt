package com.pubnub.topology

import com.pubnub.models.AggregatedPublish
import com.pubnub.serdes.aggregatedMessageSerde
import com.pubnub.serdes.messageSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig
import java.time.Duration

class Topology {
    fun buildTopology(): Topology {
        val builder = StreamsBuilder()

        val publish = builder.stream("publish", Consumed.with(Serdes.String(), messageSerde))

        publish
            .groupBy(
                { _, value -> "${value.subscribeKey}-${value.channel}" },
                Grouped.with(Serdes.String(), messageSerde)
            )
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(10)))
            .aggregate(
                { AggregatedPublish(channel = "", count = 0L) },
                { key, msg, aggregatedMsg ->
                    AggregatedPublish(
                        subscribeKey = msg.subscribeKey,
                        channel = msg.channel,
                        count = aggregatedMsg.count + 1L
                    )
                },
                Materialized.with(Serdes.String(), aggregatedMessageSerde)
            )
            .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()))
            .toStream()
            .map { key, value ->
                val aggregatedPublish = AggregatedPublish(
                    subscribeKey = value.subscribeKey,
                    channel = value.channel,
                    count = value.count,
                    startDate = key.window().start(),
                    endDate = key.window().end()
                )
                KeyValue(key.key() + "@" + key.window().start() + "->" + key.window().end(), aggregatedPublish)
            }
            .peek { key, value -> println("Outgoing ${key} ---- ${value}") }
            .to("publish-aggregated-single", Produced.with(Serdes.String(), aggregatedMessageSerde))

        return builder.build();
    }
}