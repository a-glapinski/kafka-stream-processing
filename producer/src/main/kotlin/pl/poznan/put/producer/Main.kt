package pl.poznan.put.producer

import pl.poznan.put.producer.PropertiesKeys.BOOTSTRAP_SERVERS
import pl.poznan.put.producer.PropertiesKeys.INPUT_DIRECTORY
import pl.poznan.put.producer.PropertiesKeys.SLEEP_INTERVAL_IN_MILLISECONDS
import pl.poznan.put.producer.PropertiesKeys.TOPIC_NAME
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty() && args.size != 4) {
        System.err.println("Args: <bootstrapServers> <topicName> <inputDirectory> <sleepIntervalInMilliseconds>")
        exitProcess(status = 1)
    }

    val properties = Properties().apply {
        set(BOOTSTRAP_SERVERS, args.getOrElse(0) { "localhost:9092" })
        set(TOPIC_NAME, args.getOrElse(1) { "trips-topic" })
        set(INPUT_DIRECTORY, args.getOrElse(2) { "./data/bicycle_result" })
        set(SLEEP_INTERVAL_IN_MILLISECONDS, args.getOrElse(3) { "1000" })
    }
    TripProducer(properties).produce()
}