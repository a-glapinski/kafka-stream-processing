package pl.poznan.put

import pl.poznan.put.PropertiesKeys.BOOTSTRAP_SERVERS
import pl.poznan.put.PropertiesKeys.INPUT_DIRECTORY
import pl.poznan.put.PropertiesKeys.SLEEP_INTERVAL_IN_SECONDS
import pl.poznan.put.PropertiesKeys.TOPIC_NAME
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty() && args.size != 4) {
        System.err.println("Usage: <bootstrapServers> <topicName> <inputDirectory> <sleepIntervalInSeconds>")
        exitProcess(status = 1)
    }

    val properties = Properties().apply {
        set(BOOTSTRAP_SERVERS, args.getOrElse(0) { "localhost:9092" })
        set(TOPIC_NAME, args.getOrElse(1) { "trips-topic" })
        set(INPUT_DIRECTORY, args.getOrElse(2) { "./data/bicycle_result" })
        set(SLEEP_INTERVAL_IN_SECONDS, args.getOrElse(3) { "1" })
    }
    TripProducer(properties).produce()
}