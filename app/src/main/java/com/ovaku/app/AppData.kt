package com.ovaku.app

import android.annotation.SuppressLint
import android.view.View

object AppData {

    /*val imageList = arrayListOf(R.drawable.banner, R.drawable.banner1, R.drawable.banner3)*/

    const val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    /* UserType */
    const val USER_TYPE = "User Type"
    const val USER = "User"
    const val SERVICEMAN = "SERVICEMAN"

    /*Preference Key*/
    const val UID = "id"
    const val NAME = "name"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val IMAGE = "image"
    const val ADDRESS = "address"
    const val PINCODE = "pincode"
    const val DISTRICT = "district"
    const val STATE = "state"
    const val WHATSAPP_NO = "whatsapp_no"
    const val KYC_STATUS = "kyc_status"
    const val NOTIFICATION_COUNT = "notification_count"
    const val USER_TYPE_SP = "userType"

    /* Other */
    const val HOME_FRAGMENT = "HomeFragment"
    const val SERVICE_FRAGMENT = "ServiceFragment"

    /* Profile */
    const val MOBILE = "Mobile"
    const val EMAIL_ID = "Email"

    /* Image Request Body */
    const val BLANK_REQUEST_BODY = "image/*"
    const val REQUEST_BODY_TEXT = "text/plain"
    const val REQUEST_BODY_JSON = "application/json; charset=utf-8"

    /* Intent */
    const val POLICY = "Policy"


    object IMAGE_PICK_TYPE {
        const val CAMERA = "Camera"
        const val GALLERY = "Gallery"
    }
}