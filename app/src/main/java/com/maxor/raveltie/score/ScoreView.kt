package com.maxor.raveltie.score

import com.maxor.raveltie.RaveltieConfig

interface ScoreView {
    fun showScore(score : Int)
    fun showErrorScore()
    fun showConfig(config : RaveltieConfig)
}