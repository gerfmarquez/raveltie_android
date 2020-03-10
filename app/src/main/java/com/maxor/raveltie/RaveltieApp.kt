package com.maxor.raveltie


import dagger.android.support.DaggerApplication

class RaveltieApp : DaggerApplication(){
    override fun applicationInjector() = DaggerRaveltieComponent.builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()

    }
}