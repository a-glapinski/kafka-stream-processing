package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.model.Trip
import pl.poznan.put.common.utils.objectMapper

class TripSerde : Serde<Trip> {
    override fun serializer(): Serializer<Trip> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<Trip> =
        Deserializer { _, data ->
            objectMapper.readValue<Trip>(data)
        }
}