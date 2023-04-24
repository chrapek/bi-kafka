package com.pubnub.pnGen

import com.google.gson.Gson
import io.github.serpro69.kfaker.Faker
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class PnGenProducer(brokers: String) {
    private val producer = createProducer(brokers)

    private fun createProducer(brokers: String): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    fun produce(topic: String, ratePerSecond: Int) {
        val gson = Gson();
        val generator = PublishGenerator()
        val waitTimeBetweenIterationsMs = 1000L / ratePerSecond
        println("Producing $ratePerSecond records per second (1 every ${waitTimeBetweenIterationsMs}ms)")

        val msg1 = Faker().random.nextUUID()
        val msg2 = Faker().random.nextUUID()
        val gam1 = Faker().random.nextUUID()
        val curr1 = Faker().random.nextUUID()
        while (true) {
            val messageChat1 = generator.generateMessageChat("sub-c-msg-1-${msg1}")
            val messageChat2 = generator.generateMessageChat("sub-c-msg-2-${msg2}")
            val gameState = generator.generateGameStateMessage("sub-c-gam-${gam1}")
            val currencyExchange = generator.generateCurrencyExchange("sub-c-currency-ex-${curr1}")

            producer.send(ProducerRecord(topic, messageChat1.subscribeKey, gson.toJson(messageChat1)))
            producer.send(ProducerRecord(topic, messageChat2.subscribeKey, gson.toJson(messageChat2)))
            producer.send(ProducerRecord(topic, gameState.subscribeKey, gson.toJson(gameState)))
            producer.send(ProducerRecord(topic, currencyExchange.subscribeKey, gson.toJson(currencyExchange)))

            Thread.sleep(waitTimeBetweenIterationsMs)

            // wait for the write acknowledgment
//            futureResult.get()
        }
    }
}