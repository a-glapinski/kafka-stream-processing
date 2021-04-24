package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.model.TripBicycleStation
import pl.poznan.put.common.utils.objectMapper

class TripBicycleStationSerde : Serde<TripBicycleStation> {
    override fun serializer(): Serializer<TripBicycleStation> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<TripBicycleStation> =
        Deserializer { _, data ->
            objectMapper.readValue<TripBicycleStation>(data)
        }
}