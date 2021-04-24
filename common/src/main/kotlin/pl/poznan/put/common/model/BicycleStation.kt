package pl.poznan.put.common.model

data class BicycleStation(
    val id: Int,
    val name: String,
    val totalDocks: Int,
    val docksInService: Int,
    val status: String,
    val latitude: Double,
    val longitude: Double,
    val location: String
)