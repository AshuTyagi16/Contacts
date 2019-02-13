package com.pratilipi.contacts.ui.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.tamir7.contacts.Contact
import com.pratilipi.contacts.data.LoadingState
import com.pratilipi.contacts.util.ContactUtil
import kotlinx.coroutines.*
import javax.inject.Inject

class ContactsViewModel @Inject constructor(private val contactUtil: ContactUtil) : ViewModel() {

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
                contactUtil.getContacts()
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