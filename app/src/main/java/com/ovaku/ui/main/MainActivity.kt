package com.ovaku.ui.main

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.ovaku.R
import com.ovaku.data.models.event.EventPayload
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.databinding.ActivityMainBinding
import com.ovaku.ui.auth.AuthActivity
import com.ovaku.ui.main.adapter.EventAdapter
import com.ovaku.utils.MyProgressDialog
import com.ovaku.utils.ext.*
import com.ovaku.utils.imageUtils.ImageUtilsCameraGallery
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ImageUtilsCameraGallery.ImageAttachmentListener {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel>()
    private val navController by lazy { findNavController(R.id.mainHostFragment) }
    private var eventAdapter: EventAdapter? = null
    private var eventPayloadList = mutableListOf<EventPayload>()
    private var recyclerViewVisible = false
    private var id = 0
    private var authToken = ""
    private var progressDialog: MyProgressDialog? = null
    private var isProfile = false
    private val imageUtils: ImageUtilsCameraGallery by lazy {
        ImageUtilsCameraGallery(this, false, this, false)
    }
    private var listener = { from: Int, filename: String?, file: Bitmap?, uri: Uri?, count: Int -> Unit }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        initNavComponent()
        initAdapter()
        initListener()
        iniObserver()
    }

    private fun iniObserver() {

        mainViewModel.progressView.observe(this){
            showProgress(it)
        }

        mainViewModel.preferenceUserStatus.observe(this){
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        it.data?.let {user->
                            id = user.id
                            authToken = user.accessToken
                            mainViewModel.fetchAllEvent(id,authToken)
                        }
                    }
                }
                is Resource.Error -> {
                    toast(it.message!!)
                }
            }
        }

        mainViewModel.fetchAllEventStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    lifecycleScope.launch {
                        it.data?.let {data->
                            eventPayloadList.clear()
                            eventPayloadList.addAll(data.payload)
                            mainViewModel.eventId.value = eventPayloadList[0].id
                            eventAdapter?.updateData(eventPayloadList)
                        }
                    }
                }
                is Resource.Error -> {
                    mainViewModel.progressView.value = false
                    toast(it.message!!)
                }
            }
        }

        mainViewModel.logoutStatus.observe(this){_it->
            when (_it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        _it.data.let {
                            Toast(this@MainActivity).showSuccessCustomToast("Logout Successfully", this@MainActivity)
                            finish()
                            gotToActivity(AuthActivity::class.java)
                        }
                    }
                }
                is Resource.Error -> {
                    toast(_it.message!!)
                }
            }
        }

        lifecycleScope.launch {
            mainViewModel.fetchPreferenceUserDetails()
        }

    }

    private fun initAdapter() {
        eventAdapter = EventAdapter(eventPayloadList, object: EventAdapter.OnInteraction{
            override fun onItem(eventPayload: EventPayload, position: Int) {
                mainViewModel.eventId.value = eventPayload.id
            }
        })
        binding.rvImageList.adapter = eventAdapter
    }

    private fun initNavComponent() {
        binding.bottomNavigationView.itemIconTintList = null
        setupWithNavController(binding.bottomNavigationView, navController)
        binding.bottomNavigationView.inflateMenu(R.menu.menu)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.homeFragment, R.id.likeFragment, R.id.dislikeFragment, R.id.favouriteFragment -> {
                    isProfile = false
                    binding.bottomNavigationView.visible()
                    binding.toolbarLayout.ivGallery.visible()
                    binding.toolbarLayout.ivProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.profile))
                }
                R.id.profileFragment -> {
                    isProfile = true
                    binding.bottomNavigationView.gone()
                    binding.toolbarLayout.ivGallery.gone()
                    binding.toolbarLayout.ivProfile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.circle_logout))
                }
            }
        }
    }

    private fun initListener() {

        binding.toolbarLayout.ivGallery.setOnClickListener {
            if(!recyclerViewVisible){
                recyclerViewVisible = true
                binding.rvImageList.visible()
            }else{
                recyclerViewVisible = false
                binding.rvImageList.gone()
            }
        }
        binding.toolbarLayout.ivProfile.setOnClickListener {
            if(isProfile){
                mainViewModel.logout()
            }else {
                navController.navigate(R.id.profileFragment)
            }
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

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "onStart: ")
    }

    fun getImageFromUserSelected(
        toChooseMultipleImage: Boolean,
        listener: (from: Int, filename: String?, file: Bitmap?, uri: Uri?, count: Int) -> Unit
    ) {
        this.listener = listener
        openImageIntent(toChooseMultipleImage)
    }

    private fun openImageIntent(toChooseMultipleImage: Boolean) {
        imageUtils.imagePicker(0)
    }

    override fun image_attachment(from: Int, filename: String?, file: Bitmap?, uri: Uri?, count: Int) {
        listener(from, filename, file, uri, count)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when(requestCode){
                0 -> {
                    imageUtils.onActivityResult(requestCode, resultCode, data)
                    mainViewModel.imageChoose.value = 0
                }
                1 -> {
                    imageUtils.onActivityResult(requestCode, resultCode, data)
                    mainViewModel.imageChoose.value = 1
                }
            }
        }catch (ex: Exception){
            ex.printStackTrace()
        }
    }
}