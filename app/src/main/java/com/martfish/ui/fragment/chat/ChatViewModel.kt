package com.martfish.ui.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.martfish.database.FirestoreDatabase

class ChatViewModel(val firestoreDatabase: FirestoreDatabase) : ViewModel() {
    class Factory(private val firestoreDatabase: FirestoreDatabase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(firestoreDatabase) as T
        }
    }
}