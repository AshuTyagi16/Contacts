package com.pratilipi.contacts.di.component

import android.content.Context
import com.pratilipi.contacts.di.module.ContactsUtilModule
import com.pratilipi.contacts.di.module.ContextModule
import com.pratilipi.contacts.di.scope.ApplicationScope
import com.pratilipi.contacts.util.ContactUtil
import dagger.Component

@Component(modules = [ContextModule::class, ContactsUtilModule::class])
@ApplicationScope
interface ContactsApplicationComponent {

    fun context(): Context

    fun contactUtil(): ContactUtil
}