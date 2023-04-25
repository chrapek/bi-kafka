import com.pubnub.pnGen.PnGenProducer
import com.pubnub.pnGen.PublishGenerator
import io.github.serpro69.kfaker.Faker
import kotlin.random.Random

fun main(args: Array<String>) {
    println("Hello World!")

    val faker = Faker();
    val producer = PnGenProducer("localhost:9092");
    producer.produce("publish-test", 100)

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")
}