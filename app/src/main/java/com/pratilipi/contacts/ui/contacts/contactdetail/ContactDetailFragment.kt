package com.pratilipi.contacts.ui.contacts.contactdetail

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.github.tamir7.contacts.Contact
import com.google.gson.Gson
import com.pratilipi.contacts.R
import com.pratilipi.contacts.ui.base.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_contact_detail.*

class ContactDetailFragment : RoundedBottomSheetDialogFragment() {

    companion object {
        private const val EXTRA_CONTACT = "EXTRA_CONTACT"
        private val gson = Gson()

        fun newInstance(contact: Contact): ContactDetailFragment {
            val fragment = ContactDetailFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_CONTACT, gson.toJson(contact))
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_contact_detail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            val contact = gson.fromJson(bundle.getString(EXTRA_CONTACT), Contact::class.java)

            contact.photoUri?.let {
                ivPerson.setImageURI(Uri.parse(it))

            } ?: run {
                ivPerson.setImageResource(R.drawable.avatar_placeholder)
            }

            contact.displayName?.let {
                tvName.text = contact.displayName
                tvName.visibility = View.VISIBLE
            }

            if (contact.emails.isNotEmpty()) {
                var email = ""
                contact.emails.forEach {
                    email = email + it.address + "\n\n"
                }
                tvEmail.text = email
                (clEmail).visibility = View.VISIBLE
            }
            if (contact.phoneNumbers.isNotEmpty()) {
                var number = ""
                contact.phoneNumbers.forEach {
                    number = number + it.number + "\n\n"
                }
                tvNumber.text = number
                clNumber.visibility = View.VISIBLE
            }
        }
    }
}