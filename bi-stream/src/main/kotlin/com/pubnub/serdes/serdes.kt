package com.pubnub.serdes

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.pubnub.models.AggregatedPublish
import com.pubnub.models.MessagePublishModel
import com.pubnub.schema.aggregatedPublishSchema
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.json.JsonConverter

val jsonMapper: ObjectMapper = ObjectMapper().registerModule(
    JavaTimeModule(),
).registerModule(
    KotlinModule.Builder().build(),
).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

val jsonConverter = JsonConverter().apply {
    configure(mapOf("schemas.enable" to true), false)
}

val messageSerde: Serde<MessagePublishModel> = Serdes.serdeFrom(
    { _, message -> jsonMapper.writeValueAsBytes(message) },
    { _, message -> jsonMapper.readValue<MessagePublishModel>(message) },
)

val aggregatedMessageSerde: Serde<AggregatedPublish> = Serdes.serdeFrom(
    { _, message ->
        val struct = Struct(aggregatedPublishSchema)
            .put("subscribeKey", message.subscribeKey)
            .put("channel", message.channel)
            .put("count", message.count)
            .put("startDate", message.startDate)
            .put("endDate", message.endDate)
        jsonConverter.fromConnectData("publish-aggregated", aggregatedPublishSchema, struct)
    },
    { _, bytes ->
        val struct = jsonConverter.toConnectData("publish-aggregated", bytes).value() as Struct
        AggregatedPublish(
            subscribeKey = struct.getString("subscribeKey"),
            channel = struct.getString("channel"),
            count = struct.getInt64("count"),
            startDate = struct.getInt64("startDate"),
            endDate = struct.getInt64("endDate")
        )
    }
)