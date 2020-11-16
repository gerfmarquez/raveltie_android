package com.maxor.raveltie.location

import android.util.Log
import com.maxor.raveltie.FirebaseAnalyticsUtil
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

import javax.inject.Inject
import kotlin.collections.ArrayList

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

class LocationInteractor @Inject constructor(
    private val raveltieWebService: RaveltieWebService,
    private val locationProvider: LocationProvider,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil){

    val rxDisposables : ArrayList<Disposable> = ArrayList()

    private val rate = 30
    private val throttleRate = 60

    private var detectDelay = Date().time

    private var throttleTime = AtomicLong(Date().time)
    private var isThrottle: AtomicBoolean = AtomicBoolean(false)
    private var throttleCount = AtomicInteger(0)

    fun reportLocation(imei: String) {
        requestLocation (imei){   locationData ->
            pushLocation(locationData)
        }
    }
    private fun requestLocation(imei:String, callback : (LocationData) -> Unit) {

        var disposable = Observable.interval(rate.toLong(),TimeUnit.SECONDS)
            .flatMap {
                Log.d("Location","$it")

                detectDelayRequest() { elapsedSeconds ->
                    Log.d("Location","Elapsed Seconds: $elapsedSeconds")
                }
                locationProvider.requestLocation(imei)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { locationData ->

                throttleResponse(Date().time, throttleCount, isThrottle, throttleTime) {
                    //cancel all callbacks if spammed by energy saver all at once
                    callback(locationData)
                }

                val time = SimpleDateFormat("HH:mm").format(Date(locationData.timestamp.toLong()))
                Log.d("Location:","Location: ${locationData.accuracy} ${time}")
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
            if (!disposable.isDisposed ) {
                disposable.dispose()
            }
        }
    }

    private fun detectDelayRequest(callback: (Int)->Unit) {

        val elapsedSeconds = (Date().time - detectDelay).toInt() / 1000
        if (elapsedSeconds > throttleRate) {

            firebaseAnalyticsUtil.reportDelayDetected(elapsedSeconds)
        }
        detectDelay = Date().time

        callback(elapsedSeconds)
    }

    private fun throttleResponse(currentMillis: Long, throttleCount: AtomicInteger,
                            isThrottle: AtomicBoolean, throttleTime: AtomicLong, callback: ()-> Unit) {

        val throttleEnded = currentMillis - throttleTime.get() > throttleRate
        if(throttleEnded) {

            callback()
            if(isThrottle.compareAndSet(true,false)) {

                firebaseAnalyticsUtil.reportLocationThrottle(throttleCount.get())
            }
        } else {

            throttleCount.incrementAndGet()
            if(isThrottle.compareAndSet(false,true)) {

                throttleCount.set(1)
            }
            //ignore callback
        }

        throttleTime.set(currentMillis)
    }


}