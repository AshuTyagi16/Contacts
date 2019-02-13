package com.pratilipi.contacts.di.module

import androidx.lifecycle.ViewModel
import com.pratilipi.contacts.di.mapkey.ViewModelKey
import com.pratilipi.contacts.ui.contacts.ContactsViewModel
import com.pratilipi.contacts.ui.contacts.addcontact.AddContactViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    abstract fun bindContactsViewModel(contactsViewModel: ContactsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddContactViewModel::class)
    abstract fun bindAddContactViewModel(addContactViewModel: AddContactViewModel): ViewModel
}