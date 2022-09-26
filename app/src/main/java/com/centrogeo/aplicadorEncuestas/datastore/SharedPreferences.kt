package com.centrogeo.aplicadorEncuestas.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


class SharedPreferences(context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "DataStore")
    private val mDataStore: DataStore<Preferences> = context.dataStore

    companion object{
        val USER_NAME_KEY= stringPreferencesKey("USER_NAME")
        val USER_EMAIL_KEY= stringPreferencesKey("USER_EMAIL")
    }

    suspend fun storeUserData(name:String,email:String){
        mDataStore.edit { preferences->
            preferences[USER_NAME_KEY]=name
            preferences[USER_EMAIL_KEY]=email
        }
    }

    val userNameFlow: Flow<String> = mDataStore.data
        .catch { Log.i("ErrorName flow", "name") }
        .map {
            it[USER_NAME_KEY]?:""
        }

    val userEmailFlow: Flow<String> = mDataStore.data
        .catch { Log.i("ErrorEmail","email")  }
        .map{
            it[USER_EMAIL_KEY]?:""
        }
}