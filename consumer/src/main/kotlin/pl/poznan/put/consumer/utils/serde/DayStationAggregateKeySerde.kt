package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.model.DayStationAggregateKey

class DayStationAggregateKeySerde : Serde<DayStationAggregateKey> {
    override fun serializer(): Serializer<DayStationAggregateKey> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<DayStationAggregateKey> =
        Deserializer { _, data ->
            objectMapper.readValue<DayStationAggregateKey>(data)
        }
}