package com.example.features.savetrip.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.savetrip.domain.usecases.AssignPlaceToDayUseCase
import com.example.features.savetrip.domain.usecases.GetAssignablePlacesUseCase
import com.example.features.savetrip.domain.usecases.GetPlacesOfSelectedDayUseCase
import com.example.features.savetrip.domain.usecases.GetSaveTripContributorsInfoUseCase
import com.example.features.savetrip.domain.usecases.GetSaveTripInfoUseCase
import com.example.features.savetrip.domain.usecases.GetSelectableContributorsInfoUseCase
import com.example.features.savetrip.domain.usecases.HasUserSignedInUseCase
import com.example.features.savetrip.domain.usecases.RemoveDayFromTripUseCase
import com.example.features.savetrip.domain.usecases.RemovePlaceFromDayOfTripUseCase
import com.example.features.savetrip.domain.usecases.SaveTripUseCase
import com.example.features.savetrip.domain.usecases.SelectUnselectContributorUseCase
import com.example.features.savetrip.domain.usecases.SetDaysOfTripUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripDateUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripNoteUseCase
import com.example.features.savetrip.domain.usecases.SetSaveTripTitleUseCase
import com.example.features.savetrip.domain.usecases.SetSelectedDayOfSaveTripUseCase
import com.example.features.savetrip.domain.usecases.SetTripDetailsUseCase
import com.example.features.savetrip.domain.usecases.SetUserPermissionUseCase
import com.example.features.savetrip.presentation.mappers.toContributorSaveTripPresentationModel
import com.example.features.savetrip.presentation.mappers.toPlaceSaveTripPresentationModel
import com.example.features.savetrip.presentation.mappers.toSaveTripInfoPresentationModel
import com.example.features.savetrip.presentation.models.ContributorSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.PlaceSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.SaveTripInfoPresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.map

class SaveTripViewModel(
    private val hasUserSignedInUseCase: HasUserSignedInUseCase,
    private val getSaveTripInfoUseCase: GetSaveTripInfoUseCase,
    private val getSaveTripContributorsInfoUseCase: GetSaveTripContributorsInfoUseCase,
    private val getSelectableContributorsInfoUseCase: GetSelectableContributorsInfoUseCase,
    private val getAssignablePlacesUseCase: GetAssignablePlacesUseCase,
    private val assignPlaceToDayUseCase: AssignPlaceToDayUseCase,
    private val removeDayFromTripUseCase: RemoveDayFromTripUseCase,
    private val removePlaceFromDayOfTripUseCase: RemovePlaceFromDayOfTripUseCase,
    private val saveTripUseCase: SaveTripUseCase,
    private val setTripDetailsUseCase: SetTripDetailsUseCase,
    private val selectUnselectContributorUseCase: SelectUnselectContributorUseCase,
    private val setUserPermissionUseCase: SetUserPermissionUseCase,
    private val setDaysOfTripUseCase: SetDaysOfTripUseCase,
    private val getPlacesOfSelectedDayUseCase: GetPlacesOfSelectedDayUseCase,
    private val setSelectedDayOfSaveTripUseCase: SetSelectedDayOfSaveTripUseCase,
    private val setSaveTripDateUseCase: SetSaveTripDateUseCase,
    private val setSaveTripTitleUseCase: SetSaveTripTitleUseCase,
    private val setSaveTripNoteUseCase: SetSaveTripNoteUseCase,
): ViewModel() {

    val hasUser: StateFlow<Boolean> by lazy {

        hasUserSignedInUseCase().map {
            it
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    }
    val saveTripInfoState: StateFlow<SaveTripInfoPresentationModel> by lazy {

        getSaveTripInfoUseCase().map { saveTripInfo ->

            saveTripInfo.toSaveTripInfoPresentationModel()
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SaveTripInfoPresentationModel()
        )
    }

    val placesOfSelectedDayState: StateFlow<List<PlaceSaveTripPresentationModel>> by lazy {

        getPlacesOfSelectedDayUseCase().map { placesOfDay ->

            placesOfDay.map { it.toPlaceSaveTripPresentationModel() }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val saveTripContributorsState: StateFlow<List<ContributorSaveTripPresentationModel>> by lazy {

        combine(
            getSaveTripContributorsInfoUseCase(),
            getSelectableContributorsInfoUseCase()
        ){ tripContributors, selectableContributors ->

            tripContributors.map { it.toContributorSaveTripPresentationModel(true)}.plus(
                selectableContributors.filter {
                    contributor -> tripContributors.none {
                        it.uid == contributor.uid
                    }
                }.map { it.toContributorSaveTripPresentationModel(false) }
            )

        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val assignablePlacesState: StateFlow<List<PlaceSaveTripPresentationModel>> by lazy {

        getAssignablePlacesUseCase().map { assignablePlaces ->

            assignablePlaces.map{ it.toPlaceSaveTripPresentationModel() }
        }.flowOn(
            Dispatchers.Main
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun assignPlaceToDayOfTrip(placeUUID: String) {

        viewModelScope.launch {

            assignPlaceToDayUseCase(
                placeUUID = placeUUID
            )
        }
    }

    fun setDaysOfTrip(daysLabels: List<String>) {

        viewModelScope.launch {

            setDaysOfTripUseCase(
                daysLabels = daysLabels
            )
        }
    }

    fun setSelectedDayOfTrip(index: Int) {

        viewModelScope.launch {

            setSelectedDayOfSaveTripUseCase(index)
        }
    }

    fun removeDayFromTrip(dayIndex: Int) {

        viewModelScope.launch {

            removeDayFromTripUseCase(index = dayIndex)
        }
    }

    fun removePlaceFromDayOfTrip(placeUUID: String) {

        viewModelScope.launch {

            removePlaceFromDayOfTripUseCase(
                placeUUID = placeUUID
            )
        }
    }

    fun saveTripWith(
        title: String,
        startDate: String?,
        note: String?
    ) {

        viewModelScope.launch {

            setTripDetailsUseCase(
                title = title,
                startDate = startDate,
                note = note
            )

            saveTripUseCase()
        }
    }

    fun selectUnselectContributor(contributorUID: String, select: Boolean) {

        viewModelScope.launch {

            selectUnselectContributorUseCase(
                uid = contributorUID,
                select = select
            )
        }
    }

    fun setUserPermission(userUID: String, permissionToUpdate: Boolean) {

        viewModelScope.launch {

            setUserPermissionUseCase(
                uid = userUID,
                permissionToUpdate = permissionToUpdate
            )
        }
    }

    fun setSaveTripTitle(title: String) {

        viewModelScope.launch {

            setSaveTripTitleUseCase(
                title = title
            )
        }
    }

    fun setSaveTripDate(date: String) {

        viewModelScope.launch {

            setSaveTripDateUseCase(
                date = date
            )
        }
    }

    fun setSaveTripNote(note: String) {

        viewModelScope.launch {

            setSaveTripNoteUseCase(
                note = note
            )
        }
    }

}