package pl.poznan.put.consumer

import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.Suppressed
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig.unbounded
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.WindowStore
import pl.poznan.put.common.model.BicycleStation
import pl.poznan.put.common.model.TripBicycleStation
import pl.poznan.put.consumer.PropertiesKeys.BICYCLE_STATIONS_FILEPATH
import pl.poznan.put.consumer.PropertiesKeys.DOCKS_ANOMALY_RATIO_IN_PERCENTS
import pl.poznan.put.consumer.PropertiesKeys.DURATION_IN_MINUTES
import pl.poznan.put.consumer.PropertiesKeys.INPUT_TOPIC_NAME
import pl.poznan.put.consumer.model.AnomalyReportKey
import pl.poznan.put.consumer.model.AnomalyReportValue
import pl.poznan.put.consumer.model.ConsumerTripStationKey
import pl.poznan.put.consumer.model.DayStationAggregateKey
import pl.poznan.put.consumer.model.DayStationAggregateValue
import pl.poznan.put.consumer.model.StationStartStopCountKey
import pl.poznan.put.consumer.model.StationStartStopCountValue
import pl.poznan.put.consumer.model.WindowedDayStationAggregateKey
import pl.poznan.put.consumer.utils.BicycleStationLoader
import pl.poznan.put.consumer.utils.serde.AnomalyReportKeySerde
import pl.poznan.put.consumer.utils.serde.AnomalyReportValueSerde
import pl.poznan.put.consumer.utils.serde.DayStationAggregateKeySerde
import pl.poznan.put.consumer.utils.serde.DayStationAggregateValueSerde
import pl.poznan.put.consumer.utils.serde.StationStartStopCountKeySerde
import pl.poznan.put.consumer.utils.serde.StationStartStopCountValueSerde
import pl.poznan.put.consumer.utils.serde.TripBicycleStationSerde
import pl.poznan.put.consumer.utils.serde.TripSerde
import pl.poznan.put.consumer.utils.serde.WindowedDayStationAggregateKeySerde
import pl.poznan.put.consumer.utils.toHumanReadableTimestampString
import java.time.Duration
import java.util.*

class TripConsumer(
    private val properties: Properties
) {
    init {
        properties.apply {
            set(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, TripBicycleStationSerde::class.java)
            set(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TripEventTimestampExtractor::class.java)
        }
    }

    private val inputTopicName: String
        get() = properties.getProperty(INPUT_TOPIC_NAME)
    private val durationInMinutes: Long
        get() = properties.getProperty(DURATION_IN_MINUTES).toLong()
    private val docksAnomalyRatio: Double
        get() = properties.getProperty(DOCKS_ANOMALY_RATIO_IN_PERCENTS).toDouble() / 100.0
    private val bicycleStationsFilepath: String
        get() = properties.getProperty(BICYCLE_STATIONS_FILEPATH)

    private val bicycleStations = BicycleStationLoader.load(bicycleStationsFilepath)

    fun consume() {
        val streamsBuilder = StreamsBuilder()

        val tripStationStream: KStream<ConsumerTripStationKey, TripBicycleStation> = streamsBuilder
            .stream(inputTopicName, Consumed.with(StringSerde(), TripSerde()))
            .map { _, trip ->
                val value = TripBicycleStation(trip, getBicycleStation(trip.stationId))
                val key = ConsumerTripStationKey(value)
                KeyValue(key, value)
            }

        val etlTable = tripStationStream
            .groupBy(
                { k, _ -> DayStationAggregateKey(k) },
                Grouped.keySerde(DayStationAggregateKeySerde())
            )
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
                Materialized.`as`<DayStationAggregateKey, DayStationAggregateValue, WindowStore<Bytes, ByteArray>>(
                    "etl-store"
                )
                    .withKeySerde(DayStationAggregateKeySerde()).withValueSerde(DayStationAggregateValueSerde())
                    .withCachingDisabled()
            )
            .suppress(Suppressed.untilWindowCloses(unbounded()))

        etlTable.toStream()
            .foreach { k, v ->
                println("${k.toHumanReadableTimestampString()} : $v")
            }

        val stationsStartStopCountTable = tripStationStream
            .groupBy(
                { k, _ -> StationStartStopCountKey(k) },
                Grouped.keySerde(StationStartStopCountKeySerde())
            )
            .windowedBy(TimeWindows.of(Duration.ofMinutes(durationInMinutes)).advanceBy(Duration.ofMinutes(10)))
            .aggregate(
                ::StationStartStopCountValue,
                { _, value, aggregateValue ->
                    when (value.trip.startStop) {
                        0 -> StationStartStopCountValue(value)
                            .copy(tripStartCount = aggregateValue.tripStartCount + 1L)
                        1 -> StationStartStopCountValue(value)
                            .copy(tripStopCount = aggregateValue.tripStopCount + 1L)
                        else -> StationStartStopCountValue(value)
                            .copy(
                                tripStartCount = aggregateValue.tripStartCount,
                                tripStopCount = aggregateValue.tripStopCount
                            )
                    }
                },
                Materialized.`as`<StationStartStopCountKey, StationStartStopCountValue, WindowStore<Bytes, ByteArray>>(
                    "stations-start-stop-counts-store"
                )
                    .withKeySerde(StationStartStopCountKeySerde()).withValueSerde(StationStartStopCountValueSerde())
                    .withCachingDisabled()
            )
            .suppress(Suppressed.untilWindowCloses(unbounded()))

        val anomaliesTable = stationsStartStopCountTable.toStream()
            .map { k, v ->
                KeyValue(AnomalyReportKey(k), AnomalyReportValue(v))
            }
            .filter { _, v ->
                v.anomalyRatio > docksAnomalyRatio
            }
            .toTable(
                Materialized.`as`<AnomalyReportKey, AnomalyReportValue, KeyValueStore<Bytes, ByteArray>>(
                    "anomaly-store"
                )
                    .withKeySerde(AnomalyReportKeySerde()).withValueSerde(AnomalyReportValueSerde())
                    .withCachingDisabled()
            )

        anomaliesTable.toStream()
            .foreach { k, v ->
                println("$k : $v")
            }

        etlTable.toStream()
            .map { k, v ->
                KeyValue(WindowedDayStationAggregateKey(k), v)
            }
            .to(
                "etl-topic", Produced.with(WindowedDayStationAggregateKeySerde(), DayStationAggregateValueSerde())
            )

        anomaliesTable.toStream()
            .to("anomaly-topic")

        val topology = streamsBuilder.build()
        KafkaStreams(topology, properties).run {
            cleanUp()
            start()
        }
    }

    private fun getBicycleStation(bicycleStationId: Int): BicycleStation =
        bicycleStations
            .firstOrNull { it.id == bicycleStationId }
            ?: error("Station with id = $bicycleStationId not found.")
}