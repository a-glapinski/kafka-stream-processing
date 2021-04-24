package pl.poznan.put.consumer.model

import pl.poznan.put.common.model.TripBicycleStation
import pl.poznan.put.common.utils.KafkaJsonSerializable
import java.time.LocalDateTime

data class ConsumerTripStationKey(
    val eventDateTime: LocalDateTime,
    val stationName: String
) {
    constructor(tripBicycleStation: TripBicycleStation) : this(
        eventDateTime = tripBicycleStation.trip.dateTime,
        stationName = tripBicycleStation.bicycleStation.name
    )
}
