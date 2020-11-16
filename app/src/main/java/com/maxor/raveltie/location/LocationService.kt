package com.maxor.raveltie.location;

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.maxor.raveltie.NotificationUtil
import com.maxor.raveltie.UniqueDeviceID
import dagger.android.DaggerService
import javax.inject.Inject

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

class LocationService : DaggerService() {
    @Inject
    lateinit var locationInteractor: LocationInteractor

    companion object {
        const val ONGOING_NOTIFICATION_ID: Int = 1
        const val  MODE : String = "MODE"
        const val MODE_START : String = "START"
        const val MODE_STOP : String = "STOP"
    }
    var running = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(intent != null) {
            val extras: Bundle? = intent.extras
            if(extras?.getString(MODE)?.equals(MODE_START) == true && !running) {
                NotificationUtil.setupForegroundNotification(this)
                locationInteractor.reportLocation(UniqueDeviceID.getUniqueId())
                running = true
            } else if(extras?.getString(MODE)?.equals(MODE_STOP) == true) {
                locationInteractor.cleanup()
                stopForeground(true)
                stopSelf()
                running = false
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }
    override fun onDestroy() {
        super.onDestroy()
        locationInteractor.cleanup()
    }

    //@TODO use RX to schedule location retrieval and posting to lambda web service?
    //@TODO handle  stop of the service or allow users to uninstall , reboot phone?

    override fun onBind(p0: Intent?): IBinder? {
        return null //NOP
    }

}
