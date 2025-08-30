package com.example.features.savetrip.presentation

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.ui.R
import com.example.features.savetrip.presentation.databinding.FragmentSaveTripBinding
import com.example.features.savetrip.presentation.models.DayOfTripSaveTripPresentationModel
import com.example.features.savetrip.presentation.models.PlaceSaveTripPresentationModel
import com.example.features.savetrip.presentation.viewmodel.SaveTripViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TRIP = "trip"

/** [FragmentSaveTrip]
 * a [Fragment] to save a trip either to the local or to the remote database
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class FragmentSaveTrip : Fragment() {

    // TODO: Rename and change types of parameters
    //private lateinit var trip: ClassTrip
    //private lateinit var trip: ClassTrip

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSaveTrip.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*trip: ClassTrip*/) =
            FragmentSaveTrip().apply {
                arguments = Bundle().apply {
                    //putParcelable(TRIP, trip)
                }
            }
    }

    private var _binding: FragmentSaveTripBinding? = null
    private val binding get() = _binding!!

    private val days = ArrayList<DayOfTripSaveTripPresentationModel>()

    private val placesOfDay = ArrayList<PlaceSaveTripPresentationModel>()

    private val assignablePlaces = ArrayList<PlaceSaveTripPresentationModel>()

    private lateinit var adapterDays: AdapterDaysRecyclerView;

    private lateinit var adapterPlacesOfDay: AdapterPlacesOfDayRecyclerView;

    private lateinit var adapterAssignablePlaces: AdapterAssignablePlaceRecyclerView;

    private var datePicker: MaterialDatePicker<androidx.core.util.Pair<Long, Long>>? = null

/*    private var selectedContributors: Map<String,String> = emptyMap()
    private var updatedFrom: String = ""

    private var currentTrip: Trip? = null
    private var currentTripIdentifier: TripRepositoryImpl.TripIdentifier? = null*/

    private val viewModelSaveTrip: SaveTripViewModel by inject<SaveTripViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //this.trip = it.getParcelable(TRIP)!!
        }
        //trip = viewModelMain.getCurrentTrip()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSaveTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterDays = AdapterDaysRecyclerView(this.days)

        binding.tripDays.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.tripDays.adapter = adapterDays

        adapterPlacesOfDay = AdapterPlacesOfDayRecyclerView(this.placesOfDay)

        binding.placesOfDay.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.placesOfDay.adapter = adapterPlacesOfDay;

        adapterDays.setOnClickListener(object: AdapterDaysRecyclerView.OnClickListener {
            override fun onClick(position: Int) {
                viewModelSaveTrip.setSelectedDayOfTrip(position)
            }

        })

        adapterAssignablePlaces = AdapterAssignablePlaceRecyclerView(
            this.assignablePlaces
        )
        binding.assignablePlacesRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.assignablePlacesRecyclerView.adapter = adapterAssignablePlaces

        adapterAssignablePlaces.setOnItemClickListener(object: AdapterAssignablePlaceRecyclerView.OnItemClickListener {

            override fun onItemClick(placeUUID: String) {

                viewModelSaveTrip.assignPlaceToDayOfTrip(
                    placeUUID = placeUUID
                )
            }
        })

        adapterPlacesOfDay.setAddPlaceOnClickListener(object: AdapterPlacesOfDayRecyclerView.AddPlaceOnClickListener{
            override fun onClick() {

                showAssignablePlaces()
            }
        })

        adapterPlacesOfDay.setRemovePlaceFromDayOnClickListener(object: AdapterPlacesOfDayRecyclerView.RemovePlaceFromDayOnClickListener {

            override fun onClick(placeUUID: String) {
                viewModelSaveTrip.removePlaceFromDayOfTrip(placeUUID = placeUUID)
            }
        })

        /**
         * observe the [com.example.presentation.ViewModelUser.currentTripUiState] [kotlinx.coroutines.flow.StateFlow]
         * and call the functions that updates the UI based on the read values
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSaveTrip.saveTripInfoState.collect {

                    updateSaveUi(
                        date = it.date,
                        title = it.title,
                        note = it.note,
                        contributorsUsernames = it.contributorUsernames,
                        days = it.daysOfTrip
                    )


                    //if (it.note != ) {

                        //TODO This is also not necessary
                        //currentTrip = it.currentTrip
                        //currentTripIdentifier = it.tripIdentifier



                        //Log.d("FirebaseDatabaseSaveTrip", it.currentTrip.uUID.toString())

                        //Log.d("FragmentSaveTrip", it.currentTrip.title.toString())

                        //updatedFrom = it.updatedFrom

                        //Log.d("FragmentSaveTrip", updatedFrom.toString())
                    //}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSaveTrip.placesOfSelectedDayState.collect {

                    updatePlacesOfDayList(
                        it
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSaveTrip.assignablePlacesState.collect {

                    updateAssignablePlaces(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSaveTrip.hasUser.collect {

                    binding.addContributors.visibility = View.INVISIBLE

                    if (it)
                        binding.addContributors.visibility = View.VISIBLE
                }
            }
        }

        binding.saveDateLayout.setEndIconOnClickListener { l ->

            setupDatePicker()

        }

        binding.saveTitle.setOnKeyListener { l, keyCode, keyEvent ->

            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {

                viewModelSaveTrip.setSaveTripTitle(
                    binding.saveTitle.getText().toString().trim()
                )
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }

        binding.saveNote.setOnKeyListener { v, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                viewModelSaveTrip.setSaveTripNote(
                    binding.saveNote.getText().toString().trim()
                )

                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        binding.saveTrip.setOnClickListener { l ->

            val title = binding.saveTitle.getText().toString()
            val date = binding.saveDate.getText().toString()
            val note = binding.saveNote.getText().toString()

            if (title.equals("")){
                binding.saveTitleLayout.error = resources.getString(R.string.required)
            }else {

                /*viewModelUser.setCurrentTripStateTitle(
                    title = title
                )

                viewModelUser.setCurrentTripStateNote(
                    note = binding.saveNote.getText().toString().trim()
                )

                viewModelUser.setCurrentTripStateDate(
                    date = binding.saveDate.getText().toString().trim()
                )*/

                /*currentTrip?.title = title
                currentTripIdentifier?.title = title

                currentTrip?.date = binding.saveDate.getText().toString().trim()
                currentTrip?.note = binding.saveNote.getText().toString().trim()*/

                /**
                 * save the trip
                 */
                viewModelSaveTrip.saveTripWith(
                    title = title,
                    startDate = date,
                    note = note
                )

                returnAndClear()
            }

        }

        binding.back.setOnClickListener { l ->

            returnAndClear()
        }

        binding.addContributors.setOnClickListener { l ->

            //TODO CHECK THIS ONE TOO
            //if (viewModelSaveTrip.userUiState.value.username != null)
                findNavController().navigate(com.example.features.savetrip.presentation.R.id.action_fragmentSaveTrip_to_fragmentSelectContributors)
        }

        binding.hideAssignablePlaces.setOnClickListener { _ ->

            hideAssignablePlaces()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearBindingListeners()
        _binding = null  // Elkerüli a memória szivárgást

        clearDatePicker()
    }

    fun updateSaveTrip(){

        //this.trip = viewModelMain.getCurrentTrip()

    }


    /** [returnAndClear]
     * reset the current trip in the [com.example.presentation.ViewModelUser]
     * and navigate back either to the [com.example.features.map.presentation.fragment.FragmentMain] or to the [com.example.travel_mate.ui.FragmentUser]
     * (it depends on where this fragment is initiated from)
     */
    private fun returnAndClear() {

        //TODO This WON'T BE NECESSARY I THINK BUT DOUBLE CHECK LATER
        //viewModelSaveTrip.resetCurrentTrip()

        val request = NavDeepLinkRequest.Builder.fromUri(
            "android-app://com.example.features/user".toUri()
        ).build()

        findNavController().navigate(request)
    }

    /** [updateSaveUi]
     * update the UI of this fragment with the data read from the [com.example.presentation.ViewModelUser]'s [com.example.presentation.ViewModelUser.currentTripUiState]
     */
    private fun updateSaveUi(
        date: String?,
        title: String?,
        note: String?,
        contributorsUsernames: List<String>,
        days: List<DayOfTripSaveTripPresentationModel>
    ) {

        binding.saveTitle.setText(title?: "")

        binding.saveDate.setText(date?: "")

        binding.saveNote.setText(note?: "")

        setContributorsList( contributorsList = contributorsUsernames )

        updateDaysList(days = days)

        //TODO create UI elements for showing days of trip

        //if (trip!= null) {

            /*Log.d("FirebaseDatabaseUID", tripIdentifier.uuid.toString())
            Log.d("FirebaseDatabaseTitle", tripIdentifier.title.toString())
            Log.d("FirebaseDatabaseLocation", tripIdentifier.location.toString())
            Log.d("FirebaseDatabaseCreator", tripIdentifier.creatorUID.toString())
            Log.d("FirebaseDatabaseContributorUID", tripIdentifier.contributors.map { it.key }.toString())
*/
            /*binding.saveTitle.setText(trip.title.toString().trim())

            binding.saveDate.setText(trip.date.toString().trim())

            binding.saveNote.setText(trip.note.toString().trim())

            val usernames = tripIdentifier.contributors.values.toList().map { it.username.toString() }

            setContributorsList( contributorsList = usernames )*/
        //}
    }



    private fun showAssignablePlaces() {

        binding.assignablePlacesLayout.visibility = View.VISIBLE
    }

    private fun hideAssignablePlaces() {

        binding.assignablePlacesLayout.visibility = View.GONE
    }

    private fun updateAssignablePlaces(assignablePlaces: List<PlaceSaveTripPresentationModel>) {

        this.assignablePlaces.clear()

        this.assignablePlaces.addAll(assignablePlaces)

        adapterAssignablePlaces.notifyDataSetChanged()
    }

    private fun updateDaysList(days: List<DayOfTripSaveTripPresentationModel>) {

        this.days.clear()

        this.days.addAll(days)

        adapterDays.notifyDataSetChanged()
    }

    private fun updatePlacesOfDayList(places: List<PlaceSaveTripPresentationModel>) {

        this.placesOfDay.clear()

        this.placesOfDay.addAll(
            places
        )

        adapterPlacesOfDay.notifyDataSetChanged()
    }

    private fun setContributorsList(contributorsList: List<String>) {


        when (contributorsList.size<3) {

            true -> {

                binding.contributorsList.setText(contributorsList.joinToString(separator = ",") { it })
            }
            else -> {

                val stringBuilder = StringBuilder(contributorsList[0])
                stringBuilder.append(" ," + contributorsList[1])
                stringBuilder.append(" + " + (contributorsList.size-1))

                binding.contributorsList.setText(stringBuilder.toString())
            }
        }

    }

    /** [setupDatePicker]
     * set up a [MaterialDatePicker] dialog to select the day of the trip
     */
    @OptIn(ExperimentalTime::class)
    private fun setupDatePicker(){

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setValidator(DateValidatorPointForward.now())

        datePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.day_of_trip)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker?.addOnPositiveButtonClickListener { selectedDateRange ->

            val timeZoneUTC: TimeZone = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().time) * -1

            // Create a date format, then a date object with our offset
            val simpleFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val startDate= Date(selectedDateRange.first + offsetFromUTC)
            val endDate = Date(selectedDateRange.second + offsetFromUTC)

            val instantStart: java.time.Instant = java.time.Instant.ofEpochMilli(selectedDateRange.first)

            val instantEnd: java.time.Instant = java.time.Instant.ofEpochMilli(selectedDateRange.second)

            val localStart = instantStart.atZone(ZoneId.of("UTC+01:00")).toLocalDate()
            val localEnd = instantEnd.atZone(ZoneId.of("UTC+01:00")).toLocalDate()

            val days = ArrayList<String>()

            for(day in localStart.rangeTo(localEnd)) {

                days.add("${getMonthLabel(day.month.value)} ${day.dayOfMonth}")
            }

            viewModelSaveTrip.setDaysOfTrip(days);

            val dateRange = "${simpleFormat.format(startDate)} - ${
                simpleFormat.format(
                    endDate
                )
            }"
            binding.saveDate.setText(
                dateRange
            )

            viewModelSaveTrip.setSaveTripDate(
                dateRange
            )

        }
        datePicker?.addOnNegativeButtonClickListener {
            // Respond to negative button click.
            datePicker?.dismiss()
            clearDatePicker()
        }
        datePicker?.addOnCancelListener {
            // Respond to cancel events.
            clearDatePicker()

        }
        datePicker?.addOnDismissListener {
            // Respond to dismiss events.
            clearDatePicker()
        }

        datePicker?.show(childFragmentManager,"datePicker")
    }

    private fun clearDatePicker() {
        datePicker?.clearOnPositiveButtonClickListeners()
        datePicker?.clearOnNegativeButtonClickListeners()
        datePicker?.clearOnCancelListeners()
        datePicker?.clearOnDismissListeners()
        datePicker = null
    }

    private fun clearBindingListeners(){
        _binding?.saveDateLayout?.setEndIconOnClickListener(null)
        _binding?.saveTrip?.setOnClickListener(null)
    }

    class DateIterator(val startDate: LocalDate,
                       val endDateInclusive: LocalDate,
                       val stepDays: Long): Iterator<LocalDate> {

        private var currentDate = startDate

        override fun hasNext() = currentDate <= endDateInclusive

        override fun next(): LocalDate {
            val next = currentDate
            currentDate = currentDate.plusDays(stepDays)
            return next
        }
    }

    class DateProgression(override val start: LocalDate,
                          override val endInclusive: LocalDate,
                          val stepDays: Long = 1) : Iterable<LocalDate>,
        ClosedRange<LocalDate> {

        override fun iterator(): Iterator<LocalDate> =
            DateIterator(start, endInclusive, stepDays)

        infix fun step(days: Long) = DateProgression(start, endInclusive, days)

    }

    operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

    //TODO maybe store the id and the dayOfMont in a separate field

    fun getMonthLabel(intValue: Int): String {

        return when(intValue) {
            1 -> resources.getString(R.string.month_1)
            2 -> resources.getString(R.string.month_2)
            3 -> resources.getString(R.string.month_3)
            4 -> resources.getString(R.string.month_4)
            5 -> resources.getString(R.string.month_5)
            6 -> resources.getString(R.string.month_6)
            7 -> resources.getString(R.string.month_7)
            8 -> resources.getString(R.string.month_8)
            9 -> resources.getString(R.string.month_9)
            10 -> resources.getString(R.string.month_10)
            11 -> resources.getString(R.string.month_11)
            else -> resources.getString(R.string.month_12)
        }
    }
}