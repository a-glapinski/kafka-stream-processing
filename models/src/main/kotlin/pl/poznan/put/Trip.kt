package pl.poznan.put

import pl.poznan.put.utils.KafkaJsonSerializable
import java.time.LocalDateTime

data class Trip(
    val id: Int,
    /** (0) - rozpoczęcie przejazdu; (1) - zakończenie przejazdu */
    val startStop: Int,
    val dateTime: LocalDateTime,
    val stationId: Int,
    val duration: Double,
    val userType: String,
    val gender: String,
    val week: Int,
    val temperature: Double,
    val events: String
) : KafkaJsonSerializable
