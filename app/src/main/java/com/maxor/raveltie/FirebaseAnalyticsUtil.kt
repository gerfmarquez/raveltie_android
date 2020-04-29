package com.maxor.raveltie

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsUtil @Inject constructor(val firebaseAnalytics: FirebaseAnalytics) {

    fun reportLocationThrottle(throttleCount : Int) {
        val bundle = Bundle()
        bundle.putString("device",UniqueDeviceID.getUniqueId())
        bundle.putString("throttles",throttleCount.toString())
        firebaseAnalytics.logEvent("throttle", bundle)
    }
}