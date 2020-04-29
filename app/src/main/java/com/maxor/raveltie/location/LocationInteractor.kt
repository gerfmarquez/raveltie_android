package com.maxor.raveltie.location

import android.util.Log
import com.maxor.raveltie.FirebaseAnalyticsUtil
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

import javax.inject.Inject
import kotlin.collections.ArrayList

class LocationInteractor @Inject constructor(
    private val raveltieWebService: RaveltieWebService,
    private val locationProvider: LocationProvider,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil){

    val rxDisposables : ArrayList<Disposable> = ArrayList()
    private val rate = 30
    private val throttle = 60
    private val throttleCallback = 30
    private var timestampRequest = Date().time
    private var timestampCallback = Date().time
    private var isThrottleCallback = false
    private var throttleCount = 0

    fun reportLocation(imei: String) {
        requestLocation (imei){   locationData ->
            pushLocation(locationData)
        }
    }
    private fun requestLocation(imei:String, callback : (LocationData) -> Unit) {
        var disposable = Observable.interval(rate.toLong(),TimeUnit.SECONDS)
            .flatMap {
                var locationObservable: ObservableSource<LocationData>
                //@TODO Warn user there aren't gps locations available? or phone in energy saving mode?
                val elapsedSeconds = (Date().time - timestampRequest) / 1000
                locationObservable = when {
                    (elapsedSeconds < throttle) -> locationProvider.requestLocation(imei)
                    (elapsedSeconds > (throttle * 60)) -> {
                        timestampRequest = Date().time
                        locationProvider.requestLocation(imei)
                    }
                    else -> Observable.empty<LocationData>()
                }
                locationObservable
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationData ->
                //cancel all callbacks if spammed by energy saver all at once
                if(Date().time - timestampCallback > throttleCallback) {
                    timestampRequest = Date().time
                    callback(locationData)
                    if(isThrottleCallback) {
                        firebaseAnalyticsUtil.reportLocationThrottle(throttleCount)
                        isThrottleCallback = false
                    }
                } else {
                    throttleCount++
                    if( ! isThrottleCallback) {
                        isThrottleCallback = true
                        throttleCount = 1
                    }
                    //ignore callback
                }
                timestampCallback = Date().time

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

            },  {   throwable ->
                throwable.printStackTrace()
            } ))
    }

    fun cleanup() {
        rxDisposables.forEach { disposable ->
            if (  ! disposable.isDisposed ) {
                disposable.dispose()
            }
        }

    }
}