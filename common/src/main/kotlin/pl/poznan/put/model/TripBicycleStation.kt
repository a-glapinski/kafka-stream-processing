package pl.poznan.put.model

import com.fasterxml.jackson.annotation.JsonUnwrapped
import pl.poznan.put.utils.KafkaJsonSerializable

data class TripBicycleStation(
    @JsonUnwrapped(prefix = "trip")
    val trip: Trip,

    @JsonUnwrapped(prefix = "station")
    val bicycleStation: BicycleStation
) : KafkaJsonSerializable