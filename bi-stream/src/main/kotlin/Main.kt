import com.pubnub.topology.Topology
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import kotlin.random.Random

val logger: Logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

fun main(args: Array<String>) {
    val topology = Topology().buildTopology();
    val props = Properties()
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//    props[StreamsConfig.APPLICATION_ID_CONFIG] = "my-application-id-${Random.nextInt(10)}"
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "my-application-id-p-test-2"
//    props[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = "10000"

    val streams = KafkaStreams(topology, props)
    println(topology.describe())

    streams.start()

    println("hello")

}