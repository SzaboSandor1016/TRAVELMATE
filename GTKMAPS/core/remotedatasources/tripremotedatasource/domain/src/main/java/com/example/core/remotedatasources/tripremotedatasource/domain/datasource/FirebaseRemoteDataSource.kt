package com.example.core.remotedatasources.tripremotedatasource.domain.datasource

import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripIdentifierRemoteEntityModel
import com.example.core.remotedatasources.tripremotedatasource.domain.models.TripRemoteEntityModel

/** [FirebaseRemoteDataSource]
 * interface for the [com.example.data.datasources.FirebaseRemoteDataSourceImpl] class
 */
interface FirebaseRemoteDataSource {

    suspend fun uploadTrip(trip: TripRemoteEntityModel, firebaseIdentifier: TripIdentifierRemoteEntityModel)

    suspend fun deleteTrip(uuid: String)

    suspend fun findTripById(uuid: String): TripRemoteEntityModel?

    suspend fun fetchMyTrips(uid: String): List<TripIdentifierRemoteEntityModel>

    suspend fun fetchContributedTrips(uid: String): List<TripIdentifierRemoteEntityModel>

    suspend fun deleteTripsByUserUid(uid: String)

    suspend fun deleteUidFromContributedTrips(uid: String, tripUUID: String)
}