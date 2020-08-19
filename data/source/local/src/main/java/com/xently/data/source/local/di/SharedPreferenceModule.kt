package com.xently.data.source.local.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.xently.common.di.qualifiers.EncryptedSharedPreference
import com.xently.common.di.qualifiers.UnencryptedSharedPreference
import com.xently.common.utils.ENCRYPTED_SHARED_PREFERENCE_KEY
import com.xently.common.utils.UNENCRYPTED_SHARED_PREFERENCE_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object SharedPreferenceModule {
    @Provides
    @EncryptedSharedPreference
    @Singleton
    fun provideEncryptedSharedPreference(
        @ApplicationContext context: Context
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        ENCRYPTED_SHARED_PREFERENCE_KEY,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Provides
    @UnencryptedSharedPreference
    @Singleton
    fun provideUnencryptedSharedPreference(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences(UNENCRYPTED_SHARED_PREFERENCE_KEY, Context.MODE_PRIVATE)
}