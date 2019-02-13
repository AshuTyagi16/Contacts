package com.pratilipi.contacts.di.module

import android.content.Context
import com.pratilipi.contacts.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    @ApplicationScope
    fun context(): Context {
        return context
    }
}