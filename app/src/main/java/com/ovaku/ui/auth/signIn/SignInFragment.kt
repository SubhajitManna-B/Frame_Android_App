package com.ovaku.ui.auth.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ovaku.R
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.data.models.userLogin.UserDetails
import com.ovaku.data.models.userLogin.UserSendData
import com.ovaku.databinding.FragmentSignInBinding
import com.ovaku.ui.auth.AuthViewModel
import com.ovaku.ui.main.MainActivity
import com.ovaku.utils.ext.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private var firstTimeClick = true
    private val signInViewModel by viewModels<SignInViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initObserver()
    }

    private fun initObserver() {
        signInViewModel.userLoginStatus.observe(viewLifecycleOwner){_it->
            when (_it) {
                is Resource.Loading -> {
                    authViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    _it.let {
                        authViewModel.progressView.value = false
                        lifecycleScope.launch {
                            it.data?.let { data->

                                val userDetails = decodeToken(data.payload.accessToken).fromJsonToObject<UserDetails>()

                                signInViewModel.storeUserDetails(data.payload,userDetails)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    authViewModel.progressView.value = false
                    requireContext().showCustomOkAlertFunction(title = "User Not Found", message = "User not exist associate with this number")
                }
            }
        }

        signInViewModel.preferenceStoreUserStatus.observe(viewLifecycleOwner){_it->
            when (_it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        _it.let {
                            Toast(requireContext()).showSuccessCustomToast("Login Successfully", requireActivity())
                            requireContext().gotToActivity(MainActivity::class.java)
                            requireActivity().finish()
                        }
                    }
                }
                is Resource.Error -> {
                    toast(_it.message!!)
                }
            }
        }
    }

    private fun initListener() {
        binding.btnSubmit.setOnClickListener {
            when {
                binding.etMobileNo.text.toString().trim().isEmpty() -> {
                    requireContext().showCustomOkAlertFunction(message = getString(R.string.enter_mobile_no))
                }
                binding.etMobileNo.text.toString().trim().length != 10 -> {
                    requireContext().showCustomOkAlertFunction(message = getString(R.string.enter_valid_mobile_no))
                }
                else -> {
                    if (firstTimeClick) {
                        binding.textInputLayoutPassword.visible()
                        binding.btnSubmit.text = getString(R.string.sign_in)
                        firstTimeClick = false
                    } else {
                        if(binding.etPassword.text.toString().trim().isEmpty()){
                            requireContext().showCustomOkAlertFunction(message = getString(R.string.enter_otp))
                        }else {
                            val userSendData = UserSendData(
                                binding.etMobileNo.text.toString().trim(), binding.etPassword.text.toString().trim()
                            )
                            signInViewModel.userLogin(userSendData)
                        }
                    }
                }
            }
        }
    }
}