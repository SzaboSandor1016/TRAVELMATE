package com.example.travel_mate

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.travel_mate.databinding.FragmentSaveTripBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TRIP = "trip"

/** [FragmentSaveTrip]
 * a [Fragment] to save a trip either to the local or to the remote database
 */
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

    private var datePicker: MaterialDatePicker<Long>? = null

    private var selectedContributors: Map<String,String> = emptyMap()
    private var updatedFrom: String = ""

    private var currentTrip: Trip = Trip()
    private var currentTripIdentifier: TripRepository.TripIdentifier = TripRepository.TripIdentifier()

    private val viewModelUser: ViewModelUser by activityViewModels{ MyApplication.factory }
    private val viewModelMain: ViewModelMain by activityViewModels{ MyApplication.factory }

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

        /**
         * observe the [ViewModelUser.currentTripUiState] [kotlinx.coroutines.flow.StateFlow]
         * and call the functions that updates the UI based on the read values
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelUser.currentTripUiState.collect {

                    currentTrip = it.currentTrip
                    currentTripIdentifier = it.tripIdentifier

                    updateSaveUi(
                        trip = it.currentTrip,
                        tripIdentifier = it.tripIdentifier
                    )

                    Log.d("FirebaseDatabaseSaveTrip", it.currentTrip.uUID.toString())

                    Log.d("FragmentSaveTrip", it.currentTrip.title.toString())

                    updatedFrom = it.updatedFrom

                    Log.d("FragmentSaveTrip", updatedFrom.toString())
                }
            }
        }

        binding.saveDateLayout.setEndIconOnClickListener { l ->

            setupDatePicker()

        }

        binding.saveTrip.setOnClickListener { l ->

            val title = binding.saveTitle.getText().toString()

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

                currentTrip.title = title
                currentTripIdentifier.title = title

                currentTrip.date = binding.saveDate.getText().toString().trim()
                currentTrip.note = binding.saveNote.getText().toString().trim()

                /**
                 * save the trip
                 */
                viewModelUser.saveTrip(
                    trip = currentTrip,
                    tripIdentifier = currentTripIdentifier
                )

                returnAndClear()
            }

        }

        binding.back.setOnClickListener { l ->

            returnAndClear()
        }

        binding.addContributors.setOnClickListener { l ->

            if (viewModelUser.userUiState.value.username != null)
                findNavController().navigate(R.id.action_fragmentSaveTrip_to_fragmentSelectContributors)
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
     * reset the current trip in the [ViewModelUser]
     * and navigate back either to the [FragmentMain] or to the [FragmentUser]
     * (it depends on where this fragment is initiated from)
     */
    private fun returnAndClear() {

        viewModelUser.resetCurrentTrip()

        when(updatedFrom) {
            "main" -> findNavController().navigate(R.id.action_fragmentSaveTrip_to_FragmentMain)
            "user" -> findNavController().navigate(R.id.action_fragmentSaveTrip_to_FragmentUser)
        }
    }

    /** [updateSaveUi]
     * update the UI of this fragment with the data read from the [ViewModelUser]'s [ViewModelUser.currentTripUiState]
     */
    private fun updateSaveUi(trip: Trip?, tripIdentifier: TripRepository.TripIdentifier) {

        if (trip!= null) {

            Log.d("FirebaseDatabaseUID", tripIdentifier.uuid.toString())
            Log.d("FirebaseDatabaseTitle", tripIdentifier.title.toString())
            Log.d("FirebaseDatabaseLocation", tripIdentifier.location.toString())
            Log.d("FirebaseDatabaseCreator", tripIdentifier.creatorUID.toString())
            Log.d("FirebaseDatabaseContributorUID", tripIdentifier.contributors.map { it.key }.toString())

            binding.saveTitle.setText(trip.title.toString().trim())

            binding.saveDate.setText(trip.date.toString().trim())

            binding.saveNote.setText(trip.note.toString().trim())

            setContributorsList(tripIdentifier.contributorsUsernames)
        }
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
    private fun setupDatePicker(){

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setValidator(DateValidatorPointForward.now())

        datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.day_of_trip)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        datePicker?.addOnPositiveButtonClickListener { selectedDate ->

            val timeZoneUTC: TimeZone = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC: Int = timeZoneUTC.getOffset(Date().time) * -1

            // Create a date format, then a date object with our offset
            val simpleFormat: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val date: Date = Date(selectedDate + offsetFromUTC)

            binding.saveDate.setText(simpleFormat.format(date))

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

}