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
    var rxRankDisposable : Disposable? = null
    var rxConfigDisposable : Disposable? = null

    fun bindView(view : ScoreView) {
        scoreView = view
    }
    fun presentScore() {
        rxDisposable =  raveltieWebService.pullScore(UniqueDeviceID.getUniqueId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { scoreResponse ->
                scoreView?.showScore(scoreResponse.score.toInt())
                cleanup(rxDisposable)
            },  {   throwable ->
                    throwable.printStackTrace()
                scoreView?.showErrorScore()
                cleanup(rxDisposable)
                } )
    }
    fun presentRank() {
        rxRankDisposable =  raveltieWebService.pullRank(UniqueDeviceID.getUniqueId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { rankResponse ->
                scoreView?.showRank(rankResponse.rank,
                    rankResponse.rankRavelties,
                    rankResponse.rankUsers)
                cleanup(rxRankDisposable)
            },  {   throwable ->
                throwable.printStackTrace()
                scoreView?.showErrorRank()
                cleanup(rxRankDisposable)
            } )
    }
    fun presentConfig() {
        rxConfigDisposable =  raveltieWebService.pullConfig()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { configResponse ->
                scoreView?.showConfig(configResponse)
                cleanup(rxConfigDisposable)
            },  {   throwable ->
                throwable.printStackTrace()
                cleanup(rxConfigDisposable)
            } )
    }
    fun cleanup(rxDisposable : Disposable?) {
        if (rxDisposable != null  &&  rxDisposable.isDisposed == false) {
            rxDisposable.dispose()
        }
    }
    fun cleanup() {
        if (rxDisposable != null  &&  rxDisposable?.isDisposed == false) {
            rxDisposable?.dispose()
        }
        if (rxRankDisposable != null  &&  rxRankDisposable?.isDisposed == false) {
            rxRankDisposable?.dispose()
        }
        if (rxConfigDisposable != null  &&  rxConfigDisposable?.isDisposed == false) {
            rxConfigDisposable?.dispose()
        }
    }
}