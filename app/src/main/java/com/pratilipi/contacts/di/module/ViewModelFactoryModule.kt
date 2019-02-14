package com.pratilipi.contacts.di.module

import dagger.Binds
import dagger.Module
import androidx.lifecycle.ViewModelProvider
import com.pratilipi.contacts.di.factory.DaggerViewModelFactory

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: DaggerViewModelFactory): ViewModelProvider.Factory
}