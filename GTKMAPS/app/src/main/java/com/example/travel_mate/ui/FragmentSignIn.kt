package com.example.travel_mate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.travel_mate.Application
import com.example.travel_mate.R
import com.example.travel_mate.databinding.FragmentSignInBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**[FragmentSignIn]
 * a [androidx.fragment.app.Fragment] to sign in
 */
class FragmentSignIn : Fragment() {
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
         * @return A new instance of fragment FragmentSignIn.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSignIn().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private val viewModelUser: ViewModelUser by activityViewModels { Application.Companion.factory }

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var resetView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        binding.back.setOnClickListener { l ->

            findNavController().navigate(R.id.action_FragmentSignIn_to_FragmentUser)
        }

        binding.toSignUp.setOnClickListener { l ->

            findNavController().navigate(R.id.action_FragmentSignIn_to_FragmentSignUp)
        }

        binding.forgotPassword.setOnClickListener { l ->

            resetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.layout_reset_password,null,false)

            launchResetPasswordDialog()
        }

        binding.signIn.setOnClickListener { l ->

            val email = binding.signInEmail.getText().toString().trim()
            val password = binding.signInPassword.getText().toString().trim()

            if (!email.equals("") && !password.equals("")) {

                viewModelUser.signIn(
                    email = email,
                    password = password
                )

                findNavController().navigate(R.id.action_FragmentSignIn_to_FragmentUser)
            } else {
                binding.signInEmailLayout.error = resources.getString(R.string.required)
                binding.signInPasswordLayout.error = resources.getString(R.string.required)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun updateSignInFragment(){

        //Todo update sign In fragment
    }

    /** [launchResetPasswordDialog]
     * create a dialog that allows resetting the password of an use account
     */
    private fun launchResetPasswordDialog() {

        val resetPasswordInput: TextInputEditText = resetView.findViewById(R.id.reset_password)
        val resetPasswordLayout: TextInputLayout = resetView.findViewById(R.id.reset_password_layout)

        // Building the Alert dialog using materialAlertDialogBuilder instance
        materialAlertDialogBuilder.setView(resetView)
            .setTitle(resources.getString(R.string.reset_password))
            .setPositiveButton(resources.getString(R.string.done)) { dialog, _ ->
                val email = resetPasswordInput.getText().toString().trim()

                if (!email.equals("")) {
                    viewModelUser.resetPassword(
                        email = email
                    )
                }

                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.dismiss)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}