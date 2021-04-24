package pl.poznan.put.common.model

import com.fasterxml.jackson.annotation.JsonUnwrapped
import pl.poznan.put.common.utils.KafkaJsonSerializable

data class TripBicycleStation(
    @JsonUnwrapped(prefix = "trip")
    val trip: Trip,

    @JsonUnwrapped(prefix = "station")
    val bicycleStation: BicycleStation
) : KafkaJsonSerializable