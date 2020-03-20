package com.maxor.raveltie.score

import android.util.Log
import com.maxor.raveltie.RaveltieWebService
import com.maxor.raveltie.UniqueDeviceID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ScorePresenter
    @Inject constructor (val raveltieWebService: RaveltieWebService) {
    var scoreView: ScoreView? = null

    var rxDisposable : Disposable? = null
    var rxConfigDisposable : Disposable? = null

    fun bindView(view : ScoreView) {
        scoreView = view
    }
    fun presentScore() {
        rxDisposable =  raveltieWebService.pullScore(UniqueDeviceID.getUniqueId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { scoreResponse ->
                scoreView?.showScore(scoreResponse.score)
                cleanup()
            },  {   throwable ->
                    throwable.printStackTrace()
                scoreView?.showErrorScore()
                cleanup()
                } )
    }
    fun presentConfig() {
        rxConfigDisposable =  raveltieWebService.pullConfig()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { configResponse ->
                scoreView?.showConfig(configResponse)
                cleanupConfig()
            },  {   throwable ->
                throwable.printStackTrace()
                cleanupConfig()
            } )
    }
    fun cleanup() {
        if (rxDisposable != null  &&  rxDisposable?.isDisposed == false) {
            rxDisposable?.dispose()
        }
    }
    fun cleanupConfig() {
        if (rxConfigDisposable != null  &&  rxConfigDisposable?.isDisposed == false) {
            rxConfigDisposable?.dispose()
        }
    }
}