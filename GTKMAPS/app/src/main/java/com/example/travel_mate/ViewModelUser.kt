package com.example.travel_mate

import android.R
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelUser (
    private val tripRepository: TripRepository
): ViewModel() {

    private val _userUiState = MutableStateFlow(UserUiState())
    val userUiState: StateFlow<UserUiState> = _userUiState.asStateFlow()

    private val _currentTripUiState = MutableStateFlow(CurrentTripState())
    val currentTripUiState: StateFlow<CurrentTripState> = _currentTripUiState.asStateFlow()

    private val _writeErrorMessage = MutableLiveData<String?>()
    val writeErrorMessage: LiveData<String?> = _writeErrorMessage

    private val _readErrorMessage = MutableLiveData<String?>()
    val readErrorMessage: LiveData<String?> = _readErrorMessage


    init {

        viewModelScope.launch {

            combine(
                tripRepository.tripsStateFlow,
                tripRepository.userState,
                _userUiState
            ) { tripStateFlow, userState, userStateFlow ->

                userStateFlow.copy(
                    user = userState.user,
                    username = userState.username,
                    contributors = userState.contributors.values.toList(),
                    trips = tripStateFlow.trips
                )

            }.collect { newState ->

                _userUiState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                tripRepository.currentTripState,
                _currentTripUiState
            ) { currentTripStateFlow, currentTrip ->

                currentTrip.copy(
                    currentTrip = currentTripStateFlow.trip,
                    tripIdentifier = currentTripStateFlow.tripIdentifier
                )

            }.collect { newState ->
                _currentTripUiState.value = newState
            }

        }
    }

    fun setUpdatedFrom(fragmentName: String) {

        _currentTripUiState.update {
            it.copy(
                updatedFrom = fragmentName
            )
        }
    }

    fun deleteCurrentTrip(trip: Trip, tripIdentifier: TripRepository.TripIdentifier) {

        viewModelScope.launch {
            tripRepository.deleteCurrentTrip(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }

    fun initDefaultTrip() {

        viewModelScope.launch {

            tripRepository.initDefaultTrip()
        }
    }

    fun resetCurrentTrip() {

        viewModelScope.launch {

            tripRepository.resetCurrentTrip()
        }
    }

    fun setCurrentTrip(trip: Trip, tripIdentifier: TripRepository.TripIdentifier) {

        viewModelScope.launch {

            tripRepository.setCurrentTrip(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }

    fun initAddUpdateTrip(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            tripRepository.initAddUpdateTrip(
                startPlace = startPlace,
                places = places
            )
        }
    }

    fun saveTripWithUpdatedPlaces(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            tripRepository.saveTripWithUpdatedPlaces(
                startPlace = startPlace,
                places = places
            )
        }
    }

    /*fun initNewAddUpdateTripState(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            tripRepository.initNewCurrentTripState(
                startPlace = startPlace,
                places = places
            )
        }
    }*/

    /*fun setCurrentTripStateTitle(title: String) {

        viewModelScope.launch {

            _currentTripUiState.update {

                it.copy(
                    currentTrip = it.currentTrip.copy(
                        title = title
                    ),
                    tripIdentifier = it.tripIdentifier.copy(
                        title = title
                    )
                )
            }
        }
    }

    fun setCurrentTripStateDate(date: String) {

        viewModelScope.launch {

            _currentTripUiState.update {

                it.copy(
                    currentTrip = it.currentTrip.copy(
                        date = date
                    )
                )
            }
        }
    }

    fun setCurrentTripStateNote(note: String) {

        viewModelScope.launch {

            _currentTripUiState.update {

                it.copy(
                    currentTrip = it.currentTrip.copy(
                        note = note
                    )
                )
            }
        }
    }*/

    /*fun setSelectedContributorsUsernames(usernames: List<String>) {

        viewModelScope.launch {

            _currentTripUiState.update {

                it.copy(
                    selectedContributors = usernames
                )
            }
        }
    }*/

    fun setUpdatePermission(uid: String, canUpdate: Boolean) {

        viewModelScope.launch {

            tripRepository.setUpdatePermission(
                uid = uid,
                canUpdate = canUpdate
            )
        }
    }

    fun setCurrentTripContributors() {

        viewModelScope.launch {

            tripRepository.setCurrentTripContributors()
        }
    }

    fun getNewContributorData(username: String) {

        viewModelScope.launch {

            tripRepository.getNewContributorData(
                username = username
            )

        }
    }

    fun selectContributor(uid: String) {

        viewModelScope.launch {

            tripRepository.selectUnselectContributor(
                uid = uid
            )
        }
    }

    fun getUsernamesByUIDs(uIds: List<String>){

        viewModelScope.launch {

            tripRepository.getUsernamesByUIDs(
                uIds = uIds)
        }

    }

    fun setRecentContributors(contributors: Map<String, Boolean>) {

        viewModelScope.launch {

            tripRepository.setRecentContributors(
                users = contributors
            )
        }
    }

    fun getSelectableContributors() {

        viewModelScope.launch {

            tripRepository.getSelectableContributors()
        }
    }

    fun saveTrip(trip: Trip, tripIdentifier: TripRepository.TripIdentifier){

        viewModelScope.launch {

            tripRepository.saveNewTrip(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }

    fun checkCurrentUser() {

        viewModelScope.launch {

            tripRepository.checkUser()
        }
    }

    fun signIn(email: String, password: String) {

        viewModelScope.launch {

            tripRepository.signIn(
                email = email,
                password = password
            )
        }
    }

    fun signUp(email: String, password: String, username: String) {

        viewModelScope.launch {

            tripRepository.createUser(
                email = email,
                password = password,
                username = username
            )
        }
    }

    fun signOut() {

        viewModelScope.launch {

            tripRepository.signOut()
        }
    }

    fun deleteUser(password: String) {

        viewModelScope.launch {

            tripRepository.deleteCurrentUser(
                password = password
            )
        }

    }

    fun resetPassword(email: String) {

        viewModelScope.launch {

            tripRepository.resetPassword(
                email = email
            )
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {

        viewModelScope.launch {

            tripRepository.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
        }
    }

    fun fetchMyTripsFromDatabase() {

        viewModelScope.launch {

            tripRepository.fetchMyTripsFromFirebase()
        }
    }

    fun fetchContributedTripsFromDatabase() {

        viewModelScope.launch {

            tripRepository.fetchContributedTripsFromFirebase()
        }
    }


    fun setCurrentTripIdentifier(tripIdentifier: TripRepository.TripIdentifier) {

        viewModelScope.launch {

            tripRepository.getCurrentTripData(
                tripIdentifier = tripIdentifier
            )
        }

    }

    fun fetchSavedTrips(){

        viewModelScope.launch {

            try {

                tripRepository.fetchAllLocalSavedTrips()

            }catch (e: Exception){

                Log.e("FileReader", "Error reading file: saved_trips.json \n error:", e)
                _readErrorMessage.postValue("Error reading file")
            }
        }
    }

    data class UserUiState(
        val user: FirebaseUser? = null,
        val username: String? = null,
        val contributors: List<Contributor> = emptyList(),
        val trips: List<TripRepository.TripIdentifier> = emptyList())

    data class CurrentTripState(val currentTrip: Trip? = null,
                                val tripIdentifier: TripRepository.TripIdentifier? = null,
                                val recentContributors: MutableList<Contributor> = mutableListOf(),
                                val updatedFrom: String = ""
    )


}