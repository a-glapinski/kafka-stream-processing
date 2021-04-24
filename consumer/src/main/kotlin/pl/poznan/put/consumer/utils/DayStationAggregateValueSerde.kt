package pl.poznan.put.consumer.utils

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.model.DayStationAggregateValue

class DayStationAggregateValueSerde : Serde<DayStationAggregateValue> {
    override fun serializer(): Serializer<DayStationAggregateValue> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<DayStationAggregateValue> =
        Deserializer { _, data ->
            objectMapper.readValue<DayStationAggregateValue>(data)
        }
}