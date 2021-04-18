package pl.poznan.put.utils

interface JsonSerializable {
    fun toJsonString(): String =
        objectMapper.writeValueAsString(this)
}