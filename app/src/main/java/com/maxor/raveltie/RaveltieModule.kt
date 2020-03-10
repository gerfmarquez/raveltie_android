package com.maxor.raveltie

import com.maxor.raveltie.location.LocationModule
import com.maxor.raveltie.location.LocationProvider
import com.maxor.raveltie.score.ScoreModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ScoreModule::class, LocationModule::class])
class RaveltieModule {
    @Singleton
    @Provides
    fun provideRaveltieWebService(): RaveltieWebService {
        return RaveltieWebService.create()
    }
    @Singleton
    @Provides
    fun provideLocationProvider(raveltieApp : RaveltieApp): LocationProvider {
        return LocationProvider(raveltieApp )
    }
}