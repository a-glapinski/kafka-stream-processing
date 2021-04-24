package pl.poznan.put.common.utils

interface KafkaJsonSerializable {
    fun toJsonString(): String =
        objectMapper.writeValueAsString(this)
}