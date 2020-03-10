package com.maxor.raveltie.score

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ScoreModule {
    @ContributesAndroidInjector
    abstract fun  bindScoreActivity() : ScoreActivity
}