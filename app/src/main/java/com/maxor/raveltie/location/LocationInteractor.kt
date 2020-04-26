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
    val rate = 3
    val initialTimestamp = Date().time
    var locationCount = 0

    fun reportLocation(imei: String) {
        requestLocation (imei){   locationData ->
            pushLocation(locationData)
        }
    }
    private fun requestLocation(imei:String, callback : (LocationData) -> Unit) {
        //TODO Improve this algorithm to keep track of the deviation of the 30 seconds period
        //TODO And add it  or subtract it from following dynamic intervals to adjust /makeup
        //TODO for unaccounted time
        var disposable = Observable.interval(rate.toLong(),TimeUnit.SECONDS)
            .flatMap {
                val elapsed = (Date().time - initialTimestamp)
                val elapsedRatio = ( elapsed / (rate * 1000) )
                val elapsedOffset = elapsedRatio - locationCount
                Log.d("###", "elapsed ratio: $elapsedRatio")
                Log.d("###", "Location Count: $locationCount")
                if(elapsedRatio > locationCount) {
                    val overRemnant = elapsedOffset/rate
                    Log.d("###", "overRemnant: $overRemnant")
                    if( overRemnant > 1) {

                    }
                } else {
                    Log.d("###", "<<< elapsed $elapsedRatio")
                }
                locationProvider.requestLocation(imei)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationData ->
                locationCount++
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
                Log.d("Push Location","Push Location")
            },  {   throwable ->
                throwable.printStackTrace()
            } ))
    }

    fun cleanup() {
        rxDisposables.forEach { disposable ->
            if ( !disposable.isDisposed ) {
                disposable.dispose()
            }
        }

    }
}