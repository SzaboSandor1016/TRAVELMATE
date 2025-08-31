package com.example.features.route.data.mappers

import android.util.Log
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.RouteNodeRouteDomainModel
import com.example.features.route.domain.models.RouteStateRouteDomainModel
import com.example.features.route.domain.models.RouteStepRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoNodeRouteDomainModel
import com.example.features.route.domain.models.info.RouteInfoRouteDomainModel
import com.example.features.route.domain.models.mapdata.RouteMapDataRouteDomainModel
import com.example.remotedatasources.responses.RouteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

fun StateFlow<RouteStateRouteDomainModel>.toFlowOfRouteInfo(): Flow<RouteInfoRouteDomainModel> {
    return this.map { routeState ->
        RouteInfoRouteDomainModel(
            infoNodes = routeState.route.routeNodes.map { it.toRouteInfoNode(routeState.route.transportMode) },
            transportMode = routeState.route.transportMode,
            fullWalkDuration = routeState.route.fullWalkDuration,
            fullCarDuration = routeState.route.fullCarDuration,
            fullWalkDistance = routeState.route.fullWalkDistance,
            fullCarDistance = routeState.route.fullCarDistance
        )
    }
}

fun RouteNodeRouteDomainModel.toRouteInfoNode(transportMode: String): RouteInfoNodeRouteDomainModel {

    val duration = if(transportMode == "foot-walking") this.walkDuration else this.carDuration
    val distance = if(transportMode == "foot-walking") this.walkDistance else this.carDistance

    return RouteInfoNodeRouteDomainModel(
        placeUUID = this.placeUUID,
        name = this.name,
        coordinates = this.coordinate,
        duration = duration,
        distance = distance,
    )
}

fun StateFlow<RouteStateRouteDomainModel>.toFlowOfRouteMapData(): Flow<RouteMapDataRouteDomainModel> {
    return this.map { routeState ->

        val polylines: List<Polyline> = when(routeState.route.transportMode) {
            "driving-car" -> routeState.route.routeNodes.map { it.carPolyLine }
            else -> routeState.route.routeNodes.map { it.walkPolyLine }
        }

        RouteMapDataRouteDomainModel(
            polylines = polylines
        )
    }
}

/** [mapToRouteNodeRouteDataSourceDomainModel]
 * process the response of a OpenRouteService network request
 * create [com.example.travel_mate.data.RouteNode] from the [com.example.travel_mate.data.RouteResponse]
 */
fun Pair<RouteResponse, RouteResponse>.mapToRouteNodeRouteDataSourceDomainModel(placeUUID: String,startLat: Double, startLon: Double): RouteNodeRouteDomainModel {

    val walkRoutePolyLine = Polyline()
    val carRoutePolyLine = Polyline()

    val walkSteps: ArrayList<RouteStepRouteDomainModel> = ArrayList()
    val carSteps: ArrayList<RouteStepRouteDomainModel> = ArrayList()

    var distanceWalk = 0
    var distanceCar = 0

    var durationWalk = 0
    var durationCar = 0

    this.component1().routes.forEach {

        val points: MutableList<MutableList<Double>> =
            decodeGeometry(it.geometry)

        Log.d("count", points.size.toString())

        for (point in points) {
            val lat: Double = point[0]
            val lon: Double = point[1]
            walkRoutePolyLine.addPoint(GeoPoint(lat, lon))
            walkSteps.add(
                RouteStepRouteDomainModel(
                    coordinates = CoordinatesRouteDomainModel(
                        latitude = lat,
                        longitude = lon
                    )
                )
            )
        }

        for (step in it.segments[0].steps) {

            walkSteps[step.wayPoints[0]!!] = walkSteps[step.wayPoints[0]!!].copy(
                distance = step.distance.toInt(),
                duration = step.duration.toInt(),
                name = step.name,
                instruction = step.instruction,
                type = step.type
            )

        }

        distanceWalk = it.summary.distance.toInt()

        durationWalk = (it.summary.duration / 60).toInt()

    }

    this.component2().routes.forEach {

        val points: MutableList<MutableList<Double>> =
            decodeGeometry(it.geometry)

        Log.d("count", points.size.toString())

        for (point in points) {
            val lat: Double = point[0]
            val lon: Double = point[1]
            carRoutePolyLine.addPoint(GeoPoint(lat, lon))
            carSteps.add(
                RouteStepRouteDomainModel(
                    coordinates = CoordinatesRouteDomainModel(
                        latitude = lat,
                        longitude = lon
                    )
                )
            )
        }
        for (step in it.segments[0].steps) {
            carSteps[step.wayPoints[0]] = carSteps[step.wayPoints[0]].copy(
                distance = step.distance.toInt(),
                duration = step.duration.toInt(),
                name = step.name,
                instruction = step.instruction,
                type = step.type
            )

        }

        distanceCar = it.summary.distance.toInt()

        durationCar = (it.summary.duration / 60).toInt()

    }

    return RouteNodeRouteDomainModel(
        placeUUID = placeUUID,
        walkPolyLine = walkRoutePolyLine,
        carPolyLine = carRoutePolyLine,
        walkDistance = distanceWalk,
        walkDuration = durationWalk,
        carDistance = distanceCar,
        carDuration = durationCar,
        coordinate = CoordinatesRouteDomainModel(startLat, startLon),
        carRouteSteps = carSteps.toList(),
        walkRouteSteps = walkSteps.toList()
    )
}

/** [decodeGeometry]
 * decodes the geometry "encoded polyline" [String]
 * containing the coordinates of the [com.example.travel_mate.data.Route]'s polyline returned
 * by the [getRouteNode] request.
 *
 */
private fun decodeGeometry(encodedGeometry: String): MutableList<MutableList<Double>> {

    val geometry: MutableList<MutableList<Double>> = ArrayList()
    var index = 0
    val len = encodedGeometry.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var result = 1
        var shift = 0
        var b: Int
        do {
            b = encodedGeometry[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lat += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        result = 1
        shift = 0
        do {
            b = encodedGeometry[index++].code - 63 - 1
            result += b shl shift
            shift += 5
        } while (b >= 0x1f)
        lng += if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)

        val point: MutableList<Double> = ArrayList()
        point.add(lat / 1E5)
        point.add(lng / 1E5)
        geometry.add(point)
        Log.d("coordinates", point[0].toString() + " , " + point[1].toString())
    }

    return geometry
}