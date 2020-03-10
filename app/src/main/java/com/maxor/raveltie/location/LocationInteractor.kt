package com.maxor.raveltie.location

import android.util.Log
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationInteractor @Inject constructor(
    private val raveltieWebService: RaveltieWebService,
    private val locationProvider: LocationProvider){

    val rxDisposables : ArrayList<Disposable> = ArrayList()

    fun reportLocation(imei: String) {
        requestLocation (imei){   locationData ->
            pushLocation(locationData)
        }
    }
    private fun requestLocation(imei:String, callback : (LocationData) -> Unit) {
        rxDisposables.add(locationProvider.requestLocation(imei)
            .repeatWhen { completed ->
                completed.delay(30, TimeUnit.SECONDS)  }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationData ->
                callback(locationData)
                Log.d("","")
            },  {   throwable ->
                throwable.printStackTrace()
            } ))
    }
    private fun pushLocation(locationData: LocationData) {
        rxDisposables.add(
            raveltieWebService.pushLocation(
                locationData.imei, locationData.timestamp, locationData.lat
                ,locationData.lon, locationData.accuracy)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationResponse ->
                Log.d("","")
            },  {   throwable ->
                throwable.printStackTrace()

            } ))
    }

    fun cleanup() {
        rxDisposables.forEach { disposable ->
            if ( disposable.isDisposed ) {
                disposable.dispose()
            }
        }

    }
}