package com.maxor.raveltie

import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

class FirebaseAnalyticsUtil @Inject constructor(val firebaseAnalytics: FirebaseAnalytics) {

    fun reportLocationThrottle(throttleCount : Int) {
        val bundle = Bundle()
        bundle.putString("brand", Build.BRAND)
        bundle.putString("device", Build.DEVICE)
        bundle.putString("model", Build.MODEL)

        bundle.putInt("throttles",throttleCount)
        firebaseAnalytics.logEvent("throttle", bundle)
    }
    fun reportDelayDetected(delayed : Int) {
        val bundle = Bundle()

        bundle.putString("brand", Build.BRAND)
        bundle.putString("device", Build.DEVICE)
        bundle.putString("model", Build.MODEL)

        bundle.putInt("delayed", delayed)
        firebaseAnalytics.logEvent("delay", bundle)
    }
    fun reportQuitCollecting() {
        firebaseAnalytics.logEvent("btn_quit", null)

    }
}