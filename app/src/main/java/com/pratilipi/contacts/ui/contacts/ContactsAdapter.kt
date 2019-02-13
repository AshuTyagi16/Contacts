package com.pratilipi.contacts.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.pratilipi.contacts.R
import com.pratilipi.contacts.data.Contact

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

    override fun onItemClicked(position: Int) {
        if (::onItemClickListener.isInitialized)
            onItemClickListener.onItemClicked(position)
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}