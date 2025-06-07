package com.example.travel_mate.domain

import com.example.travel_mate.data.Contributor
import com.example.travel_mate.data.CurrentTripRepositoryImpl.CurrentTrip
import com.example.travel_mate.data.Place
import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import kotlinx.coroutines.flow.StateFlow

interface CurrentTripRepository {

    val currentTripState: StateFlow<CurrentTrip>

    suspend fun initDefaultTrip()

    suspend fun initAddUpdateTrip(startPlace: Place, places: List<Place>)

    suspend fun resetCurrentTrip()

    suspend fun setCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier)

    suspend fun getCurrentTrip(): Pair<Trip?, TripIdentifier?>

    fun getCurrentTripContributors(): Map<String,Contributor>

    suspend fun setCurrentTripContributors(contributors: Map<String, Contributor>)

}