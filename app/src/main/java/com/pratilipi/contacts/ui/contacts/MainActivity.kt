package com.pratilipi.contacts.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.tamir7.contacts.Contact
import com.pratilipi.contacts.Contacts
import com.pratilipi.contacts.R
import com.pratilipi.contacts.data.model.LoadingState
import com.pratilipi.contacts.data.model.event.ContactAddedEvent
import com.pratilipi.contacts.di.component.DaggerMainActivityComponent
import com.pratilipi.contacts.di.module.MainActivityModule
import com.pratilipi.contacts.ui.base.ItemDecorator
import com.pratilipi.contacts.ui.contacts.addcontact.AddContactFragment
import com.pratilipi.contacts.ui.contacts.contactdetail.ContactDetailFragment
import com.pratilipi.contacts.ui.widget.PermissionModal
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.*
import javax.inject.Inject

@RuntimePermissions
class MainActivity : AppCompatActivity(),
    ContactsAdapter.OnItemClickListener, PermissionModal.OnAskPermissionClickListener {

    @Inject
    lateinit var adapter: ContactsAdapter

    @Inject
    lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var itemDecorator: ItemDecorator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var permissionModal: PermissionModal

    companion object {
        private const val PERMISSION_CODE = 9921
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val component = DaggerMainActivityComponent.builder()
            .contactsApplicationComponent(Contacts.get(this).contactsApplicationComponent())
            .mainActivityModule(MainActivityModule(this))
            .build()

        component.injectMainActivity(this)

        contactsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ContactsViewModel::class.java)
        permissionModal = PermissionModal(this)
        permissionModal.setOnAskPermissionClickListener(this)

        initializeRecyclerView()

        observeContacts()

        observeLoadingState()

        fabAddContact.setOnClickListener {
            val fragment = AddContactFragment.newInstance()
            fragment.show(supportFragmentManager, fragment.tag)
        }

        if (checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionModal.show()
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    fun readContacts() {
        permissionModal.dismiss()
        contactsViewModel.getContacts()
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    fun showContactRationale(request: PermissionRequest) {
        request.proceed()

    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    fun onReadContactsPermissionDenied() {
        Toast.makeText(this, getString(R.string.please_allow_permission), Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
    fun onReadContactsPermissionDeniedForever() {
        openSettings()
    }

    private fun initializeRecyclerView() {
        rvContacts.layoutManager = layoutManager
        rvContacts.addItemDecoration(itemDecorator)
        adapter.setOnItemClickListener(this)
        rvContacts.adapter = adapter
    }

    private fun observeContacts() {
        contactsViewModel.contactsLiveData.observe(this, Observer { contacts ->
            adapter.submitList(contacts)
        })

        contactsViewModel.deleteContactLiveData.observe(this, Observer {
            if (it)
                contactsViewModel.getContacts()
        })
    }

    private fun observeLoadingState() {
        contactsViewModel.loadingStateLiveData.observe(this, Observer { loadingState ->
            when (loadingState.status) {
                LoadingState.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                LoadingState.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    rvContacts.visibility = View.VISIBLE

                }
                LoadingState.Status.FAILED -> {
                    progressBar.visibility = View.GONE
                    rvContacts.visibility = View.GONE
                }
            }
        })
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onResume() {
        super.onResume()
        if (checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (::contactsViewModel.isInitialized)
                contactsViewModel.getContacts()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onItemClicked(position: Int, contact: Contact) {
        val fragment = ContactDetailFragment.newInstance(contact)
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onDeleteItemClicked(position: Int, contact: Contact) {
        contactsViewModel.deleteContact(contact.displayName)
    }

    override fun onAskPermissionClick() {
        readContactsWithPermissionCheck()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onContactAddedEvent(event: ContactAddedEvent) {
        if (::contactsViewModel.isInitialized) {
            contactsViewModel.getContacts()
            EventBus.getDefault().removeStickyEvent(event)
        }
    }
}
