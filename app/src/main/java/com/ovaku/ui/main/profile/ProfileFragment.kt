package com.ovaku.ui.main.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ovaku.R
import com.ovaku.app.AppData.BLANK_REQUEST_BODY
import com.ovaku.data.models.profileDetails.ProfileAddress
import com.ovaku.data.models.profileDetails.ProfilePayload
import com.ovaku.data.models.responseModel.Resource
import com.ovaku.databinding.FragmentProfileBinding
import com.ovaku.ui.main.MainActivity
import com.ovaku.ui.main.MainViewModel
import com.ovaku.utils.decodeBitmapUri
import com.ovaku.utils.ext.*
import com.ovaku.utils.fixBitmapOrientation
import com.ovaku.utils.permissionHandler.DENIED
import com.ovaku.utils.permissionHandler.EXPLAINED
import com.ovaku.utils.permissionHandler.goToAppDetailsSettings
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()
    private var userId = -1
    private var authToken = ""
    private var addressId = -1
    private var password = ""
    private var id = -1
    private var isActive = true
    private var profileImageUrl = ""
    private var imageFrom: Int? = null
    private var imageFilename: String? = null
    private var mImageFile: Bitmap? = null
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var file: File? = null

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result->
        //Filter elements whose value is false and convert them to list
        val deniedList = result.filter { !it.value }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                //Group the rejected all list, and the grouping condition is whether to check and do not ask again
                val map = deniedList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                // Rejected and unchecked Do not ask again
                map[DENIED]?.let {
                    requireContext().showOkayAlertFunction(
                        "Permission Refused",
                        "Kindly allow permission from app setting without this permission app would not work properly.",
                        positiveBtnClick = { goToAppDetailsSettings() }
                    )
                }
                // Rejected and checked Do not ask again
                map[EXPLAINED]?.let {
                    requireContext().showOkayAlertFunction(
                        "Permission Refused",
                        "Kindly allow permission from app setting without this permission app would not work properly.",
                        positiveBtnClick = { goToAppDetailsSettings() }
                    )
                }
            }
            else -> {
                mainViewModel.imageChoose.value = -1
                capturePhoto()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.profileViewModel = profileViewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initObserver()

    }

    private fun initListener() {
        binding.profileImage.setOnClickListener {
            var permissionList = arrayOf(Manifest.permission.CAMERA)
            permissionList += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            permissionLauncher.launch(permissionList)
        }

        binding.btnUpdate.setOnClickListener {
            when {
                binding.etFirstName.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter first name.")
                binding.etLastName.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter last name.")
                binding.etPhoneNo.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter phone no")
                binding.etPhoneNo.text?.trim()?.toString()?.length != 10 -> requireContext().showCustomOkAlertFunction(message = "Please enter valid phone no")
                binding.etEmail.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Enter Email Id.")
                !emailValid(binding.etEmail.text.toString()) -> requireContext().showCustomOkAlertFunction(message = "Enter Valid Email Id.")
                binding.etAddressLine1.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter address line no 1.")
                binding.etCity.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter city")
                binding.etDistrict.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter district")
                binding.etState.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter state")
                binding.etCountry.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter country")
                binding.etPincode.isEmpty() -> requireContext().showCustomOkAlertFunction(message = "Please enter PIN code")
                binding.etPincode.text?.trim()?.toString()?.length != 6 -> requireContext().showCustomOkAlertFunction(message = "Please enter valid PIN code")
                else ->{
                    val profilePayload = ProfilePayload(
                        firstName = binding.etFirstName.text?.trim().toString(),
                        lastName = binding.etLastName.text?.trim().toString(),
                        phoneNo = binding.etPhoneNo.text?.trim().toString().toLong(),
                        email = binding.etEmail.text?.trim().toString(),
                        password = password,
                        isActive = isActive,
                        id = id,
                        profileImageUrl = profileImageUrl,
                        name = "${binding.etFirstName.text?.trim().toString()}  ${binding.etLastName.text?.trim().toString()}",
                        address = ProfileAddress(
                            id = addressId,
                            line1 = binding.etAddressLine1.text?.trim().toString(),
                            line2 = binding.etAddressLine2.text?.trim().toString(),
                            city = binding.etCity.text?.trim().toString(),
                            district = binding.etDistrict.text?.trim().toString(),
                            state = binding.etState.text?.trim().toString(),
                            country = binding.etCountry.text?.trim().toString(),
                            pincode = binding.etPincode.text?.trim().toString().toInt()
                        )
                    )
                    profileViewModel.updateProfile(userId, profilePayload = profilePayload, authToken = authToken)
                }
            }
        }
    }

    private fun capturePhoto() {
        (requireActivity() as MainActivity).getImageFromUserSelected(false)
        { from: Int, filename: String?, file: Bitmap?, uri: Uri?, count: Int ->
            imageFrom = from
            imageFilename = filename
            mImageFile = file
            imageUri = uri
            Log.d("TAG", "capturePhoto: $uri")
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON) //Enable Image Guidelines
                .setFixAspectRatio(true)
                .start(requireContext(), this)
        }
    }

    private fun initObserver() {

        mainViewModel.preferenceUserStatus.observe(this){
            when (it) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    lifecycleScope.launch {
                        it.data?.let {user->
                            userId = user.id
                            authToken = user.accessToken
                        }
                    }
                }
                is Resource.Error -> {
                    toast(it.message!!)
                }
            }
        }

        profileViewModel.fetchProfileStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    lifecycleScope.launch {
                        it.data?.payload?.let {user->

                            addressId = user.address.id
                            password = user.password
                            profileImageUrl = user.profileImageUrl?:""
                            if(profileImageUrl.isEmpty()){
                                Glide.with(requireContext())
                                    .load(R.drawable.profile)
                                    .apply(RequestOptions.skipMemoryCacheOf(true))
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .placeholder(R.color.purple_200)
                                    .into(binding.profileImage)
                            }else {
                                Log.d("TAG", "initObserver: $profileImageUrl")
                                Glide.with(binding.profileImage)
                                    .load(profileImageUrl)
                                    .apply(RequestOptions.skipMemoryCacheOf(true))
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .placeholder(R.color.purple_200)
                                    .into(binding.profileImage)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    mainViewModel.progressView.value = false
                    toast(it.message!!)
                }
            }
        }

        profileViewModel.updateProfileStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    Toast(requireContext()).showSuccessCustomToast("Profile Update Successfully", requireActivity())
                }
                is Resource.Error -> {
                    mainViewModel.progressView.value = false
                    toast(it.message!!)
                }
            }
        }

        profileViewModel.updateProfileImageStatus.observe(this){
            when (it) {
                is Resource.Loading -> {
                    mainViewModel.progressView.value = true
                }
                is Resource.Success -> {
                    mainViewModel.progressView.value = false
                    Toast(requireContext()).showSuccessCustomToast("Profile Image Update Successfully", requireActivity())
                }
                is Resource.Error -> {
                    mainViewModel.progressView.value = false
                    toast(it.message!!)
                }
            }
        }

        lifecycleScope.launch {
            delay(500)
            mainViewModel.eventId.observe(viewLifecycleOwner){id->
                profileViewModel.fetchProfile(userId = userId, authToken = authToken)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {

                val resultUri = result.uri
                bitmap = requireContext().decodeBitmapUri(resultUri)
                Log.d("TAG", "onActivityResult: ${imageUri?.path}")
                bitmap = fixBitmapOrientation(requireContext(), imageUri!!, bitmap, mainViewModel.imageChoose.value)
                binding.profileImage.loadImageWithCircle(bitmap!!)
                file = requireContext().BitmapToFile(bitmap!!, "${System.currentTimeMillis()}.jpg")
                val requestBody1: RequestBody = file?.asRequestBody(BLANK_REQUEST_BODY.toMediaTypeOrNull())!!
                val fileToUpload = MultipartBody.Part.createFormData("file", file?.name, requestBody1)
                lifecycleScope.launch {
                    profileViewModel.updateProfileImage(fileToUpload, userId, authToken)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.d("TAG", "onActivityResult: $error")
            }
        }
    }
}