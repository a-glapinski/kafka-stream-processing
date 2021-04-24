package pl.poznan.put.consumer.utils

import pl.poznan.put.common.model.BicycleStation
import java.io.File

object BicycleStationLoader {
    fun load(inputFilepath: String): Sequence<BicycleStation> =
        File(inputFilepath)
            .let {
                sequence {
                    it.useLines { lines ->
                        lines.drop(1)
                            .forEach { yield(it) }
                    }
                }
            }
            .map { line ->
                line.split(',', limit = 8)
                    .let {
                        BicycleStation(
                            id = it[0].toInt(),
                            name = it[1],
                            totalDocks = it[2].toInt(),
                            docksInService = it[3].toInt(),
                            status = it[4],
                            latitude = it[5].toDouble(),
                            longitude = it[6].toDouble(),
                            location = it[7].removeSurrounding("\"")
                        )
                    }
            }
}