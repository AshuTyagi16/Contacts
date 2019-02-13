package com.pratilipi.contacts.util

import android.content.Context
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts
import android.provider.ContactsContract
import android.content.ContentProviderOperation
import androidx.annotation.WorkerThread


class ContactUtil(private val context: Context) {

    @WorkerThread
    suspend fun getContacts(): List<Contact>? {
        return Contacts.getQuery().find()
    }

    @WorkerThread
    suspend fun addContact(displayName: String?, number: String?, emailID: String?): Boolean {
        val operations = ArrayList<ContentProviderOperation>()

        operations.add(
            ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI
            )
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        displayName?.let {
            operations.add(
                ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI
                )
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        it
                    ).build()
            )
        }

        number?.let {
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, it)
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }

        emailID?.let {
            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    )
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, it)
                    .withValue(
                        ContactsContract.CommonDataKinds.Email.TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK
                    )
                    .build()
            )
        }

        return try {
            val res = context.contentResolver.applyBatch(ContactsContract.AUTHORITY, operations)
            res.isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }


    }
}