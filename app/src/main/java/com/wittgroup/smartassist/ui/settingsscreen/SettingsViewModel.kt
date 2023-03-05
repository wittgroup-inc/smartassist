package com.wittgroup.smartassist.ui.settingsscreen

import androidx.lifecycle.*

class SettingsViewModel() : ViewModel() {
    companion object {
        fun provideFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel() as T
            }
        }
    }
}

