package com.example.travel_mate

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    username = userState.username,
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

    fun setCurrentTripContributors(contributors: Map<String, Boolean>) {

        viewModelScope.launch {

            tripRepository.setCurrentTripContributors(
                contributors = contributors
            )
        }
    }

    fun getNewContributorData(username: String) {

        viewModelScope.launch {

            val user = async {

                tripRepository.findUserByUsername(
                    username = username
                )
            }

            Log.d("FirebaseCurrentUser", user.await()?.first + " " + user.await()?.second)

            if (user.await() != null) {

                _currentTripUiState.update {

                    val contributors = it.recentContributors.plus(Contributor(user.await()!!, true))

                    it.copy(
                        recentContributors = contributors.toMutableList()
                    )
                }
            }

        }
    }

    fun selectContributor(position: Int) {

        _currentTripUiState.update {

            val oldList = it.recentContributors

            oldList[position].selected = !oldList[position].selected

            it.copy(
                recentContributors = oldList.toMutableList()
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

    fun getRecentContributors() {

        viewModelScope.launch {
            val contributors = tripRepository.getRecentContributorsOfUser()

            _currentTripUiState.update {
                it.copy(
                    recentContributors = contributors.entries.map { Contributor(it.toPair(),false) }.toMutableList()
                )
            }

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
        val username: String? = null,
        val contributors: Map<String, String> = hashMapOf(),
        val trips: List<TripRepository.TripIdentifier> = emptyList())

    data class CurrentTripState(val currentTrip: Trip = Trip(),
                                val tripIdentifier: TripRepository.TripIdentifier = TripRepository.TripIdentifier(uuid = currentTrip.uUID),
                                val recentContributors: MutableList<Contributor> = mutableListOf(),
                                val updatedFrom: String = ""
    )

    data class Contributor(val data: Pair<String, String>, var selected: Boolean)
}