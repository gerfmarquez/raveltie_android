package com.maxor.raveltie

import android.media.MediaDrm
import android.os.Build
import java.security.MessageDigest
import java.util.*

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

object UniqueDeviceID {

    /**
     * UUID for the Widevine DRM scheme.
     * <p>
     * Widevine is supported on Android devices running Android 4.3 (API Level 18) and up.
     */
    fun getUniqueId(): String {
        val WIDEVINE_UUID = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
        var wvDrm: MediaDrm? = null
        try {
            wvDrm = MediaDrm(WIDEVINE_UUID)
            val widevineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            val md = MessageDigest.getInstance("SHA-256")
            md.update(widevineId)
            return md.digest().toHexString()
        } catch (e: Exception) {
            //WIDEVINE is not available
            return ""
        } finally {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                wvDrm?.close()
            } else {
                wvDrm?.release()
            }
        }
    }
    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}
