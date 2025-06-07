package com.example.travel_mate.data

import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import com.example.travel_mate.domain.CurrentTripRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class CurrentTripRepositoryImpl: CurrentTripRepository {

    private val _currentTripState = MutableStateFlow(CurrentTrip())
    override val currentTripState: StateFlow<CurrentTrip> = _currentTripState.asStateFlow()

    private val currentTripCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun initDefaultTrip() {

        withContext(currentTripCoroutineDispatcher) {

            val defaultTrip = Trip()
            val defaultIdentifier = TripIdentifier(uuid = defaultTrip.uUID)

            _currentTripState.update {

                it.copy(
                    trip = defaultTrip,
                    tripIdentifier = defaultIdentifier
                )
            }
        }
    }

    override suspend fun initAddUpdateTrip(startPlace: Place, places: List<Place>) {

        withContext(currentTripCoroutineDispatcher) {

            _currentTripState.update {

                val newTrip = it.trip ?: Trip()
                val newIdentifier = it.tripIdentifier ?: TripIdentifier(uuid = newTrip.uUID)
                it.copy(
                    trip = newTrip.copy(
                        startPlace = startPlace,
                        places = places
                    ),
                    tripIdentifier = newIdentifier
                )
            }
        }
    }

    override suspend fun resetCurrentTrip() {

        withContext(currentTripCoroutineDispatcher) {

            _currentTripState.update {
                it.copy(
                    trip = null,
                    tripIdentifier = null
                )
            }
        }
    }

    override suspend fun setCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        withContext(currentTripCoroutineDispatcher) {

            _currentTripState.update {

                it.copy(

                    trip = trip,
                    tripIdentifier = tripIdentifier
                )
            }
        }
    }

    override suspend fun getCurrentTrip(): Pair<Trip?, TripIdentifier?>
    = Pair(_currentTripState.value.trip,_currentTripState.value.tripIdentifier)

    override fun getCurrentTripContributors(): Map<String, Contributor> = _currentTripState.value.tripIdentifier?.contributors ?: emptyMap()


    //todo do something with this one
    override suspend fun setCurrentTripContributors(contributors: Map<String, Contributor>) {

        withContext(currentTripCoroutineDispatcher) {

            val contributors = contributors.filter { it.value.selected == true }

            _currentTripState.update {

                it.copy(
                    tripIdentifier = it.tripIdentifier?.copy(

                        contributorUIDs = contributors.mapValues { true },
                        contributors = contributors
                    )
                )
            }
        }
    }

    data class CurrentTrip(
        val trip: Trip? = null,
        val tripIdentifier: TripIdentifier? = null
    )
}