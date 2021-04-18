package pl.poznan.put.utils

interface KafkaJsonSerializable {
    fun toJsonString(): String =
        objectMapper.writeValueAsString(this)
}