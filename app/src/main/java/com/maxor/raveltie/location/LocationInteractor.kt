package com.maxor.raveltie.location

import android.util.Log
import com.maxor.raveltie.FirebaseAnalyticsUtil
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.Observable
import io.reactivex.ObservableSource
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

class LocationInteractor @Inject constructor(
    private val raveltieWebService: RaveltieWebService,
    private val locationProvider: LocationProvider,
    private val firebaseAnalyticsUtil: FirebaseAnalyticsUtil){

    val rxDisposables : ArrayList<Disposable> = ArrayList()

    private val rate = 30
    private val throttleRate = 60

    private var powerSavingIdleResume = Date().time

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

                powerSavingRequest(locationProvider.requestLocation(imei)) { elapsedSeconds ->
                    Log.d("Location","Elapsed Seconds: $elapsedSeconds")
                }
            }
            .subscribeOn(Schedulers.trampoline())
            .observeOn(Schedulers.newThread())
            .subscribe( { locationData ->

                throttleResponse(Date().time, throttleCount, isThrottle, throttleTime) {
                    //cancel all callbacks if spammed by energy saver all at once
                    callback(locationData)
                }

                val time = SimpleDateFormat("HH:mm").format(Date(locationData.timestamp.toLong()))
                Log.d("Location:","Location: $time")
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
    //@TODO Warn user there aren't gps locations available? or phone in energy saving mode?
    private fun powerSavingRequest(locationRequest: ObservableSource<LocationData>,
                               callback: (Long)->Unit): ObservableSource<LocationData> {

        var locationObservable: ObservableSource<LocationData>
        val elapsedSeconds = (Date().time - powerSavingIdleResume) / 1000

        locationObservable = when {
            //Request only above or below Throttle Rate
            (elapsedSeconds < throttleRate) -> locationRequest
            (elapsedSeconds > (throttleRate * 60)) -> {
                powerSavingIdleResume = Date().time
                locationRequest
            }
            else -> locationRequest//Observable.empty()
        }

        callback(elapsedSeconds)
        return locationObservable
    }

    private fun throttleResponse(currentMillis: Long, throttleCount: AtomicInteger,
                            isThrottle: AtomicBoolean, throttleTime: AtomicLong, callback: ()-> Unit) {

        val throttleEnded = currentMillis - throttleTime.get() > rate
        if(throttleEnded) {

            powerSavingIdleResume = Date().time
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