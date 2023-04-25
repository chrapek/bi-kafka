package com.pubnub.schema

import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.data.SchemaBuilder

val aggregatedPublishSchema: Schema = SchemaBuilder.struct()
    .field("subscribeKey", Schema.STRING_SCHEMA)
    .field("channel", Schema.STRING_SCHEMA)
    .field("count", Schema.INT64_SCHEMA)
    .field("startDate", Schema.INT64_SCHEMA)
    .field("endDate", Schema.INT64_SCHEMA)
    .build()
