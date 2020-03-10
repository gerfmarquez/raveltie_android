package com.maxor.raveltie.location

import com.maxor.raveltie.RaveltieApp
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
abstract class LocationModule {
    @ContributesAndroidInjector
    abstract fun bindLocationService() : LocationService
}