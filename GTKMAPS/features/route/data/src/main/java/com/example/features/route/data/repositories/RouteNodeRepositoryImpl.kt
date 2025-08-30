package com.example.features.route.data.repositories

import android.util.Log
import com.example.features.route.data.mappers.mapToRouteNodeRouteDataSourceDomainModel
import com.example.core.remotedatasources.routedatasource.domain.RouteRemoteDataSource
import com.example.features.route.domain.models.CoordinatesRouteDomainModel
import com.example.features.route.domain.models.RouteNodeRouteDomainModel
import com.example.features.route.domain.repositories.RouteNodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class RouteNodeRepositoryImpl(): RouteNodeRepository {

    private val routeRemoteDataSource: RouteRemoteDataSource by inject(RouteRemoteDataSource::class.java)

    private val routeNodeCoroutineDispatcher = Dispatchers.IO

    override suspend fun getRouteNode(
        placeUUID: String,
        stop1: CoordinatesRouteDomainModel,
        stop2: CoordinatesRouteDomainModel
    ): RouteNodeRouteDomainModel? {

        return withContext(routeNodeCoroutineDispatcher) {

            try {

                return@withContext routeRemoteDataSource.getRouteNode(
                    startLat = stop1.latitude,
                    startLon = stop1.longitude,
                    endLat = stop2.latitude,
                    endLon = stop2.longitude
                ).mapToRouteNodeRouteDataSourceDomainModel(
                    placeUUID = placeUUID,
                    startLat = stop2.latitude,
                    startLon = stop2.longitude
                )

            } catch (e: Exception) {
                Log.e("getRouteNode", "getting route node: error, exception", e)
            }

            return@withContext null
        }
    }

}