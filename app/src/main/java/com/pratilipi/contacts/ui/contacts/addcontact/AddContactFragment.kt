package com.pratilipi.contacts.ui.contacts.addcontact

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.pratilipi.contacts.ui.widget.ProgressModal
import kotlinx.android.synthetic.main.fragment_add_contact.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import gun0912.tedbottompicker.TedBottomPicker
import permissions.dispatcher.*


@RuntimePermissions
class AddContactFragment : RoundedBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var addContactViewModel: AddContactViewModel

    private lateinit var progressModal: ProgressModal

    companion object {
        private const val PERMISSION_CODE = 9923
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
            progressModal = ProgressModal(it, getString(R.string.default_loading_message))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPerson.setImageResource(R.drawable.avatar_placeholder)

        setClickListeners()

        observeLiveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::progressModal.isInitialized && progressModal.isShowing)
            progressModal.dismiss()
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
                    progressModal.show()
                }
                LoadingState.Status.SUCCESS -> {
                    context?.let {
                        Toast.makeText(context, getString(R.string.contact_added_successfully), Toast.LENGTH_SHORT)
                            .show()
                    }
                    progressModal.dismiss()
                    dismiss()

                }
                LoadingState.Status.FAILED -> {
                    progressModal.show()
                }
            }
        })
    }

    private fun setClickListeners() {

        ivPerson.setOnClickListener {
            showPickerWithPermissionCheck()
        }

        btnSubmit.setOnClickListener {
            val name = etName.text.trim().toString()
            val number = etNumber.text.trim().toString()
            val email = etEmail.text.trim().toString()
            if (name.isNotBlank() && name.isNotEmpty()) {
                var image: Bitmap? = null
                ivPerson.drawable?.let {
                    image = (it as BitmapDrawable).bitmap
                }
                addContactViewModel.addContact(name, number, email, image)
            } else {
                context?.let {
                    val error = getString(R.string.name_cannot_be_blank)
                    etName.error = error
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showPicker() {
        context?.let {
            val picker = TedBottomPicker.Builder(it)
                .setSelectMaxCount(1)
                .setPeekHeight(1000)
                .showTitle(true)
                .showCameraTile(false)
                .showGalleryTile(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Image Selected")
                .setOnImageSelectedListener { uri ->
                    ivPerson.setImageURI(uri)
                }
                .create()
            picker.show(childFragmentManager, picker.tag)
        }
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun showStorageRationale(request: PermissionRequest) {
        request.proceed()
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        context?.let {
            Toast.makeText(it, getString(R.string.please_allow_permission), Toast.LENGTH_SHORT).show()
        }
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onStorageForeverDenied() {
        openSettings()
    }

    private fun openSettings() {
        context?.let {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", it.packageName, null)
            intent.data = uri
            startActivityForResult(intent, PERMISSION_CODE)
        }
    }
}