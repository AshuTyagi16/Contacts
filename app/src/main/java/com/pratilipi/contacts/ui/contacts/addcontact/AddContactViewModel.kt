package com.pratilipi.contacts.ui.contacts.addcontact

import android.graphics.Bitmap
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pratilipi.contacts.data.model.LoadingState
import com.pratilipi.contacts.util.ContactUtil
import kotlinx.coroutines.*
import javax.inject.Inject

class AddContactViewModel @Inject constructor(private val contactUtil: ContactUtil) : ViewModel() {

    private val _addContactLiveData = MutableLiveData<Boolean>()
    val addContactLiveData: LiveData<Boolean>
        get() = _addContactLiveData

    private val _loadingStateLiveData = MutableLiveData<LoadingState>()
    val loadingStateLiveData: LiveData<LoadingState>
        get() = _loadingStateLiveData

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    @UiThread
    fun addContact(displayName: String?, phoneNumber: String?, email: String?, image: Bitmap?) {
        _loadingStateLiveData.postValue(LoadingState(LoadingState.Status.LOADING))
        uiScope.launch {
            val isAdded = withContext(Dispatchers.IO) {
                contactUtil.addContact(displayName, phoneNumber, email, image)
            }
            _addContactLiveData.postValue(isAdded)
            _loadingStateLiveData.postValue(LoadingState(LoadingState.Status.SUCCESS))
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}