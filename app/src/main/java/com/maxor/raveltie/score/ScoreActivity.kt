package com.maxor.raveltie.score


import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.maxor.raveltie.BuildConfig
import com.maxor.raveltie.R
import com.maxor.raveltie.RaveltieConfig
import com.maxor.raveltie.location.LocationService
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class ScoreActivity : DaggerAppCompatActivity(), ScoreView {
    @Inject
    lateinit var scorePresenter: ScorePresenter

    override fun onStart() {
        super.onStart()
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION),5)

    }
    override fun showScore(score: Int) {
        tv_score.text = score.toString()
    }
    override fun showErrorScore() {
    }
    override fun showConfig(config: RaveltieConfig) {
        if(config.killswitch) {
            createAlertDialog("We apologize for the inconvenience, temporarily out of service.")
        } else if (config.minReqVers.toFloat() > BuildConfig.VERSION_NAME.toFloat()) {
            createAlertDialog("Please update app on Google Play Store.")
        }
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
        scorePresenter.presentConfig()
    }
    fun quitRaveltie(view: View) {
        stopService()
        finish()
    }
    private fun startService() {
        var extras = Bundle()
        extras!!.putString(LocationService.MODE,LocationService.MODE_START)
        val raveltieService = Intent(this, LocationService::class.java)
        raveltieService.putExtras(extras)
        ContextCompat.startForegroundService(this,raveltieService)
    }
    private fun stopService() {
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
                stopService()
            }.create().show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 5)
        for((index,permission) in permissions.withIndex())
        if(grantResults[index] == PERMISSION_GRANTED)
        when(permission) {
            ACCESS_FINE_LOCATION  ->
                requestPermissions(arrayOf(ACCESS_BACKGROUND_LOCATION),5)
            ACCESS_BACKGROUND_LOCATION ->
                startService()
        }
        else
        createAlertDialog("Permission needs to be accepted for Raveltie to work properly")
    }
}
