package com.example.features.navigation.domain.mappers

import android.util.Log
import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationNavigationDomainModel
import com.example.features.navigation.domain.models.CurrentLocationStateNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationMapDataNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationInfoNavigationDomainModel
import com.example.features.navigation.domain.models.NavigationStateNavigationDomainModel
import com.example.features.navigation.domain.models.RouteNodeNavigationDomainModel
import com.example.features.navigation.domain.models.RouteStepNavigationDomainModel
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.remotedatasources.responses.RouteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline

fun Pair<RouteResponse, RouteResponse>.mapToNavigationRouteNode(startLat: Double, startLon: Double): RouteNodeNavigationDomainModel {

    val walkRoutePolyLine = Polyline()
    val carRoutePolyLine = Polyline()

    val walkSteps: ArrayList<RouteStepNavigationDomainModel> = ArrayList()
    val carSteps: ArrayList<RouteStepNavigationDomainModel> = ArrayList()

    this.component1().routes.forEach {

        val points: MutableList<MutableList<Double>> =
            decodeGeometry(it.geometry)

        Log.d("count", points.size.toString())

        for (point in points) {
            val lat: Double = point[0]
            val lon: Double = point[1]
            walkRoutePolyLine.addPoint(GeoPoint(lat, lon))
            walkSteps.add(
                RouteStepNavigationDomainModel(
                    coordinates = CoordinatesNavigationDomainModel(
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
                RouteStepNavigationDomainModel(
                    coordinates = CoordinatesNavigationDomainModel(
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
    }

    return RouteNodeNavigationDomainModel(
        walkPolyLine = walkRoutePolyLine,
        carPolyLine = carRoutePolyLine,
        coordinate = CoordinatesNavigationDomainModel(startLat, startLon),
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

fun CoordinatesRouteDomainModel.toCoordinatesNavigationDomainModel(): CoordinatesNavigationDomainModel {

    return CoordinatesNavigationDomainModel(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun StateFlow<NavigationStateNavigationDomainModel>.toFlowOfNavigationInfoDomainModel(): Flow<NavigationInfoNavigationDomainModel> {

    return this.map {

        when(it.navigation) {
            is NavigationNavigationDomainModel.Default -> NavigationInfoNavigationDomainModel.Default
            is NavigationNavigationDomainModel.Navigation -> NavigationInfoNavigationDomainModel.NavigationInfo(
                currentStepName = it.navigation.currentRouteStep.name,
                currentStepInstruction = it.navigation.currentRouteStep.instruction,
                currentStepInstructionType = it.navigation.currentRouteStep.type!!,
                prevStepName = it.navigation.prevRouteStep?.name,
                prevStepInstruction = it.navigation.prevRouteStep?.instruction,
                prevStepInstructionType = it.navigation.prevRouteStep?.type
            )
            is NavigationNavigationDomainModel.Arrived -> NavigationInfoNavigationDomainModel.Arrived(
                hasNextDestination = it.navigation.hasNextDestination,
                finalRouteStepName = it.navigation.finalRouteStep.name,
                finalRouteStepInstruction = it.navigation.finalRouteStep.instruction,
                finalRouteStepInstructionType = it.navigation.finalRouteStep.type!!,
            )
        }
    }
}

fun StateFlow<NavigationStateNavigationDomainModel>.toFlowOfNavigationMapDataDomainModel():
        Flow<NavigationMapDataNavigationDomainModel> {

    return this.map {

        when(it.navigation) {
            is NavigationNavigationDomainModel.Default -> NavigationMapDataNavigationDomainModel.Default
            is NavigationNavigationDomainModel.Navigation -> NavigationMapDataNavigationDomainModel.NavigationMapData(
                goal = it.navigation.goal,
                route = it.navigation.routePolyLines
            )
            is NavigationNavigationDomainModel.Arrived -> NavigationMapDataNavigationDomainModel.Arrived
        }
    }
}

fun StateFlow<CurrentLocationStateNavigationDomainModel>.toFlowOfCurrentLocationDomainModel():
        Flow<CurrentLocationNavigationDomainModel> {

    return this.map {

        return@map CurrentLocationNavigationDomainModel(
            it.currentLocation
        )
    }
}