package pl.poznan.put.consumer.model

import org.apache.kafka.streams.kstream.Window
import pl.poznan.put.common.utils.toLocalDateTime
import java.time.LocalDateTime

data class AnomalyReportValue(
    val windowStart: LocalDateTime,
    val windowEnd: LocalDateTime,
    val stationDocksInService: Long ,
    val stoppedButNotStartedTripCount: Long,
    val startedButNotStoppedTripCount: Long,
    val anomalyRatio: Double
) {
    constructor(window: Window, anomalyAggregateValue: AnomalyAggregateValue) : this(
        windowStart = window.start().toLocalDateTime(),
        windowEnd = window.end().toLocalDateTime(),
        stationDocksInService = anomalyAggregateValue.stationDocksInService,
        stoppedButNotStartedTripCount = anomalyAggregateValue.stoppedButNotStartedTripCount,
        startedButNotStoppedTripCount = anomalyAggregateValue.startedButNotStoppedTripCount,
        anomalyRatio = anomalyAggregateValue.anomalyRatio
    )
}
