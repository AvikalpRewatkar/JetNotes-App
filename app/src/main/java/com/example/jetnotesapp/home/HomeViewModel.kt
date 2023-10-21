package com.example.jetnotesapp.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnotesapp.models.Notes
import com.example.jetnotesapp.repository.Resources
import com.example.jetnotesapp.repository.StorageRepository
import com.google.android.gms.common.internal.ResourceUtils
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StorageRepository = StorageRepository()
): ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())

    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadNotes() {
        if(hasUser) {
            if (userId.isNotBlank()) {
                getUserNotes(userId)
            } else {
                homeUiState = homeUiState.copy(
                    notesList = Resources.Error(
                        throwable = Throwable(message = "User is not login")
                    )
                )
            }
        }
    }

    private fun getUserNotes(userId: String) = viewModelScope.launch {
        repository.getUserNotes(userId).collect {
            homeUiState = homeUiState.copy(notesList = it)
        }
    }

    fun deleteNote(notedId: String) = repository.deleteNote(notedId) {
        homeUiState = homeUiState.copy(noteDeletedStatus = it)
    }

    fun signOut()  = repository.signOut()

}

data class HomeUiState(
    val notesList: Resources<List<Notes>> = Resources.Loading(),
    val noteDeletedStatus: Boolean = false

)