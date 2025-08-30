package com.example.features.navigation.data.repositories

import android.util.Log
import com.example.core.remotedatasources.routedatasource.domain.RouteRemoteDataSource
import com.example.features.navigation.domain.mappers.mapToNavigationRouteNode
import com.example.features.navigation.domain.models.CoordinatesNavigationDomainModel
import com.example.features.navigation.domain.models.RouteNodeNavigationDomainModel
import com.example.features.navigation.domain.repositories.RouteNodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class RouteNodeRepositoryImpl(): RouteNodeRepository {

    private val routeRemoteDataSource: RouteRemoteDataSource by inject(RouteRemoteDataSource::class.java)

    private val routeNodeCoroutineDispatcher = Dispatchers.IO

    override suspend fun getRouteNode(
        stop1: CoordinatesNavigationDomainModel,
        stop2: CoordinatesNavigationDomainModel
    ): RouteNodeNavigationDomainModel? {

        return withContext(routeNodeCoroutineDispatcher) {

            try {

                return@withContext routeRemoteDataSource.getRouteNode(
                    startLat = stop1.latitude,
                    startLon = stop1.longitude,
                    endLat = stop2.latitude,
                    endLon = stop2.longitude
                ).mapToNavigationRouteNode(
                    startLat = stop1.latitude,
                    startLon = stop1.longitude
                )

            } catch (e: Exception) {
                Log.e("getRouteNode", "getting route node: error, exception", e)
            }

            return@withContext null
        }
    }

}