package com.maxor.raveltie.score

import android.util.Log
import com.maxor.raveltie.RaveltieWebService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ScorePresenter
    @Inject constructor (val raveltieWebService: RaveltieWebService) {
    var scoreView: ScoreView? = null

    var rxDisposable : Disposable? = null

    fun bindView(view : ScoreView) {
        scoreView = view
    }
    fun presentScore(imei: Int) {
        rxDisposable =  raveltieWebService.pullScore(imei)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { scoreResponse ->
                //scoreView?.showScore(scoreResponse.score)
                Log.d("","")
            },  {   throwable ->
                    throwable.printStackTrace()
                scoreView?.showErrorScore()
                } )

    }
    fun cleanup() {
        if (rxDisposable != null  &&  rxDisposable?.isDisposed == false) {
            rxDisposable?.dispose()
        }
    }
}