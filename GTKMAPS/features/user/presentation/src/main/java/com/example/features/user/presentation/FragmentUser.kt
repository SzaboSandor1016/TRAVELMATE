package com.example.features.user.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.example.core.ui.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.example.features.user.presentation.databinding.FragmentUserBinding
import com.example.features.user.presentation.models.UserUserPresentationModel
import com.example.features.user.presentation.viewmodel.ViewModelUser
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**[FragmentUser]
 * a [Fragment] to show the saved trips and the shared ones too if there is a user signed in
 */
class FragmentUser : Fragment() {
    // TODO: Rename and change types of parameters

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModelUser: ViewModelUser by inject<ViewModelUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelUser.userUiState.collect{

                    when(it) {

                        is UserUserPresentationModel.SignedOut -> {
                            setSignedOutUIState()
                        }
                        is UserUserPresentationModel.SignedIn -> {
                            setSignedInUIState(it.username)
                        }
                    }
                }
            }
        }

        binding.back.setOnClickListener { l ->

            returnAndClear()
        }

        binding.signIn.setOnClickListener { l ->

            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentUser_to_fragmentSignIn)
        }

        binding.signOut.setOnClickListener { l ->

            viewModelUser.signOut()
        }

        binding.settings.setOnClickListener { l ->
            findNavController().navigate(com.example.features.user.presentation.R.id.action_fragmentUser_to_fragmentSettings)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentUser.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FragmentUser().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun returnAndClear() {

        val request = NavDeepLinkRequest.Builder.fromUri(
            "android-app://app/main".toUri()
        ).build()

        findNavController().navigate(request)
    }

    private fun setSignedInUIState(username :String) {

        binding.signedIn.setText(username)

        binding.signIn.visibility = View.GONE
        binding.signOut.visibility = View.VISIBLE

        binding.settings.visibility = View.VISIBLE
    }
    fun setSignedOutUIState() {

        binding.signedIn.setText(resources.getString(R.string.not_signed_in))

        binding.signIn.visibility = View.VISIBLE
        binding.signOut.visibility = View.GONE

        binding.settings.visibility = View.GONE
    }
}