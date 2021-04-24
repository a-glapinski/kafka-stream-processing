package pl.poznan.put

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.state.Stores
import pl.poznan.put.PropertiesKeys.BICYCLE_STATIONS_FILEPATH
import pl.poznan.put.PropertiesKeys.DURATION_IN_MINUTES
import pl.poznan.put.PropertiesKeys.WORKING_STATIONS_RATIO
import pl.poznan.put.model.ConsumerTripStationKey
import pl.poznan.put.model.Trip
import pl.poznan.put.model.TripBicycleStation
import pl.poznan.put.utils.BicycleStationLoader
import pl.poznan.put.utils.objectMapper
import java.util.*

class TripConsumer(
    private val properties: Properties
) {
    init {
        properties.apply {
            set(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, StringSerde::class.java)
            set(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, StringSerde::class.java)
//            set(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TripEventTimeExtractor::class.java)
        }
    }

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
            .stream("trips-topic", Consumed.with(StringSerde(), StringSerde()))
            .map { _, v ->
                val trip = objectMapper.readValue<Trip>(v)
                val value = bicycleStations
                    .firstOrNull { it.id == trip.stationId }
                    ?.let { TripBicycleStation(trip, it) }
                    ?: error("Station with id = ${trip.stationId} not found.")
                val key = ConsumerTripStationKey(value)
                KeyValue(key, value)
            }

        tripStream.foreach { k, v ->
            println("$k : $v")
        }

        val storeBuilder = Stores.keyValueStoreBuilder(
            Stores.persistentKeyValueStore("state-store"),
            StringSerde(), StringSerde()
        )
        streamsBuilder.addStateStore(storeBuilder)

        val topology = streamsBuilder.build()

        KafkaStreams(topology, properties).run {
            cleanUp()
            start()
        }
    }
}