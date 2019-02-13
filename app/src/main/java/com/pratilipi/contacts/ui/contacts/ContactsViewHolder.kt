package com.pratilipi.contacts.ui.contacts

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import kotlinx.android.synthetic.main.cell_contact.view.*
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.github.tamir7.contacts.Contact
import kotlinx.coroutines.*


class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var onItemClickListener: OnItemClickListener
    private val generator = ColorGenerator.MATERIAL
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun setContact(contact: Contact) {

        itemView.tvName.text = contact.displayName
        uiScope.launch {
            val drawable = withContext(Dispatchers.IO) {
                getDrawable(contact.id.toString(), contact.displayName)
            }
            itemView.ivName.setImageDrawable(drawable)
        }
        itemView.setOnClickListener {
            if (::onItemClickListener.isInitialized)
                onItemClickListener.onItemClicked(adapterPosition, contact)
        }

    }

    suspend fun getDrawable(id: String, name: String): TextDrawable {
        val substr = if (name.length >= 3)
            name.substring(0, 2)
        else
            name
        return TextDrawable.builder()
            .beginConfig()
            .textColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            .fontSize(40)
            .toUpperCase()
            .bold()
            .endConfig()
            .buildRound(substr, generator.getColor(name + id))
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, contact: Contact)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


}