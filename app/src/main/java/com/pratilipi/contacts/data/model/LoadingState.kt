package com.pratilipi.contacts.data.model

class LoadingState(val status: Status, val msg: String = "Some error occurred") {

    enum class Status {
        LOADING,
        SUCCESS,
        FAILED
    }
}