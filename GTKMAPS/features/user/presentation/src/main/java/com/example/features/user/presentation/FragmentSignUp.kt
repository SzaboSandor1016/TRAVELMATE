package com.example.features.user.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.features.user.presentation.databinding.FragmentSignUpBinding
import com.example.features.user.presentation.viewmodel.ViewModelUser
import com.example.core.ui.R
import org.koin.android.ext.android.inject

/**[FragmentSignUn]
 * a [Fragment] to sign up
 */
class FragmentSignUp : Fragment() {
    // TODO: Rename and change types of parameters
/*    private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSignUp.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSignUp().apply {
                arguments = Bundle().apply {/*
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setOnClickListener { l ->

            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentSignUp_to_fragmentSignIn)
        }

        binding.signUp.setOnClickListener { l ->

            val email = binding.signUpEmail.getText().toString().trim()
            val password = binding.signUpPassword.getText().toString().trim()
            val confirmedPassword = binding.signUpPasswordAgain.getText().toString().trim()
            val username = binding.signUpUsername.getText().toString().trim()

            if (!email.equals("")) {

                if (!password.equals("")) {

                    if (password.equals(confirmedPassword)) {

                        if (!username.equals("")) {

                            viewModelUser.signUp(
                                email = email,
                                password = password,
                                username = username
                            )

                            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentSignUp_to_fragmentUser)

                        } else {

                            binding.signUpUsernameLayout.error =
                                resources.getString(R.string.required)
                        }
                    } else {

                        binding.signUpPasswordAgainLayout.error =
                            resources.getString(R.string.password_mismatch)
                        binding.signUpPasswordLayout.error =
                            resources.getString(R.string.password_mismatch)
                    }
                }else {

                    binding.signUpPassword.error =
                        resources.getString(R.string.required)
                }
            } else {

                binding.signUpEmailLayout.error =
                    resources.getString(R.string.required)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun updateSignUpFragment() {

        //TODO update sign up fragment
    }
}