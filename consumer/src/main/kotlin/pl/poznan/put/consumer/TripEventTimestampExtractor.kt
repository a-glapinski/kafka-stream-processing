package pl.poznan.put.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor
import pl.poznan.put.common.model.Trip
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.common.utils.toEpochMilli

class TripEventTimestampExtractor : TimestampExtractor {
    override fun extract(
        record: ConsumerRecord<Any, Any>,
        partitionTime: Long
    ): Long {
        var timestamp = -1L
        val value = record.value()

        if (value is String) {
            val trip = objectMapper.readValue<Trip>(value)
            timestamp = trip.dateTime.toEpochMilli()
        }

        return timestamp
    }
}