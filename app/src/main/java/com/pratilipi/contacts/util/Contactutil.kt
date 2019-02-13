package com.pratilipi.contacts.util

import android.content.Context
import android.provider.ContactsContract
import com.pratilipi.contacts.data.Contact

class Contactutil(private val context: Context) {

    private lateinit var list: MutableList<Contact>
    private val numberList = ArrayList<String>()
    private lateinit var contact: Contact

    suspend fun getContacts(): List<Contact>? {
        val contacts = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        contacts?.let {
            list = ArrayList(contacts.count)
            while (contacts.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (!numberList.contains(phoneNumber)) {
                    contact = Contact(id, name, phoneNumber)
                    list.add(contact)
                    numberList.add(phoneNumber)
                }
            }
            it.close()
            return list
        } ?: run {
            contacts?.close()
            return null
        }
    }
}