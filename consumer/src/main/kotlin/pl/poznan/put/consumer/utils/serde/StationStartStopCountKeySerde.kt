package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.model.StationStartStopCountKey

class StationStartStopCountKeySerde : Serde<StationStartStopCountKey> {
    override fun serializer(): Serializer<StationStartStopCountKey> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<StationStartStopCountKey> =
        Deserializer { _, data ->
            objectMapper.readValue<StationStartStopCountKey>(data)
        }
}