package pl.poznan.put.consumer.model

import pl.poznan.put.common.model.TripBicycleStation

data class StationStartStopCountValue(
    val docksInService: Long = 0,
    val tripStartCount: Long = 0,
    val tripStopCount: Long = 0
) {
    constructor(tripBicycleStation: TripBicycleStation) : this(
        docksInService = tripBicycleStation.bicycleStation.docksInService.toLong()
    )
}