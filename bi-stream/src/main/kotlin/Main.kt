import com.pubnub.topology.Topology
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsConfig
import java.util.*

fun main(args: Array<String>) {
    val topology = Topology().buildTopology();
    val props = Properties()
    props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
//    props[StreamsConfig.APPLICATION_ID_CONFIG] = "my-application-id-${Random.nextInt(10)}"
    props[StreamsConfig.APPLICATION_ID_CONFIG] = "my-application-id-p-test-speed"
    props[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = "10000"

    val streams = KafkaStreams(topology, props)
    println(topology.describe())


    streams.start()
}