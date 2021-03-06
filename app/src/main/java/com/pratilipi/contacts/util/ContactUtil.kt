package com.pratilipi.contacts.util

import android.annotation.SuppressLint
import android.content.Context
import com.github.tamir7.contacts.Contact
import com.github.tamir7.contacts.Contacts
import android.provider.ContactsContract
import android.content.ContentProviderOperation
import androidx.annotation.WorkerThread
import android.net.Uri
import android.provider.ContactsContract.PhoneLookup
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.IOException


class ContactUtil(private val context: Context) {

    @WorkerThread
    suspend fun getContacts(): List<Contact>? {
        return Contacts.getQuery().find()
    }

    @SuppressLint("WrongThread")
    @WorkerThread
    suspend fun addContact(displayName: String?, number: String?, emailID: String?, image: Bitmap?): Boolean {
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

        image?.let {
            val stream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.WEBP, 10, stream)

            operations.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                    .build()
            )

            try {
                stream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
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

    @WorkerThread
    suspend fun deleteContact(name: String): Boolean {
        val cur = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        try {
            cur?.let {
                if (it.moveToFirst()) {
                    do {
                        if (it.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)) == name) {
                            val lookupKey = it.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
                            context.contentResolver.delete(uri, null, null)
                            return true
                        }
                    } while (it.moveToNext())
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cur?.close()
        }
        return false
    }
}