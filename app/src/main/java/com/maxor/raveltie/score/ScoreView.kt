package com.maxor.raveltie.score

import com.maxor.raveltie.RaveltieConfig

interface ScoreView {
    fun showScore(score : Int)
    fun showRank(rank : Int, rankRaveltie: Int, rankUsers: Int)
    fun showErrorScore()
    fun showErrorRank()
    fun showConfig(config : RaveltieConfig)
}