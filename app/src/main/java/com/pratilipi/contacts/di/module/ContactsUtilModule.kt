package com.pratilipi.contacts.di.module

import android.content.Context
import com.pratilipi.contacts.di.scope.ApplicationScope
import com.pratilipi.contacts.util.Contactutil
import dagger.Module
import dagger.Provides

@Module
class ContactsUtilModule {

    @Provides
    @ApplicationScope
    fun contactUtil(context: Context): Contactutil {
        return Contactutil(context)
    }
}