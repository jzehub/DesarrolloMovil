package com.example.businesscard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.businesscard.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BusinessCardViewModel(private val prefs: UserPreferences) : ViewModel() {
    val name = prefs.name.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val role = prefs.role.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val yearsExperience = prefs.yearsExperience.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    val phone = prefs.phone.stateIn(viewModelScope, SharingStarted.Lazily, "")
    val email = prefs.email.stateIn(viewModelScope, SharingStarted.Lazily, "")

    fun save(name: String, role: String, years: Int, phone: String, email: String) {
        viewModelScope.launch {
            prefs.saveData(name, role, years, phone, email)
        }
    }
}



class BusinessCardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = UserPreferences(context)
        return BusinessCardViewModel(prefs) as T
    }
}
