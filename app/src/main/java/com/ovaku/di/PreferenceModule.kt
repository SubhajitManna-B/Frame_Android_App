package com.ovaku.di

import android.content.Context
import android.content.SharedPreferences
import com.ovaku.data.dataSource.preferences.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Provides
    fun getPreference(@ApplicationContext applicationContext: Context): SharedPreferences {
        return PreferenceHelper.defaultPrefs(applicationContext)
    }
}
