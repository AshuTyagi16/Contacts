package com.pratilipi.contacts

import android.app.Activity
import android.app.Application
import com.pratilipi.contacts.di.component.ContactsApplicationComponent
import com.pratilipi.contacts.di.component.DaggerContactsApplicationComponent
import com.pratilipi.contacts.di.module.ContextModule

class Contacts : Application() {

    private lateinit var component: ContactsApplicationComponent

    companion object {
        fun get(activity: Activity): Contacts {
            return activity.application as Contacts
        }
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerContactsApplicationComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }

    fun contactsApplicationComponent(): ContactsApplicationComponent {
        return component
    }
}