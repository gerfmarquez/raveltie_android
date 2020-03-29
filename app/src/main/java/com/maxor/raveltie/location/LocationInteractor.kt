package com.maxor.raveltie.location

import android.util.Log
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import kotlin.collections.ArrayList

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
        //TODO Improve this algorithm to keep track of the deviation of the 30 seconds period
        //TODO And add it  or subtract it from following dynamic intervals to adjust /makeup
        //TODO for unaccounted time
        var disposable = Observable.interval(30, TimeUnit.SECONDS)
            .flatMap {
                locationProvider.requestLocation(imei)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationData ->
                callback(locationData)
            },  {   throwable ->
                throwable.printStackTrace()
            } )
        rxDisposables.add(disposable)
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