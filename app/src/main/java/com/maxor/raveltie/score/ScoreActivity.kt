package com.maxor.raveltie.score


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat
import com.maxor.raveltie.R
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
            val raveltieService = Intent(this, LocationService::class.java)
            ContextCompat.startForegroundService(this,raveltieService)
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                5)
        }

    }

    override fun onResume() {
        super.onResume()
        scorePresenter.presentScore(1234)
    }

    override fun showScore(score: Int) {
        tv_score.text = score.toString()
    }

    override fun showErrorScore() {

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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 5) {
            grantResults.forEach { grantResult ->
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    val raveltieService = Intent(this, LocationService::class.java)
                    ContextCompat.startForegroundService(this,raveltieService)
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
}
