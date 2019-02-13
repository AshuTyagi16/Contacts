package com.pratilipi.contacts.ui.contacts

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratilipi.contacts.Contacts
import com.pratilipi.contacts.R
import com.pratilipi.contacts.data.LoadingState
import com.pratilipi.contacts.di.component.DaggerMainActivityComponent
import com.pratilipi.contacts.di.module.MainActivityModule
import com.pratilipi.contacts.ui.base.ItemDecorator
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.*
import javax.inject.Inject

@RuntimePermissions
class MainActivity : AppCompatActivity(),
    ContactsAdapter.OnItemClickListener {

    @Inject
    lateinit var adapter: ContactsAdapter

    @Inject
    lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var itemDecorator: ItemDecorator

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactsViewModel: ContactsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val component = DaggerMainActivityComponent.builder()
            .contactsApplicationComponent(Contacts.get(this).contactsApplicationComponent())
            .mainActivityModule(MainActivityModule(this))
            .build()

        component.injectMainActivity(this)

        contactsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ContactsViewModel::class.java)

        readContactsWithPermissionCheck()

        initializeRecyclerView()

        observeContacts()

        observeLoadingState()
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun readContacts() {
        contactsViewModel.getContacts()
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    fun showContactRationale(request: PermissionRequest) {
        request.proceed()

    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onReadContactsPermissionDenied() {

    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onReadContactsPermissionDeniedForever() {

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
    }

    private fun observeLoadingState() {
        contactsViewModel.loadingStateLiveData.observe(this, Observer { loadingState ->
            when (loadingState.status) {
                LoadingState.Status.LOADING -> {
                    rvContacts.visibility = View.GONE
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

    override fun onItemClicked(position: Int) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}