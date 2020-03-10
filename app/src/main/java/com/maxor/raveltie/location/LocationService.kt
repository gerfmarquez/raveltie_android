package com.maxor.raveltie.location;

import android.content.Intent
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
    }

    override fun onCreate() {
        super.onCreate()
        NotificationUtil.setupForegroundNotification(this)
        locationInteractor.reportLocation(UniqueDeviceID.getUniqueId())
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
