package com.maxor.raveltie.location

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.maxor.raveltie.RaveltieApp
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class LocationProvider @Inject constructor (val raveltieApp: RaveltieApp) {

    fun requestLocation(imei: String) : Observable<LocationData> {

        return Observable.fromPublisher<LocationData> { subscriber ->
            catching( { location ->
                subscriber.onNext(LocationData(
                    imei,
                    location.time.toString(),
                    location.latitude,
                    location.longitude,
                    location.accuracy))
                subscriber.onComplete()
            }, {    throwable ->
                subscriber.onError(throwable)
            })
        }
    }
    private fun catching(callback: (Location) -> Unit, error : (Throwable)-> Unit) {
        try {
            requesting(callback)
        } catch(securityException : SecurityException) {
            securityException.printStackTrace(System.err)
            error(securityException)
        } catch(interruptedException : InterruptedException) {
            interruptedException.printStackTrace(System.err)
            error(interruptedException)
        }
    }
    @Throws(SecurityException::class)
    private fun requesting(callback: (Location) -> Unit)  {
        val locationManager = raveltieApp.applicationContext
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestSingleUpdate( LocationManager.GPS_PROVIDER,
            listening { location ->
                callback(location)
            }
            , Looper.getMainLooper())
    }
    private fun listening(callback: (Location) -> Unit) : LocationListener {
        return object : LocationListener {
            override fun onLocationChanged(location: Location) {
                callback(location)
            }
            override fun onProviderDisabled(p0: String?) {
            }
            override fun onProviderEnabled(p0: String?) {
            }
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            }
        }
    }

}