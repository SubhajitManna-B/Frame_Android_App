package com.ovaku.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ovaku.R

class MyProgressDialog(mContext: Context) : Dialog(mContext, R.style.RoundedCornersDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_my_progress)

        setCancelable(false)
    }
}