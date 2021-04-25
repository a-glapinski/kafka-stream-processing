package pl.poznan.put.consumer.model

import org.apache.kafka.streams.kstream.Windowed
import pl.poznan.put.common.utils.toLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime

data class WindowedDayStationAggregateKey(
    val day: LocalDate,
    val stationId: Int,
    val stationName: String,
    val windowStart: LocalDateTime,
    val windowEnd: LocalDateTime
) {
    constructor(dayStationAggregateKey: Windowed<DayStationAggregateKey>) : this(
        day = dayStationAggregateKey.key().day,
        stationId = dayStationAggregateKey.key().stationId,
        stationName = dayStationAggregateKey.key().stationName,
        windowStart = dayStationAggregateKey.window().start().toLocalDateTime(),
        windowEnd = dayStationAggregateKey.window().end().toLocalDateTime()
    )
}