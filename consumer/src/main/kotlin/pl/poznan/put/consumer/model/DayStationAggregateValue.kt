package pl.poznan.put.consumer.model

data class DayStationAggregateValue(
    val startCount: Long = 0L,
    val stopCount: Long = 0L,
    val averageTemperature: Double = 0.0
) {
    fun withIncrementedStartCount(): DayStationAggregateValue =
        this.copy(startCount = this.startCount + 1L)

    fun withIncrementedStopCount(): DayStationAggregateValue =
        this.copy(stopCount = this.stopCount + 1L)

    fun withNewAverageTemperature(temperature: Double): DayStationAggregateValue =
        this.copy(
            averageTemperature = ((startCount + stopCount) * averageTemperature + temperature) / (startCount + stopCount + 1)
        )
}
