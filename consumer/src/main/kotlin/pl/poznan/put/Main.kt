package pl.poznan.put

import pl.poznan.put.PropertiesKeys.APPLICATION_ID
import pl.poznan.put.PropertiesKeys.BICYCLE_STATIONS_FILEPATH
import pl.poznan.put.PropertiesKeys.BOOTSTRAP_SERVERS
import pl.poznan.put.PropertiesKeys.DURATION_IN_MINUTES
import pl.poznan.put.PropertiesKeys.WORKING_STATIONS_RATIO
import java.util.*
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.isNotEmpty() && args.size != 5) {
        System.err.println(
            """Args: 
                |<durationInMinutes> (D)
                |<minimalWorkingStationsRatio> (P) 
                |<bootstrapServers> 
                |<applicationId> 
                |<bicycleStationsFilepath>""".trimMargin()
        )
        exitProcess(status = 1)
    }

    val properties = Properties().apply {
        set(DURATION_IN_MINUTES, args.getOrElse(0) { "60" })
        set(WORKING_STATIONS_RATIO, args.getOrElse(1) { "50" } )
        set(BOOTSTRAP_SERVERS, args.getOrElse(2) { "localhost:9092" })
        set(APPLICATION_ID, args.getOrElse(3) { "kafka-stream-processing" })
        set(BICYCLE_STATIONS_FILEPATH, args.getOrElse(4) { "./data/Divvy_Bicycle_Stations.csv" })
    }

    TripConsumer(properties).consume()
}