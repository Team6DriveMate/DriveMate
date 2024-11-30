package com.jeoktoma.drivemate

data class RouteRequest(
    val start_location: Point,
    val end_location: Point
)
data class RouteResponse(
    val totalDistance: Int,
    val totalTime: Int,
    val route: Route
)

data class Route(
    val segments: List<Segment>
)

data class Segment(
    val segmentIndex: String?,
    val startPoint: Point,
    val endPoint: Point,
    val path: List<Point>,
    val distance: Int,
    val time: Double,
    val roadName: String,
    val traffic: String,
    val roadType: String?
)

data class Point(
    val lat: Double,
    val lng: Double
)


data class CoordRequest(val road_address: String)
data class CoordResponse(val lat: Double, val lng: Double)

data class CompleteRequest(val start_location: Point, val end_location: Point,
                           val stopover_location: List<Point>?)

data class CompleteResponse(val success:Boolean)

data class GetRouteResponse(
    val startTime: String,
    val endTime: String,
    val totalDistance: Int,
    val totalTime: Int,
    val route: GetRoute
)

data class GetRoute(
    val sections: List<GetSection>
)

data class GetSection(
    val sectionIndex: String?,
    val sectionName: String,
    val segments: List<GetSegment>
)

data class GetSegment(
    val segmentIndex: String?,
    val startPoint: Point,
    val endPoint: Point,
    val path: List<Point>,
    val distance: Int,
    val time: Double,
    val roadName: String,
    val traffic: String
)
