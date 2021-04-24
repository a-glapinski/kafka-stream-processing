package pl.poznan.put.consumer.model

import pl.poznan.put.common.utils.KafkaJsonSerializable

data class DayStationAggregateValue(
    val startCount: Long = 0L,
    val stopCount: Long = 0L,
//    val averageTemperature: Double = 0.0
) : KafkaJsonSerializable {
    fun withIncrementedStartCount(): DayStationAggregateValue =
        this.copy(startCount = this.startCount + 1L)

    fun withIncrementedStopCount(): DayStationAggregateValue =
        this.copy(stopCount = this.stopCount + 1L)
}
