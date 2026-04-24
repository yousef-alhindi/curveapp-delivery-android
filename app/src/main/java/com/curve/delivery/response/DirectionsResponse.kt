package com.curve.delivery.response


data class DirectionsResponse(
    val routes: List<Route>?=null
)

data class Route(
    val legs: List<Leg>,
    val overview_polyline: OverviewPolyline,
    val bounds: Bounds
)

data class Leg(
    val distance: Distance,
    val duration: Duration,
    val start_address: String,
    val end_address: String,
    val start_location: Location,
    val end_location: Location,
    val steps: List<Step>
)

data class Distance(
    val text: String,
    val value: Int
)

data class Duration(
    val text: String,
    val value: Int
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Step(
    val distance: Distance,
    val duration: Duration,
    val start_location: Location,
    val end_location: Location,
    val polyline: Polyline,
    val html_instructions: String
)

data class Polyline(
    val points: String
)

data class OverviewPolyline(
    val points: String
)

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

