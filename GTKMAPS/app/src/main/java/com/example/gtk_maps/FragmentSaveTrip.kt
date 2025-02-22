package com.example.gtk_maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.gtk_maps.databinding.FragmentSaveTripBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TRIP = "trip"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSaveTrip.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSaveTrip : Fragment() {
    private var datePicker: MaterialDatePicker<Long>? = null

    // TODO: Rename and change types of parameters
    //private lateinit var trip: ClassTrip
    private lateinit var trip: ClassTrip

    private var _binding: FragmentSaveTripBinding? = null
    private val binding get() = _binding!!

    private val viewModelSave: ViewModelSave by activityViewModels()
    private val viewModelMain: ViewModelMain by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //this.trip = it.getParcelable(TRIP)!!
        }
        trip = viewModelMain.getCurrentTrip()
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

        binding.saveDateLayout.setEndIconOnClickListener { l ->

            setupDatePicker()

        }

        binding.saveTrip.setOnClickListener { l ->

            val title = binding.saveTitle.getText().toString()

            if (title.equals("")){
                binding.saveTitleLayout.error = resources.getString(R.string.required)
            }else {
                viewModelMain.setUUID()
                viewModelMain.setTripTitle(title)
                viewModelMain.setTripDate(binding.saveDate.getText().toString())
                viewModelMain.setTripNote(binding.saveNote.getText().toString())

                viewModelSave.writeTripToFile(this.trip)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearBindingListeners()
        _binding = null  // Elkerüli a memória szivárgást

        clearDatePicker()
    }

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

    fun updateSaveTrip(){

        this.trip = viewModelMain.getCurrentTrip()

    }

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

        datePicker?.show(parentFragmentManager,"datePicker")
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