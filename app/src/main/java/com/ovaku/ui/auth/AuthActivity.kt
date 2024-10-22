package com.ovaku.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import com.ovaku.R
import com.ovaku.databinding.ActivityAuthBinding
import com.ovaku.utils.MyProgressDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val authViewModel by viewModels<AuthViewModel>()
    private var progressDialog: MyProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        binding.lifecycleOwner = this

        initObserver()
    }

    /** Observer Fetch Data From Response */
    private fun initObserver() {
        authViewModel.progressView.observe(this){
            showProgress(it)
        }
    }

    /** Progress Dialog Show
     * @param showProgress - This Is Boolean Value If It Is True Than Show Progress Dialog Else Hide Progress Dialog */
    private fun showProgress(showProgress: Boolean) {
        try {
            progressDialog = progressDialog ?: MyProgressDialog(this)
            progressDialog?.let {
                if (showProgress && !it.isShowing) {
                    it.show()
                } else if (it.isShowing) {
                    it.dismiss()
                }
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }
}