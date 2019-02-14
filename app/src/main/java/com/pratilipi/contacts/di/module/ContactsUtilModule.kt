package com.pratilipi.contacts.di.module

import android.content.Context
import com.pratilipi.contacts.di.scope.ApplicationScope
import com.pratilipi.contacts.util.ContactUtil
import dagger.Module
import dagger.Provides

@Module
class ContactsUtilModule {

    @Provides
    @ApplicationScope
    fun contactUtil(context: Context): ContactUtil {
        return ContactUtil(context)
    }
}