package com.pratilipi.contacts.util

import android.content.Context
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts


class ContactUtil(private val context: Context) {

    private lateinit var list: MutableList<Contact>
    private val numberList = ArrayList<String>()
    private lateinit var contact: Contact

    suspend fun getContacts(): List<Contact>? {
        return Contacts.getQuery().find()
    }
}