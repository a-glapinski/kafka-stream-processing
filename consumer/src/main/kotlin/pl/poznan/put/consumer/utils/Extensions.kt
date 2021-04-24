package pl.poznan.put.consumer.utils

import org.apache.kafka.streams.kstream.Windowed
import pl.poznan.put.common.utils.toLocalDateTime

fun <K> Windowed<K>.toHumanReadableTimestampString(): String {
    return "[" + key() + "@" + window().start().toLocalDateTime() + "/" + window().end().toLocalDateTime() + "]"
}