package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.model.StationStartStopCountValue

class StationStartStopCountValueSerde : Serde<StationStartStopCountValue> {
    override fun serializer(): Serializer<StationStartStopCountValue> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<StationStartStopCountValue> =
        Deserializer { _, data ->
            objectMapper.readValue<StationStartStopCountValue>(data)
        }
}