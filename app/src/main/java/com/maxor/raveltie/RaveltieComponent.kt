package com.maxor.raveltie

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component( modules = [AndroidInjectionModule::class,RaveltieModule::class])
interface RaveltieComponent  : AndroidInjector<RaveltieApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: RaveltieApp): Builder

        fun build(): RaveltieComponent
    }

    override fun inject(app: RaveltieApp)
}