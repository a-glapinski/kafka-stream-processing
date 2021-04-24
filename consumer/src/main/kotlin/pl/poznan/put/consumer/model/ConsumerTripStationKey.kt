package pl.poznan.put.consumer.model

import pl.poznan.put.common.model.TripBicycleStation
import pl.poznan.put.common.utils.KafkaJsonSerializable
import java.time.LocalDateTime

data class ConsumerTripStationKey(
    val eventDateTime: LocalDateTime,
    val stationId: Int,
    val stationName: String
) {
    constructor(tripBicycleStation: TripBicycleStation) : this(
        eventDateTime = tripBicycleStation.trip.dateTime,
        stationId = tripBicycleStation.bicycleStation.id,
        stationName = tripBicycleStation.bicycleStation.name
    )
}
