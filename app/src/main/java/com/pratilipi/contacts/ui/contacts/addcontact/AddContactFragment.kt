package com.pratilipi.contacts.ui.contacts.addcontact

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pratilipi.contacts.Contacts
import com.pratilipi.contacts.R
import com.pratilipi.contacts.data.model.LoadingState
import com.pratilipi.contacts.data.model.event.ContactAddedEvent
import com.pratilipi.contacts.di.component.DaggerAddContactFragmentComponent
import com.pratilipi.contacts.ui.base.RoundedBottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_contact.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AddContactFragment : RoundedBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var addContactViewModel: AddContactViewModel

    companion object {
        fun newInstance(): AddContactFragment {
            return AddContactFragment()
        }
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_add_contact
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            val component = DaggerAddContactFragmentComponent.builder()
                .contactsApplicationComponent(Contacts.get(it).contactsApplicationComponent())
                .build()
            component.injectAddContactFragment(this)
            addContactViewModel = ViewModelProviders.of(this, viewModelFactory).get(AddContactViewModel::class.java)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPerson.setImageResource(R.drawable.avatar_placeholder)

        setClickListeners()

        observeLiveData()
    }

    private fun observeLiveData() {
        addContactViewModel.addContactLiveData.observe(this, Observer {
            if (it)
                EventBus.getDefault().postSticky(ContactAddedEvent())
            else {
                Toast.makeText(context, getString(R.string.some_error_occurred), Toast.LENGTH_SHORT)
                    .show()
            }
        })

        addContactViewModel.loadingStateLiveData.observe(this, Observer { loadingState ->
            when (loadingState.status) {
                LoadingState.Status.LOADING -> {

                }
                LoadingState.Status.SUCCESS -> {
                    context?.let {
                        Toast.makeText(context, getString(R.string.contact_added_successfully), Toast.LENGTH_SHORT)
                            .show()
                    }
                    dismiss()

                }
                LoadingState.Status.FAILED -> {

                }
            }
        })
    }

    private fun setClickListeners() {
        btnSubmit.setOnClickListener {
            val name = etName.text.trim().toString()
            val number = etNumber.text.trim().toString()
            val email = etEmail.text.trim().toString()
            if (name.isNotBlank() && name.isNotEmpty()) {
                addContactViewModel.addContact(name, number, email)
            } else {
                context?.let {
                    val error = getString(R.string.name_cannot_be_blank)
                    etName.error = error
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}