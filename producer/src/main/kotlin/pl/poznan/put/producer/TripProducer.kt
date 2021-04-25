package pl.poznan.put.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import pl.poznan.put.producer.PropertiesKeys.INPUT_DIRECTORY
import pl.poznan.put.producer.PropertiesKeys.KEY_SERIALIZER
import pl.poznan.put.producer.PropertiesKeys.SLEEP_INTERVAL_IN_MILLISECONDS
import pl.poznan.put.producer.PropertiesKeys.TOPIC_NAME
import pl.poznan.put.producer.PropertiesKeys.VALUE_SERIALIZER
import pl.poznan.put.common.model.Trip
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class TripProducer(
    private val properties: Properties
) {
    private companion object {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    }

    private val kafkaProducer = KafkaProducer<String, String>(properties.apply {
        set(KEY_SERIALIZER, StringSerializer::class.java)
        set(VALUE_SERIALIZER, StringSerializer::class.java)
    })

    private val topicName
        get() = properties.getProperty(TOPIC_NAME)
    private val inputDirectory
        get() = File(properties.getProperty(INPUT_DIRECTORY))
    private val sleepInterval
        get() = properties.getProperty(SLEEP_INTERVAL_IN_MILLISECONDS).toLong()

    fun produce() {
        inputDirectory.walkTopDown()
            .sortedBy { it.name }
            .filter { it.isFile }
            .filter { it.name.startsWith("part") && it.name.endsWith(".csv") }
            .flatMap { file ->
                sequence {
                    file.useLines { lines ->
                        lines.drop(1)
                            .forEach { yield(it) }
                    }
                }
            }
            .map { line ->
                line.split(',')
                    .let {
                        Trip(
                            id = it[0].toInt(),
                            startStop = it[1].toInt(),
                            dateTime = LocalDateTime.parse(it[2], dateTimeFormatter),
                            stationId = it[3].toInt(),
                            duration = it[4].toDouble(),
                            userType = it[5],
                            gender = it[6],
                            week = it[7].toInt(),
                            temperature = it[8].toDouble(),
                            events = it[9]
                        )
                    }
            }
            .map(Trip::toJsonString)
            .forEach { trip ->
                val futureRecordMetadata = kafkaProducer.send(ProducerRecord(topicName, trip))
                TimeUnit.MILLISECONDS.sleep(sleepInterval)
                println("${futureRecordMetadata.get()} : $trip")
            }
    }
}