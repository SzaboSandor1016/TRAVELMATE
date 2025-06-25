package com.example.travel_mate.data

import android.util.Log
import com.example.travel_mate.domain.RouteNodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class RouteNodeRepositoryImpl(): RouteNodeRepository {

    private val routeRemoteDataSource: RouteRemoteDataSource by inject(RouteRemoteDataSource::class.java)

    private val routeNodeCoroutineDispatcher = Dispatchers.IO

    override suspend fun getRouteNode(
        pointStart: Coordinates,
        pointEnd: Coordinates
    ): RouteNode {

        return withContext(routeNodeCoroutineDispatcher) {

            try {

                return@withContext routeRemoteDataSource.getRouteNode(
                    pointStart = pointStart,
                    pointEnd = pointEnd
                )

            } catch (e: Exception) {
                Log.e("getRouteNode", "getting route node: error, exception", e)
            }

            return@withContext RouteNode()
        }
    }

}