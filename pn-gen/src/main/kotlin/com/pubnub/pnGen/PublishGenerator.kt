package com.pubnub.pnGen

import io.github.serpro69.kfaker.Faker
import kotlin.random.Random

class PublishGenerator {
    private val faker = Faker();

    data class Publish<T> (
        val channel: String,
        val actualChannel: String,
        val subscribedChannel: String,
        val timetoken: Int,
        val publisher: String,
        val message: T,
        val subscribeKey: String
    )

    data class MessageChat (
        val msg: String,
    )

    data class Action (
        val actionName: String,
        val actionType: String
    )

    data class GameState (
        val actionName: String,
        val actionType: String,
        val actionValue: Int
    )

    data class CurrencyExchange (
        val value: Double,
    )

    fun generateMessageChat(subscribeKey: String): Publish<MessageChat> {
        val channel = faker.random.randomString(2, 3).lowercase();
        val msg = Publish<MessageChat>(
            channel,
            actualChannel = channel,
            subscribedChannel = channel,
            timetoken = 1,
            publisher = faker.name.firstName(),
            subscribeKey = subscribeKey,
            message = MessageChat(
                msg = List(Random.nextInt(2, 30)) { faker.lorem.words() }.joinToString(" ")
            )
        )

        println("Created a message $msg");

        return msg
    }

    fun generateGameStateMessage(subscribeKey: String): Publish<GameState> {
        val channel = faker.worldOfWarcraft.hero().replace(' ', '-').lowercase()
        val actionNames = listOf<Action>(
            Action("Arcane Explosion", "AoE"),
            Action("Chain Lightning", "AoE"),
            Action("Corruption", "DoT"),
            Action("Thunderstorm", "AoE"),
        )
        val randomIndex = Random.nextInt(actionNames.size);
        val msg = Publish<GameState>(
            channel,
            actualChannel = channel,
            subscribedChannel = channel,
            timetoken = 1,
            publisher = faker.name.firstName(),
            subscribeKey = subscribeKey,
            message = GameState(
                actionName = actionNames[randomIndex].actionName,
                actionType = actionNames[randomIndex].actionType,
                actionValue = Random.nextInt(30, 500)
            )
        )

        println("Created a message $msg");

        return msg
    }


    fun generateCurrencyExchange(subscribeKey: String): Publish<CurrencyExchange> {
        val currencies = listOf("USD", "EUR", "PLN", "AUD", "INR", "SGD")
        val selectedCurrencies = currencies.shuffled(Random).take(2)
        val channel = "${selectedCurrencies[0]}-${selectedCurrencies[1]}";
        val msg = Publish<CurrencyExchange>(
            channel,
            actualChannel = channel,
            subscribedChannel = channel,
            timetoken = 1,
            publisher = faker.name.firstName(),
            subscribeKey = subscribeKey,
            message = CurrencyExchange(
                value = Random.nextDouble(1.0, 999999.0),
            )
        )

        println("Created a message $msg");

        return msg
    }
}