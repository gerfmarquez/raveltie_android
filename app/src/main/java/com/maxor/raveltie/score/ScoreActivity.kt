package com.maxor.raveltie.score


import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.view.View

import androidx.core.content.ContextCompat
import com.maxor.raveltie.BuildConfig
import com.maxor.raveltie.FirebaseAnalyticsUtil
import com.maxor.raveltie.R
import com.maxor.raveltie.RaveltieConfig
import com.maxor.raveltie.location.LocationService
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

class ScoreActivity : DaggerAppCompatActivity(), ScoreView {
    @Inject
    lateinit var scorePresenter: ScorePresenter
    @Inject
    lateinit var firebaseAnalyticsUtil: FirebaseAnalyticsUtil

    private var finePermissionGrantedCallback : () -> Unit =  {
        startRaveltieService()
    }
    override fun onStart() {
        super.onStart()
        checkPermissionsGranted()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scorePresenter.bindView(this)
    }
    override fun onPause() {
        super.onPause()
        scorePresenter.cleanup()
    }
    override fun onResume() {
        super.onResume()
        scorePresenter.presentScore()
        scorePresenter.presentRank()
        scorePresenter.presentConfig()
    }
    override fun showScore(score: Int) {
        tv_score.text = score.toString()
    }
    override fun showRank(rank: Int, rankRaveltie: Int, rankUsers: Int) {
        tv_rank_ravelties.text = rankRaveltie.toString()
        tv_raveltie_users.text = rankUsers.toString()
    }
    override fun showErrorScore() {
        tv_score.text = "100"
    }

    override fun showErrorRank() {
        tv_rank_ravelties.text = "1"
        tv_raveltie_users.text = "1"


    }
    override fun showConfig(config: RaveltieConfig) {
        if(config.killswitch) {
            createAlertDialog("We apologize for the inconvenience, temporarily out of service.")
        } else if (config.minReqVers.toFloat() > BuildConfig.VERSION_NAME.toFloat()) {
            createAlertDialog("Please update app on Google Play Store.")
        }
    }
    fun quitRaveltie(view: View) {
        firebaseAnalyticsUtil.reportQuitCollecting()
        stopRaveltieService()
        finish()
    }
    private fun startRaveltieService() {
        var extras = Bundle()
        extras!!.putString(LocationService.MODE,LocationService.MODE_START)
        val raveltieService = Intent(this, LocationService::class.java)
        raveltieService.putExtras(extras)
        ContextCompat.startForegroundService(this,raveltieService)
    }
    private fun stopRaveltieService() {
        var extras = Bundle()
        extras!!.putString(LocationService.MODE,LocationService.MODE_STOP)
        val raveltieService = Intent(this, LocationService::class.java)
        raveltieService.putExtras(extras)
        startService(raveltieService)
    }
    private fun createAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setOnCancelListener {
                finish()
                stopRaveltieService()
            }.create().show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 5)
        for((index,permission) in permissions.withIndex())
        if(grantResults[index] == PERMISSION_GRANTED)
        when(permission) {
            ACCESS_FINE_LOCATION  ->
                checkBackgroundPermissionGranted()
            ACCESS_BACKGROUND_LOCATION ->
                finePermissionGrantedCallback()
        }
        else
        createAlertDialog("Permission needs to be accepted for Raveltie to work properly")
    }
    fun checkBackgroundPermissionGranted() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q )
            requestPermissions(arrayOf(ACCESS_BACKGROUND_LOCATION), 5)
         else
            finePermissionGrantedCallback()
    }
    fun checkPermissionsGranted()  {
        requestPermissions(arrayOf(ACCESS_FINE_LOCATION),5)
    }
}
