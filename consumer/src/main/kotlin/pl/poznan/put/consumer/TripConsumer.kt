package pl.poznan.put.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.WindowStore
import pl.poznan.put.common.model.Trip
import pl.poznan.put.common.model.TripBicycleStation
import pl.poznan.put.common.utils.objectMapper
import pl.poznan.put.consumer.PropertiesKeys.BICYCLE_STATIONS_FILEPATH
import pl.poznan.put.consumer.PropertiesKeys.DURATION_IN_MINUTES
import pl.poznan.put.consumer.PropertiesKeys.INPUT_TOPIC_NAME
import pl.poznan.put.consumer.PropertiesKeys.WORKING_STATIONS_RATIO
import pl.poznan.put.consumer.model.ConsumerTripStationKey
import pl.poznan.put.consumer.model.DayStationAggregateKey
import pl.poznan.put.consumer.model.DayStationAggregateValue
import pl.poznan.put.consumer.utils.BicycleStationLoader
import pl.poznan.put.consumer.utils.serde.DayStationAggregateKeySerde
import pl.poznan.put.consumer.utils.serde.DayStationAggregateValueSerde
import pl.poznan.put.consumer.utils.serde.TripBicycleStationSerde
import pl.poznan.put.consumer.utils.toHumanReadableTimestampString
import java.time.Duration
import java.util.*

class TripConsumer(
    private val properties: Properties
) {
    init {
        properties.apply {
            set(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, DayStationAggregateKeySerde::class.java)
            set(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, TripBicycleStationSerde::class.java)
            set(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TripEventTimestampExtractor::class.java)
        }
    }

    private val inputTopicName: String
        get() = properties.getProperty(INPUT_TOPIC_NAME)
    private val durationInMinutes: Long
        get() = properties.getProperty(DURATION_IN_MINUTES).toLong()
    private val workingStationsRatio: Double
        get() = properties.getProperty(WORKING_STATIONS_RATIO).toDouble()
    private val bicycleStationsFilepath: String
        get() = properties.getProperty(BICYCLE_STATIONS_FILEPATH)

    private val bicycleStations = BicycleStationLoader.load(bicycleStationsFilepath)

    fun consume() {
        val streamsBuilder = StreamsBuilder()

        val tripStream: KStream<ConsumerTripStationKey, TripBicycleStation> = streamsBuilder
            .stream(inputTopicName, Consumed.with(StringSerde(), StringSerde()))
            .map { _, v ->
                val trip = objectMapper.readValue<Trip>(v)
                val value = bicycleStations
                    .firstOrNull { it.id == trip.stationId }
                    ?.let { TripBicycleStation(trip, it) }
                    ?: error("Station with id = ${trip.stationId} not found.")
                val key = ConsumerTripStationKey(value)
                KeyValue(key, value)
            }

        val etlTable = tripStream
            .groupBy { k, _ -> DayStationAggregateKey(k) }
            .windowedBy(TimeWindows.of(Duration.ofDays(1)).advanceBy(Duration.ofMinutes(10)))
            .aggregate(
                ::DayStationAggregateValue,
                { _, value, aggregateValue ->
                    when (value.trip.startStop) {
                        0 -> aggregateValue
                            .withIncrementedStartCount()
                            .withNewAverageTemperature(value.trip.temperature)
                        1 -> aggregateValue
                            .withIncrementedStopCount()
                            .withNewAverageTemperature(value.trip.temperature)
                        else -> aggregateValue.withNewAverageTemperature(value.trip.temperature)
                    }
                },
                Materialized.with<DayStationAggregateKey, DayStationAggregateValue, WindowStore<Bytes, ByteArray>>(
                    DayStationAggregateKeySerde(), DayStationAggregateValueSerde()
                ).withCachingDisabled()
            )

        etlTable
            .toStream()
            .foreach { k, v ->
                println("${k.toHumanReadableTimestampString()} : $v")
            }

        val topology = streamsBuilder.build()

        KafkaStreams(topology, properties).run {
            cleanUp()
            start()
        }
    }
}