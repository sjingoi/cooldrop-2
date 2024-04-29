package com.example.cooldrop

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey

class SharedPrefKeyValueStore(context: Context) {
    private object PreferencesKeys {
        val EXAMPLE_FLAG: Preferences.Key<Boolean> = booleanPreferencesKey("exampleFlag")
    }
}