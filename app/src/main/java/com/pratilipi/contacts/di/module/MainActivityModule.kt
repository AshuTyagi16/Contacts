package com.pratilipi.contacts.di.module

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratilipi.contacts.data.Contact
import com.pratilipi.contacts.di.scope.ActivityScope
import com.pratilipi.contacts.ui.base.ItemDecorator
import com.pratilipi.contacts.ui.contacts.ContactsAdapter
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule(private val context: Context) {

    @Provides
    @ActivityScope
    fun itemDecorator(): ItemDecorator {
        return ItemDecorator(10, 10, 50, 10)
    }

    @Provides
    @ActivityScope
    fun layoutManager(): LinearLayoutManager {
        return LinearLayoutManager(context)
    }

    @Provides
    @ActivityScope
    fun contactsAdapter(comparator: DiffUtil.ItemCallback<Contact>): ContactsAdapter {
        return ContactsAdapter(comparator)
    }

    @Provides
    @ActivityScope
    fun comparator(): DiffUtil.ItemCallback<Contact> {
        return object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem == newItem
            }

        }
    }
}