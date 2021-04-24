package pl.poznan.put.model

import pl.poznan.put.utils.KafkaJsonSerializable
import java.time.LocalDateTime

data class ConsumerTripStationKey(
    val eventDateTime: LocalDateTime,
    val stationName: String
) : KafkaJsonSerializable {
    constructor(tripBicycleStation: TripBicycleStation) : this(
        eventDateTime = tripBicycleStation.trip.dateTime,
        stationName = tripBicycleStation.bicycleStation.name
    )
}
