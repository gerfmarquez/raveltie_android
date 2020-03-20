package com.maxor.raveltie.score


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED  &&
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ) {
            startService()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                5)
        }
    }

    override fun showScore(score: Int) {
        tv_score.text = score.toString()
    }

    override fun showErrorScore() {
    }
    override fun showConfig(config: RaveltieConfig) {
        if(config.killswitch) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("We apologize for the inconvenience, temporarily out of service.")
                .setOnCancelListener {
                    stopService()
                    finish()}
            .create().show()
        } else if (config.minReqVers.toFloat() > BuildConfig.VERSION_NAME.toFloat()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Please update app on Google Play Store.")
                .setOnCancelListener {
                    finish()
                }
                .create().show()
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

    override fun onStop() {
        super.onStop()
        stopService()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 5) {
            grantResults.forEach { grantResult ->
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    startService()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        ||  !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        finish()
                        return
                    }
                }
            }

        }
    }
    fun startService() {
        var extras = Bundle()
        extras!!.putString(LocationService.MODE,LocationService.MODE_START)
        val raveltieService = Intent(this, LocationService::class.java)
        raveltieService.putExtras(extras)
        ContextCompat.startForegroundService(this,raveltieService)
    }
    fun stopService() {
        var extras = Bundle()
        extras!!.putString(LocationService.MODE,LocationService.MODE_STOP)
        val raveltieService = Intent(this, LocationService::class.java)
        raveltieService.putExtras(extras)
        ContextCompat.startForegroundService(this,raveltieService)
    }
}
