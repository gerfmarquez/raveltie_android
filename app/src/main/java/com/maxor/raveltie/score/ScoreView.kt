package com.maxor.raveltie.score

import com.maxor.raveltie.RaveltieConfig

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

interface ScoreView {
    fun showScore(score : Int)
    fun showRank(rank : Int, rankRaveltie: Int, rankUsers: Int)
    fun showErrorScore()
    fun showErrorRank()
    fun showConfig(config : RaveltieConfig)
}