package pl.poznan.put.consumer.model

import pl.poznan.put.common.utils.KafkaJsonSerializable

data class DayStationAggregate(
    val dayStationAggregateKey: DayStationAggregateKey,
    val departureCount: Long,
    val arrivalCount: Long,
    val averageTemperature: Double
) : KafkaJsonSerializable
