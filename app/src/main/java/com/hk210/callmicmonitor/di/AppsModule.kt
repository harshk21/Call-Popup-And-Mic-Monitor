package com.hk210.callmicmonitor.di

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.view.WindowManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppsModule {

    @Provides
    @Singleton
    fun getContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    @Provides
    @Singleton
    fun provideTelephonyManager(@ApplicationContext context: Context): TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    @SuppressLint("InlinedApi")
    @Provides
    @Singleton
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    @Provides
    @Singleton
    fun provideWindowManager(@ApplicationContext context: Context): WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}
