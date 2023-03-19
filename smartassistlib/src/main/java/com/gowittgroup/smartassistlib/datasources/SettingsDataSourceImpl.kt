package com.gowittgroup.smartassistlib.datasources

import android.content.SharedPreferences
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.aiModel
import com.gowittgroup.smartassistlib.datasources.LocalPreferenceManager.readAloud
import com.gowittgroup.smartassistlib.models.Resource
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


class SettingsDataSourceImpl(private val pref: SharedPreferences): SettingsDataSource {
    private val mutex = Mutex()
    override suspend fun getSelectedAiModel(): Resource<String> = Resource.Success(pref.aiModel!!)
    override suspend fun getReadAloud(): Resource<Boolean> = Resource.Success(pref.readAloud)

    override suspend fun toggleReadAloud(isOn: Boolean) {
        mutex.withLock {
            pref.readAloud = isOn
        }
    }

    override suspend fun chooseAiModel(model: String) {
        mutex.withLock {
            pref.aiModel = model
        }
    }
}
