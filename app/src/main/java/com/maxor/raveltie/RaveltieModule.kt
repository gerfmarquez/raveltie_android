package com.maxor.raveltie

import com.google.firebase.analytics.FirebaseAnalytics
import com.maxor.raveltie.location.LocationModule
import com.maxor.raveltie.location.LocationProvider
import com.maxor.raveltie.score.ScoreModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

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

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(raveltieApp: RaveltieApp) : FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(raveltieApp.applicationContext)
    }
}