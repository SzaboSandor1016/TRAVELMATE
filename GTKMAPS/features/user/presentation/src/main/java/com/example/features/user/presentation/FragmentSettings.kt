package com.example.features.user.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.core.ui.R
import androidx.navigation.fragment.findNavController
import com.example.features.user.presentation.databinding.FragmentSettingsBinding
import com.example.features.user.presentation.viewmodel.ViewModelUser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.android.ext.android.inject

/** [FragmentSettings]
 * a [Fragment] for user account settings
 */
class FragmentSettings : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSettings().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsMaterialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var deleteView: View
    private lateinit var changePasswordView: View

    private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*arguments?.let {
            *//*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*//*
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsMaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        binding.deleteAccount.setOnClickListener { l ->

            deleteView = LayoutInflater.from(requireContext())
                .inflate(com.example.features.user.presentation.R.layout.layout_delete_dialog,null,false)

            launchDeleteDialog()
        }

        binding.changePassword.setOnClickListener { l ->

            changePasswordView = LayoutInflater.from(requireContext())
                .inflate(com.example.features.user.presentation.R.layout.layout_change_password_dialog,null,false)

            launchPasswordChangeDialog()
        }

        binding.back.setOnClickListener { l ->
            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentSettings_to_fragmentUser)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    /** [launchDeleteDialog]
     * create a dialog that allows deleting the user account
     */
    private fun launchDeleteDialog() {

        val deletePassword: TextInputEditText = deleteView.findViewById(com.example.features.user.presentation.R.id.password)

        settingsMaterialAlertDialogBuilder.setView(deleteView)
            .setTitle(resources.getString(R.string.delete_account))
            .setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
                val password = deletePassword.getText().toString().trim()

                viewModelUser.deleteUser(
                    password = password
                )

                dialog.dismiss()

                findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentSettings_to_fragmentUser)
            }
            .setNegativeButton(resources.getString(R.string.dismiss)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun launchPasswordChangeDialog() {

        val currentPassword: TextInputEditText = changePasswordView.findViewById(com.example.features.user.presentation.R.id.current_password)
        val currentPasswordLayout: TextInputLayout = changePasswordView.findViewById(com.example.features.user.presentation.R.id.current_password_layout)

        val newPassword: TextInputEditText = changePasswordView.findViewById(com.example.features.user.presentation.R.id.change_password)
        val newPasswordLayout: TextInputLayout = changePasswordView.findViewById(com.example.features.user.presentation.R.id.change_password_layout)

        val newPasswordAgain: TextInputEditText = changePasswordView.findViewById(com.example.features.user.presentation.R.id.change_password_again)
        val newPasswordAgainLayout: TextInputLayout = changePasswordView.findViewById(com.example.features.user.presentation.R.id.change_password_again_layout)

        settingsMaterialAlertDialogBuilder.setView(changePasswordView)
            .setTitle(resources.getString(R.string.change_password))
            .setPositiveButton(resources.getString(R.string.done)) { dialog, _ ->
                val currentPasswordInput = currentPassword.getText().toString().trim()
                val newPasswordInput = newPassword.getText().toString().trim()
                val newPasswordAgainInput = newPasswordAgain.getText().toString().trim()

                if (!currentPasswordInput.equals("")) {
                    if (!newPasswordInput.equals("")) {
                        if (newPasswordInput.equals(newPasswordAgainInput)) {


                            viewModelUser.changePassword(
                                currentPassword = currentPasswordInput,
                                newPassword = newPasswordInput
                            )

                            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentSettings_to_fragmentUser)

                        } else {
                            newPasswordLayout.error = resources.getString(R.string.required)
                            newPasswordAgainLayout.error = resources.getString(R.string.required)
                        }
                    } else {
                        newPasswordLayout.error = resources.getString(R.string.required)
                    }
                } else {

                    currentPasswordLayout.error = resources.getString(R.string.required)
                }
            }
            .setNegativeButton(resources.getString(R.string.dismiss)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}