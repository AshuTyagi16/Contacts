package com.pratilipi.contacts.di.component

import com.pratilipi.contacts.di.module.MainActivityModule
import com.pratilipi.contacts.di.module.ViewModelFactoryModule
import com.pratilipi.contacts.di.module.ViewModelModule
import com.pratilipi.contacts.di.scope.ActivityScope
import com.pratilipi.contacts.ui.contacts.MainActivity
import dagger.Component

@ActivityScope
@Component(
    modules = [MainActivityModule::class, ViewModelFactoryModule::class, ViewModelModule::class],
    dependencies = [ContactsApplicationComponent::class]
)
interface MainActivityComponent {
    fun injectMainActivity(mainActivity: MainActivity)
}