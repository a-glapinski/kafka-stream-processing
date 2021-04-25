package pl.poznan.put.consumer.model

data class StationStartStopCountKey(
    val stationId: Int,
    val stationName: String
) {
    constructor(consumerTripStationKey: ConsumerTripStationKey) : this(
        stationId = consumerTripStationKey.stationId,
        stationName = consumerTripStationKey.stationName
    )
}
