package com.example.features.savetrip.data.repository

import android.util.Log
import com.example.core.auth.domain.FirebaseAuthenticationSource
import com.example.core.database.domain.datasource.RoomLocalDataSource
import com.example.core.remotedatasources.tripremotedatasource.domain.datasource.FirebaseRemoteDataSource
import com.example.core.databse.dao.models.PlaceLocalEntityModel
import com.example.features.savetrip.data.mappers.toFlowOfAssignablePlaces
import com.example.features.savetrip.data.mappers.toFlowOfSelectedContributorsInfoSaveTrip
import com.example.features.savetrip.data.mappers.toFlowOfPlacesOfSelectedDay
import com.example.features.savetrip.data.mappers.toFlowOfSaveTripInfo
import com.example.features.savetrip.data.mappers.toLocalDayOfTripEntityModel
import com.example.features.savetrip.data.mappers.toLocalPlaceEntityModel
import com.example.features.savetrip.data.mappers.toLocalTripEntity
import com.example.features.savetrip.data.mappers.toRemoteTripEntity
import com.example.features.savetrip.data.mappers.toTripIdentifierRemoteEntityModel
import com.example.features.savetrip.domain.models.ContributorSaveTripDomainModel
import com.example.features.savetrip.domain.models.DayOfTripSaveTripDomainModel
import com.example.features.savetrip.domain.models.PlaceSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripDataSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripInfoSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripOptionsSaveTripDomainModel
import com.example.features.savetrip.domain.models.SaveTripStateSaveTripDomainModel
import com.example.features.savetrip.domain.repository.SaveTripRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

class SaveTripRepositoryImpl(
    private val appScope: CoroutineScope
): SaveTripRepository {

    private val roomLocalDataSource: RoomLocalDataSource by inject(RoomLocalDataSource::class.java)

    private val firebaseAuthSource: FirebaseAuthenticationSource by inject(
        FirebaseAuthenticationSource::class.java)

    private val firebaseRemoteDataSource: FirebaseRemoteDataSource by inject(FirebaseRemoteDataSource::class.java)


    private val saveTripCoroutineDispatcher = Dispatchers.IO

    private val userState = firebaseAuthSource.userFlow().mapLatest {

        it
    }.flowOn(
        Dispatchers.Main
    ).stateIn(
        appScope,
        SharingStarted.Eagerly,
        null
    )

    private val _saveTripState = MutableStateFlow(SaveTripStateSaveTripDomainModel())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _selectableContributorsState = userState.mapNotNull { it }.mapLatest { user ->

        val recentContributorsOfUser =
            firebaseAuthSource.getRecentContributorsOfUser(user.uid)

        firebaseAuthSource.getUserPairsByUIds(recentContributorsOfUser)
            .map {
                ContributorSaveTripDomainModel(
                    uid = it.key,
                    username = it.value,
                    selected = false
                )
            }
    }.flowOn(
        Dispatchers.Main
    ).stateIn(
        appScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    private val _assignablePlacesState: MutableStateFlow<List<PlaceSaveTripDomainModel>> = MutableStateFlow(emptyList())
    override fun hasUserSignedIn(): Flow<Boolean> {

        return userState.map {
            it != null
        }
    }

    override fun getSaveTripInfo(): Flow<SaveTripInfoSaveTripDomainModel> {
        return _saveTripState.toFlowOfSaveTripInfo()
    }

    override fun getPlacesOfDayOfTripBy(): Flow<List<PlaceSaveTripDomainModel>> {
        return _saveTripState.toFlowOfPlacesOfSelectedDay()
    }

    override fun getSaveTripContributorsInfo(): Flow<List<ContributorSaveTripDomainModel>> {
        return _saveTripState.toFlowOfSelectedContributorsInfoSaveTrip()
    }

    override fun getSelectableContributorsInfo(): Flow<List<ContributorSaveTripDomainModel>> {
        return _selectableContributorsState
    }

    override fun isContainedInAssignablePlaces(uuid: String): Flow<Boolean> {
        return _assignablePlacesState.map {places ->
            places.any { it.uUID == uuid }
        }
    }

    override fun areAssignablePlacesEmpty(): Flow<Boolean> {
        return _assignablePlacesState.map { it.isEmpty() }
    }

    override fun getAssignablePlaces(): Flow<List<PlaceSaveTripDomainModel>> {

        return _assignablePlacesState.toFlowOfAssignablePlaces()
    }

    override fun areContributorsEmpty(): Boolean = _saveTripState.value.saveTripData.selectedContributorsUIDs.isEmpty()

    override suspend fun initSaveFromSearchWithStartPlace(startPlace: PlaceSaveTripDomainModel) {

        withContext(saveTripCoroutineDispatcher) {

            val userID = userState.value?.uid

            _saveTripState.update {

                it.copy(
                    saveTripData = SaveTripDataSaveTripDomainModel(
                        uUID = UUID.randomUUID().toString(),
                        startPlace = startPlace,
                        creatorUID = userID
                    ),
                    saveTripOptions = SaveTripOptionsSaveTripDomainModel()
                )
            }
        }
    }

    override suspend fun initSaveBySelectingTripToUpdate(
        tripData: SaveTripDataSaveTripDomainModel
    ) {
        withContext(saveTripCoroutineDispatcher) {

            Log.d("initTripUpdate", "executed")

            _saveTripState.update {

                it.copy(
                    saveTripOptions = SaveTripOptionsSaveTripDomainModel(
                        originalDays = tripData.daysOfTrip
                    ),
                    saveTripData = tripData
                )
            }

            Log.d("initTripFromUpdate", tripData.daysOfTrip.size.toString())
        }
    }

    override suspend fun clearAssignablePlaces() {

        withContext(saveTripCoroutineDispatcher) {

            _assignablePlacesState.update {
                emptyList()
            }
        }
    }

    override suspend fun setSelectedDayWith(index: Int) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {
                it.copy(
                    saveTripOptions = it.saveTripOptions.copy(
                        selectedDayPosition = index
                    )
                )
            }
        }
    }

    override suspend fun addRemoveAssignablePlace(place: PlaceSaveTripDomainModel) {

        withContext(saveTripCoroutineDispatcher) {

            _assignablePlacesState.update { assignablePlaces ->

                if (assignablePlaces.any{it.uUID == place.uUID})
                    assignablePlaces.minus(place)
                else assignablePlaces.plus(place)
            }
        }
    }

    override suspend fun assignPlaceToDayOfTrip(placeUUID: String) {

        withContext(saveTripCoroutineDispatcher) {

            val indexOfDay = _saveTripState.value.saveTripOptions.selectedDayPosition

            if (indexOfDay != -1) {

                val place = _assignablePlacesState.value.find { it.uUID == placeUUID }!!

                val days = _saveTripState.value.saveTripData.daysOfTrip.toMutableList()

                days[indexOfDay] = days[indexOfDay].copy(
                    places = days[indexOfDay].places.plus(place)
                )

                _assignablePlacesState.update {
                    it.minus(place)
                }

                _saveTripState.update {
                    it.copy(

                        saveTripData = it.saveTripData.copy(
                            daysOfTrip = days
                        )
                    )
                }

                Log.d("addPlaceToTrip", "Place withUUID: $placeUUID added to $indexOfDay. day Added?: ${_saveTripState.value.saveTripData.daysOfTrip[indexOfDay].places.any { it.uUID == placeUUID}}")
            }
        }
    }

    override suspend fun removePlaceFromDayOfTrip(
        placeUUID: String
    ) {

        withContext(saveTripCoroutineDispatcher) {

            val indexOfDay = _saveTripState.value.saveTripOptions.selectedDayPosition

            val place = _saveTripState.value.saveTripData.daysOfTrip[indexOfDay].places.find { it.uUID == placeUUID }!!

            val days = _saveTripState.value.saveTripData.daysOfTrip.toMutableList()

            days[indexOfDay] = days[indexOfDay].copy(
                places = days[indexOfDay].places.minus(place)
            )

            _assignablePlacesState.update {
                it.plus(place)
            }

            _saveTripState.update {
                it.copy(

                    saveTripData = it.saveTripData.copy(daysOfTrip = days)
                )
            }
        }
    }

    override suspend fun setTripTitle(title: String) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {

                it.copy(
                    saveTripData = it.saveTripData.copy(
                        title = title
                    )
                )
            }
        }
    }

    override suspend fun setTripDate(date: String) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {

                it.copy(
                    saveTripData = it.saveTripData.copy(
                        date = date
                    )
                )
            }
        }
    }

    override suspend fun setTripNote(note: String) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {

                it.copy(
                    saveTripData = it.saveTripData.copy(
                        note = note
                    )
                )
            }
        }
    }

    override suspend fun setTripDetails(title: String, startDate: String?, note: String?) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {

                it.copy(

                    saveTripData = it.saveTripData.copy(
                        title = title,
                        date = startDate,
                        note = note
                    )
                )
            }
        }
    }

    override suspend fun setDaysOfTrip(daysLabels: List<String>) {

        withContext(saveTripCoroutineDispatcher) {

            _assignablePlacesState.update { assignablePlaces ->

                assignablePlaces.plus(_saveTripState.value.saveTripData.daysOfTrip.flatMap { it.places })
            }

            _saveTripState.update { saveTripState ->

                saveTripState.copy(

                    saveTripData = saveTripState.saveTripData.copy(

                        daysOfTrip = daysLabels.map {
                            DayOfTripSaveTripDomainModel(
                                label = it
                            )
                        },
                        numberOfDays = daysLabels.size
                    )
                )
            }
        }
    }

    override suspend fun removeDayFromTrip(index: Int) {

        withContext(saveTripCoroutineDispatcher) {

            val day = _saveTripState.value.saveTripData.daysOfTrip.elementAt(index)

            _assignablePlacesState.update {
                it.plus(day.places)
            }

            _saveTripState.update {
                it.copy(
                    saveTripData = it.saveTripData.copy(
                        daysOfTrip = it.saveTripData.daysOfTrip.minus(day),
                        numberOfDays = it.saveTripData.numberOfDays - 1
                    )
                )
            }
        }
    }
    override suspend fun getNewContributorByUsername(username: String) {

        withContext(saveTripCoroutineDispatcher) {

            val user = firebaseAuthSource.findUserByUsername(
                username = username
            )

            if(user != null) {

                updateRecentContributorsOfUser(
                    newUserID = user.first
                )

                _saveTripState.update {

                    it.copy(

                        saveTripData = it.saveTripData.copy(

                            selectedContributorsUIDs = it.saveTripData.selectedContributorsUIDs.plus(
                                Pair(user.first, true)
                            ),
                            selectedContributorsParameters = it.saveTripData.selectedContributorsParameters.plus(
                                Pair(
                                    user.first,
                                    ContributorSaveTripDomainModel(
                                        uid = user.first,
                                        username = user.second,
                                        selected = true
                                    )
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    private suspend fun updateRecentContributorsOfUser(
        newUserID: String
    ) {

        withContext(saveTripCoroutineDispatcher) {

            val currentUserID = userState.value?.uid

            val newContributorsOfUser = _selectableContributorsState.value.associate {

                Pair(it.uid, true)
            }.plus(
                Pair(newUserID, true)
            )

            if (currentUserID != null)
                firebaseAuthSource.setRecentContributors(
                    currentUserID,
                    newContributorsOfUser
                )
        }
    }

    override suspend fun selectUnselectContributor(
        uid: String
    ) {

        val userParams: ContributorSaveTripDomainModel? = _selectableContributorsState.first().find { it.uid == uid }

        val uids: Map<String, Boolean>
        val parameters: Map<String, ContributorSaveTripDomainModel>

        val select = !_saveTripState.value.saveTripData.selectedContributorsUIDs.containsKey(uid)

        if(select) {

            if (userParams != null) {

                uids = _saveTripState.value.saveTripData.selectedContributorsUIDs.plus(
                    Pair(uid, true)
                )
                parameters = _saveTripState.value.saveTripData.selectedContributorsParameters.plus(
                    Pair(uid, userParams)
                )

                _saveTripState.update {
                    it.copy(
                        saveTripData = it.saveTripData.copy(
                            selectedContributorsUIDs = uids,
                            selectedContributorsParameters = parameters
                        )
                    )
                }
            }
        } else {

            uids = _saveTripState.value.saveTripData.selectedContributorsUIDs.minus(
                uid
            )
            parameters = _saveTripState.value.saveTripData.selectedContributorsParameters.minus(
                uid
            )

            _saveTripState.update {
                it.copy(
                    saveTripData = it.saveTripData.copy(
                        selectedContributorsUIDs = uids,
                        selectedContributorsParameters = parameters
                    )
                )
            }
        }
    }

    override suspend fun setUserPermission(userUid: String, permissionToUpdate: Boolean) {

        withContext(saveTripCoroutineDispatcher) {

            if (!_saveTripState.value.saveTripData.selectedContributorsParameters.containsKey(userUid)) {

                selectUnselectContributor(
                    uid = userUid
                )
            }

            val users = _saveTripState.value.saveTripData.selectedContributorsParameters.toMutableMap()

            users[userUid] = users[userUid]!!.copy(
                selected = permissionToUpdate
            )

            _saveTripState.update {

                it.copy(
                    saveTripData = it.saveTripData.copy(
                        selectedContributorsParameters = users
                    )
                )
            }
        }
    }

    override suspend fun addContributor(contributor: ContributorSaveTripDomainModel) {

        withContext(saveTripCoroutineDispatcher) {

            _saveTripState.update {
                it.copy(
                    saveTripData = it.saveTripData.copy(selectedContributorsUIDs = it.saveTripData.selectedContributorsUIDs.plus(
                        Pair(
                            contributor.uid,
                            true
                        )
                    ),
                    selectedContributorsParameters = it.saveTripData.selectedContributorsParameters.plus(
                        Pair(
                            contributor.uid,
                            contributor
                        )
                    ))
                )
            }
        }
    }

    override suspend fun uploadTripToRemoteDatabase() {

        withContext(saveTripCoroutineDispatcher) {

            val creatorUid = userState.value?.uid

            firebaseRemoteDataSource.uploadTrip(
                trip = _saveTripState.value.saveTripData.toRemoteTripEntity(),
                firebaseIdentifier = _saveTripState.value.saveTripData.toTripIdentifierRemoteEntityModel(creatorID = creatorUid)
            )
        }
    }

    override suspend fun uploadTripToLocalDatabase(){

        withContext(saveTripCoroutineDispatcher) {

            val trip = _saveTripState.value.saveTripData.toLocalTripEntity()

            val places: MutableList<PlaceLocalEntityModel> = mutableListOf()

            val days = _saveTripState.value.saveTripData.daysOfTrip.map {

                val dayUUID = UUID.randomUUID().toString()

                places.addAll(it.places.map { place ->
                    place.toLocalPlaceEntityModel(dayUUID)
                })

                Log.d("saveToLocal","Saved places for day of uuid: $dayUUID Number of all places: ${it.places.size}")

                it.toLocalDayOfTripEntityModel(
                    dayUUID = dayUUID,
                    trip.uUID
                )
            }

            val originalPlaces: MutableList<PlaceLocalEntityModel> = mutableListOf()

            val originalDays = _saveTripState.value.saveTripOptions.originalDays.map {

                val dayUUID = it.dayUUID!!

                originalPlaces.addAll(it.places.map { place ->
                    place.toLocalPlaceEntityModel(dayUUID)
                })

                it.toLocalDayOfTripEntityModel(
                    dayUUID = dayUUID,
                    trip.uUID
                )
            }

            roomLocalDataSource.uploadTrip(
                trip = trip,
                days = days,
                places = places,
                originalDays = originalDays,
                originalPlaces = originalPlaces
            )
        }
    }
}