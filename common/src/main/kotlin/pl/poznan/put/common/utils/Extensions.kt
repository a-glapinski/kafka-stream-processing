package pl.poznan.put.common.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toEpochMilli(zone: ZoneId = ZoneId.systemDefault()): Long =
    this.atZone(zone).toInstant().toEpochMilli()

fun Long.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDateTime()