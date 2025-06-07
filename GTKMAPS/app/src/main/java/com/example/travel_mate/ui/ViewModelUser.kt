package com.example.travel_mate.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel_mate.data.Contributor
import com.example.travel_mate.data.Place
import com.example.travel_mate.data.Trip
import com.example.travel_mate.data.TripRepositoryImpl
import com.example.travel_mate.data.TripRepositoryImpl.TripIdentifier
import com.example.travel_mate.domain.CheckUserUseCase
import com.example.travel_mate.domain.CurrentTripRepository
import com.example.travel_mate.domain.DeleteTripUseCase
import com.example.travel_mate.domain.DeleteUserUseCase
import com.example.travel_mate.domain.GetLocalTripsUseCase
import com.example.travel_mate.domain.GetNewContributorDataUseCase
import com.example.travel_mate.domain.GetRemoteContributedTripsUseCase
import com.example.travel_mate.domain.GetRemoteTripsUseCase
import com.example.travel_mate.domain.GetSelectableContributorsUseCase
import com.example.travel_mate.domain.GetSelectedTripDataUseCase
import com.example.travel_mate.domain.SaveTripUseCase
import com.example.travel_mate.domain.SaveTripWithUpdatedPlacesUseCase
import com.example.travel_mate.domain.SetCurrentTripContributorsUseCase
import com.example.travel_mate.domain.SetUserPermissionUseCase
import com.example.travel_mate.domain.SignInUserUseCase
import com.example.travel_mate.domain.SignOutUserUseCase
import com.example.travel_mate.domain.SignUpUserUseCase
import com.example.travel_mate.domain.TripRepository
import com.example.travel_mate.domain.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelUser (
    private val tripRepository: TripRepository,
    private val currentTripRepository: CurrentTripRepository,
    private val userRepository: UserRepository,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val signInUserUseCase: SignInUserUseCase,
    private val signOutUserUseCase: SignOutUserUseCase,
    private val checkUserUseCase: CheckUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val setUserPermissionUseCase: SetUserPermissionUseCase,
    private val getNewContributorDataUseCase: GetNewContributorDataUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val getLocalTripsUseCase: GetLocalTripsUseCase,
    private val getRemoteTripsUseCase: GetRemoteTripsUseCase,
    private val getRemoteContributedTripsUseCase: GetRemoteContributedTripsUseCase,
    private val getSelectedTripDataUseCase: GetSelectedTripDataUseCase,
    private val getSelectableContributorsUseCase: GetSelectableContributorsUseCase,
    private val saveTripWithUpdatedPlacesUseCase: SaveTripWithUpdatedPlacesUseCase,
    private val setCurrentTripContributorsUseCase: SetCurrentTripContributorsUseCase
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
                userRepository.userState,
                _userUiState
            ) { userState, userStateFlow ->

                userStateFlow.copy(
                    user = userState.user,
                    username = userState.username,
                    contributors = userState.contributors.values.toList(),
                )

            }.collect { newState ->

                _userUiState.value = newState
            }
        }

        viewModelScope.launch {

            combine(
                currentTripRepository.currentTripState,
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

    fun deleteCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        viewModelScope.launch {


            deleteTripUseCase(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }

    //todo review
    fun initDefaultTrip() {

        viewModelScope.launch {

            currentTripRepository.initDefaultTrip()
        }
    }
    //todo review
    fun resetCurrentTrip() {

        viewModelScope.launch {

            currentTripRepository.resetCurrentTrip()
        }
    }
    //todo review (surely with UseCase)
    fun setCurrentTrip(trip: Trip, tripIdentifier: TripIdentifier) {

        viewModelScope.launch {

            currentTripRepository.setCurrentTrip(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }
    //todo review (surely with UseCase)
    fun initAddUpdateTrip(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            currentTripRepository.initAddUpdateTrip(
                startPlace = startPlace,
                places = places
            )
        }
    }

    fun saveTripWithUpdatedPlaces(startPlace: Place, places: List<Place>) {

        viewModelScope.launch {

            saveTripWithUpdatedPlacesUseCase(
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

            setUserPermissionUseCase(
                uid = uid,
                canUpdate = canUpdate
            )
        }
    }
    //todo review with UseCase
    fun setCurrentTripContributors() {

        viewModelScope.launch {

            setCurrentTripContributorsUseCase()
        }
    }

    fun getNewContributorData(username: String) {

        viewModelScope.launch {

            //todo notify user if username not found

            getNewContributorDataUseCase(
                username = username
            )

        }
    }

    fun selectContributor(uid: String) {

        viewModelScope.launch {

            userRepository.selectUnselectContributor(
                uid = uid
            )
        }
    }

    //todo review (surely with UseCase)
    fun setRecentContributors(contributors: Map<String, Boolean>) {

        viewModelScope.launch {

            userRepository.setRecentContributors(
                users = contributors
            )
        }
    }

    fun getSelectableContributors() {

        viewModelScope.launch {

            getSelectableContributorsUseCase()
        }
    }

    fun saveTrip(trip: Trip, tripIdentifier: TripIdentifier){

        viewModelScope.launch {

            //todo what if its not mine and remove all contributors
            // nothing, if you are not the OP you will not able to update the contributors
            saveTripUseCase(
                trip = trip,
                tripIdentifier = tripIdentifier
            )
        }
    }

    fun checkCurrentUser() {

        viewModelScope.launch {

            checkUserUseCase()
        }
    }

    fun signIn(email: String, password: String) {

        viewModelScope.launch {

            signInUserUseCase(
                email = email,
                password = password
            )
        }
    }

    fun signUp(email: String, password: String, username: String) {

        viewModelScope.launch {

            signUpUserUseCase(
                email = email,
                password = password,
                username = username
            )
        }
    }

    fun signOut() {

        viewModelScope.launch {

            signOutUserUseCase()
        }
    }

    fun deleteUser(password: String) {

        viewModelScope.launch {

            deleteUserUseCase(
                password = password
            )
        }

    }

    fun resetPassword(email: String) {

        viewModelScope.launch {

            userRepository.resetPassword(
                email = email
            )
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {

        viewModelScope.launch {

            userRepository.changePassword(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
        }
    }

    private fun updateStateWithFetchedTrips(tripIdentifiers: List<TripIdentifier>) {

        _userUiState.update {

            it.copy(
                trips = tripIdentifiers
            )
        }
    }

    fun fetchMyTripsFromDatabase() {

        viewModelScope.launch {

            getRemoteTripsUseCase().collect { trips ->

                updateStateWithFetchedTrips(trips)
            }
        }
    }

    fun fetchContributedTripsFromDatabase() {

        viewModelScope.launch {

            getRemoteContributedTripsUseCase().collect{ trips ->

                updateStateWithFetchedTrips(trips)
            }
        }
    }

    fun setCurrentTripIdentifier(tripIdentifier: TripIdentifier) {

        viewModelScope.launch {

            getSelectedTripDataUseCase(
                tripIdentifier = tripIdentifier
            )
        }

    }

    fun fetchSavedTrips(){

        viewModelScope.launch {

            try {

                getLocalTripsUseCase().collect { trips ->

                    updateStateWithFetchedTrips(trips)
                }

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
        val trips: List<TripIdentifier> = emptyList())

    data class CurrentTripState(val currentTrip: Trip? = null,
                                val tripIdentifier: TripIdentifier? = null,
                                val recentContributors: MutableList<Contributor> = mutableListOf(),
                                val updatedFrom: String = ""
    )


}