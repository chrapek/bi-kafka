//        val jsonMapper: ObjectMapper = ObjectMapper().registerModule(
//            JavaTimeModule(),
//        ).registerModule(
//            KotlinModule.Builder().build(),
//        ).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

//        val aggregatedMessageSerdeMap = Serdes.serdeFrom(
//            { _, message -> jsonMapper.writeValueAsBytes(message) },
//            { _, message -> jsonMapper.readValue<AggregatedPublish>(message) },
//        )


//        val channelCountSchema: Schema = SchemaBuilder.struct()
//            .field("channel", Schema.STRING_SCHEMA)
//            .field("count", Schema.INT64_SCHEMA)
//            .build()

//                val channelsCount = struct.getArray<ChannelCount>("channelsCount").filterIsInstance<Struct>().map { structItem ->
//                    ChannelCount(
//                        channel = structItem.getString("channel"),
//                        count = structItem.getInt64("count")
//                    )
//                }



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