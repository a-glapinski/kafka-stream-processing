package pl.poznan.put.consumer.model

import kotlin.math.abs

data class AnomalyAggregateValue(
    val stationDocksInService: Long = 0,
    val stoppedButNotStartedTripCount: Long = 0,
    val startedButNotStoppedTripCount: Long = 0,
    val anomalyRatio: Double = 0.0
) {
    constructor(stationStartStopCount: StationStartStopCountValue) : this(
        stationDocksInService = stationStartStopCount.docksInService,
        stoppedButNotStartedTripCount = (stationStartStopCount.tripStopCount - stationStartStopCount.tripStartCount)
            .coerceAtLeast(0),
        startedButNotStoppedTripCount = (stationStartStopCount.tripStartCount - stationStartStopCount.tripStopCount)
            .coerceAtLeast(0),
        anomalyRatio = abs(stationStartStopCount.tripStopCount - stationStartStopCount.tripStartCount) /
                stationStartStopCount.docksInService.toDouble()
    )
}
