package com.ovaku.ui.splash

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.ovaku.R
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.databinding.ActivitySplashBinding
import com.ovaku.ui.auth.AuthActivity
import com.ovaku.ui.main.MainActivity
import com.ovaku.utils.ext.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel by viewModels<SplashViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.lifecycleOwner = this // we now set the contentview as the binding.root

        initObserver()

        //Calling Activity after splash screen

    }

    private fun initObserver() {
        splashViewModel.preferenceUserStatus.observe(this){
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        it.data?.let {user->
                            openingActivity(user.accessToken)
                        }
                    }
                }
                is Resource.Error -> {
                    toast(it.message!!)
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                splashViewModel.fetchPreferenceUserDetails()
            }
        },3000)


    }

    private fun openingActivity(accessToken: String) {

        if(accessToken.isNullOrEmpty()){
            val intent=Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}