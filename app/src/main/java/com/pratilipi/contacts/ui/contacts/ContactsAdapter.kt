package com.pratilipi.contacts.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.github.tamir7.contacts.Contact
import com.pratilipi.contacts.R

class ContactsAdapter(comparator: DiffUtil.ItemCallback<Contact>) :
    ListAdapter<Contact, ContactsViewHolder>(comparator),
    ContactsViewHolder.OnItemClickListener {

    private lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.cell_contact, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        getItem(position)?.let { contact ->
            holder.setContact(contact)
            holder.setOnItemClickListener(this)
        }
    }

    override fun onItemClicked(position: Int, contact: Contact) {
        if (::onItemClickListener.isInitialized)
            onItemClickListener.onItemClicked(position, contact)
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, contact: Contact)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}