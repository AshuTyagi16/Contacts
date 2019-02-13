package com.pratilipi.contacts.di.component

import com.pratilipi.contacts.di.module.ViewModelFactoryModule
import com.pratilipi.contacts.di.module.ViewModelModule
import com.pratilipi.contacts.di.scope.ActivityScope
import com.pratilipi.contacts.ui.contacts.addcontact.AddContactFragment
import dagger.Component

@ActivityScope
@Component(
    modules = [ViewModelFactoryModule::class, ViewModelModule::class],
    dependencies = [ContactsApplicationComponent::class]
)
interface AddContactFragmentComponent {
    fun injectAddContactFragment(addContactFragment: AddContactFragment)
}