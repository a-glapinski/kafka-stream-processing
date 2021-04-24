package pl.poznan.put.common.model

import pl.poznan.put.common.utils.KafkaJsonSerializable

data class TripBicycleStation(
    val trip: Trip,
    val bicycleStation: BicycleStation
) : KafkaJsonSerializable