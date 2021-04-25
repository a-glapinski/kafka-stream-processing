package pl.poznan.put.consumer.model

import org.apache.kafka.streams.kstream.Windowed
import pl.poznan.put.common.utils.toLocalDateTime
import java.time.LocalDateTime

data class AnomalyReportKey(
    val stationId: Int,
    val stationName: String,
    val windowStart: LocalDateTime,
    val windowEnd: LocalDateTime,
) {
    constructor(stationStartStopCountKey: Windowed<StationStartStopCountKey>) : this(
        stationId = stationStartStopCountKey.key().stationId,
        stationName = stationStartStopCountKey.key().stationName,
        windowStart = stationStartStopCountKey.window().start().toLocalDateTime(),
        windowEnd = stationStartStopCountKey.window().end().toLocalDateTime()
    )
}