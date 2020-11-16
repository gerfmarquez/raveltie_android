package com.maxor.raveltie.score

import dagger.Module
import dagger.android.ContributesAndroidInjector

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

@Module
abstract class ScoreModule {
    @ContributesAndroidInjector
    abstract fun  bindScoreActivity() : ScoreActivity
}