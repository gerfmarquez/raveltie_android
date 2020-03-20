package com.maxor.raveltie.location;

import android.content.Intent
import android.media.MediaCodec.MetricsConstants.MODE
import android.os.Bundle
import android.os.IBinder
import com.maxor.raveltie.NotificationUtil
import com.maxor.raveltie.UniqueDeviceID
import dagger.android.DaggerService
import javax.inject.Inject


class LocationService : DaggerService() {
    @Inject
    lateinit var locationInteractor: LocationInteractor
    companion object {
        const val ONGOING_NOTIFICATION_ID: Int = 1
        const val  MODE : String = "MODE"
        const val MODE_START : String = "START"
        const val MODE_STOP : String = "STOP"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null) {
            val extras: Bundle? = intent.extras
            if(extras?.getString(MODE)?.equals(MODE_START) == true) {
                NotificationUtil.setupForegroundNotification(this)
                locationInteractor.reportLocation(UniqueDeviceID.getUniqueId())
            } else if(extras?.getString(MODE)?.equals(MODE_STOP) == true) {
                locationInteractor.cleanup()
                stopForeground(true)
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
