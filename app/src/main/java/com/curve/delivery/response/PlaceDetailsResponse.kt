package com.curve.delivery.response

data class PlaceDetailsResponse(
    val result: PlaceDetails?,
    val status: String
)

data class PlaceDetails(
    val geometry: Geometry
)

data class Geometry(
    val location: Locations
)

data class Locations(
    val lat: Double,
    val lng: Double
)