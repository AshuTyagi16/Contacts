package com.pratilipi.contacts.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pratilipi.contacts.data.Contact
import com.pratilipi.contacts.data.LoadingState
import com.pratilipi.contacts.util.Contactutil
import kotlinx.coroutines.*
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactutil: Contactutil) : ViewModel() {

    private val _contactsLiveData = MutableLiveData<List<Contact>>()
    val contactsLiveData: LiveData<List<Contact>>
        get() = _contactsLiveData

    private val _loadingStateLiveData = MutableLiveData<LoadingState>()
    val loadingStateLiveData: LiveData<LoadingState>
        get() = _loadingStateLiveData

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    fun getContacts() {
        _loadingStateLiveData.postValue(LoadingState(LoadingState.Status.LOADING))
        uiScope.launch {
            val contacts = withContext(Dispatchers.IO) {
                contactutil.getContacts()
            }
            _contactsLiveData.postValue(contacts)
            _loadingStateLiveData.postValue(LoadingState(LoadingState.Status.SUCCESS))
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}