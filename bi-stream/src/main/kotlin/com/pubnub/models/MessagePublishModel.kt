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
    val subscribeKey: String = "",
    val channel: String,
    val count: Long,
    val startDate: Long = 0,
    val endDate: Long = 0,
)

data class ChannelCount(
    val channel: String,
    val count: Long,
)



//data class AggregatedPublish(
//    val subscribeKey: String = "",
//    val channelsCount: MutableMap<String, Long>,
//    val startDate: Long = 0,
//    val endDate: Long = 0,
//)
//
//data class AggregatedPublishSer(
//    val subscribeKey: String = "",
//    val channelsCount: MutableMap<String, Long>,
//    val startDate: Long = 0,
//    val endDate: Long = 0,
//)