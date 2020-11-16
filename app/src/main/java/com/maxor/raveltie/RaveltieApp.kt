package com.maxor.raveltie


import dagger.android.support.DaggerApplication

/** This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * Copyright 2020, Gerardo Marquez.
 */

class RaveltieApp : DaggerApplication(){
    override fun applicationInjector() = DaggerRaveltieComponent.builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()

    }
}