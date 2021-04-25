package pl.poznan.put.consumer.utils.serde

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.model.AnomalyReportValue

class AnomalyReportValueSerde : Serde<AnomalyReportValue> {
    override fun serializer(): Serializer<AnomalyReportValue> =
        Serializer { _, data ->
            objectMapper.writeValueAsBytes(data)
        }

    override fun deserializer(): Deserializer<AnomalyReportValue> =
        Deserializer { _, data ->
            objectMapper.readValue<AnomalyReportValue>(data)
        }
}
