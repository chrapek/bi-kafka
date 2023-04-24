package com.pubnub.models

data class MessagePublishModel(
    val channel: String,
    val actualChannel: String,
    val subscribedChannel: String,
    val timetoken: Int,
    val publisher: String,
    val message: Any,
    val subscribeKey: String
)

data class AggregatedPublish(
    val channelsCount: Map<String, Long>,
)