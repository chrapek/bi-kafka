package com.pubnub.pnGen

import io.github.serpro69.kfaker.Faker
import java.util.Currency
import kotlin.random.Random
import kotlin.reflect.KFunction0

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

//
//Sensor Network: Ambient Temperature: 20.99℃. Humidity: 79.9747%. Photosensor: 779.96w/m2. Radiation level: 203 millirads/hr. Sensor ID: probe-veqvrdig
//Game State: Action Name: Arcane Explosion. Action Type: AoE. Action Value: 397. Coordinates: { x = 25, y = 39 }
//Market Order: Stock: Elerium. Bid Price: 156.89. Order Quantity: 536. Trade Type: stop
//Game State: Action Name: Rend. Action Type: DoT. Action Value: 255. Coordinates: { x = 26, y = 71 }
//Game State: Action Name: Immolate. Action Type: DoT. Action Value: 388. Coordinates: { x = 31, y = 27 }
//Sensor Network: Ambient Temperature: 21.02℃. Humidity: 79.5209%. Photosensor: 780.79w/m2. Radiation level: 196 millirads/hr. Sensor ID: probe-ewrmyzls
//Market Order: Stock: Apple. Bid Price: 183.81. Order Quantity: 358. Trade Type: fill or kill
//Game State: Action Name: Rend. Action Type: DoT. Action Value: 208. Coordinates: { x = 13, y = 20 }
//Game State: Action Name: Arcane Explosion. Action Type: AoE. Action Value: 458. Coordinates: { x = 30, y = 26 }
//Sensor Network: Ambient Temperature: 21.01℃. Humidity: 80.4171%. Photosensor: 780.07w/m2. Radiation level: 207 millirads/hr. Sensor ID: probe-ffkmqdbz
//Game State: Action Name: Ultima. Action Type: AoE. Action Value: 195. Coordinates: { x = 98, y = 44 }
//Game State: Action Name: Frostbolt. Action Type: Instant. Action Value: 407. Coordinates: { x = 72, y = 38 }
//Market Order: Stock: Elerium. Bid Price: 139.19. Order Quantity: 783. Trade Type: limit
//Sensor Network: Ambient Temperature: 20.9℃. Humidity: 80.645%. Photosensor: 812.34w/m2. Radiation level: 196 millirads/hr. Sensor ID: probe-sitollyq
//Game State: Action Name: Rend. Action Type: DoT. Action Value: 280. Coordinates: { x = 29, y = 27 }
//Market Order: Stock: Google. Bid Price: 376.23. Order Quantity: 625. Trade Type: market
//Game State: Action Name: Immolate. Action Type: DoT. Action Value: 256. Coordinates: { x = 15, y = 21 }
//Sensor Network: Ambient Temperature: 20.77℃. Humidity: 79.6787%. Photosensor: 821.4w/m2. Radiation level: 201 millirads/hr. Sensor ID: probe-kdsnpmel
//Game State: Action Name: Immolate. Action Type: DoT. Action Value: 515. Coordinates: { x = 41, y = 86 }
//Game State: Action Name: Ultima. Action Type: AoE. Action Value: 366. Coordinates: { x = 21, y = 19 }
//Sensor Network: Ambient Temperature: 20.99℃. Humidity: 80.0951%. Photosensor: 801.34w/m2. Radiation level: 206 millirads/hr. Sensor ID: probe-cekpbfkb
//Market Order: Stock: Bespin Gas. Bid Price: 228.06. Order Quantity: 220. Trade Type: fill or kill
//Game State: Action Name: Rend. Action Type: DoT. Action Value: 297. Coordinates: { x = 33, y = 28 }
//Game State: Action Name: Rend. Action Type: DoT. Action Value: 453. Coordinates: { x = 31, y = 26 }