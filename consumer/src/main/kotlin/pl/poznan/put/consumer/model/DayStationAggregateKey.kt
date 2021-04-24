package pl.poznan.put.consumer.model

import java.time.LocalDate

data class DayStationAggregateKey(
    val day: LocalDate,
    val stationId: Int,
    val stationName: String
) {
    constructor(consumerTripStationKey: ConsumerTripStationKey) : this(
        day = consumerTripStationKey.eventDateTime.toLocalDate(),
        stationId = consumerTripStationKey.stationId,
        stationName = consumerTripStationKey.stationName
    )
}
