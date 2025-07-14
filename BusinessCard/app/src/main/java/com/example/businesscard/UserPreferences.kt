package com.example.businesscard

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val NAME_KEY = stringPreferencesKey("name")
        val ROLE_KEY = stringPreferencesKey("role")
        val YEARS_KEY = intPreferencesKey("years")
        val PHONE_KEY = stringPreferencesKey("phone")
        val EMAIL_KEY = stringPreferencesKey("email")
    }

    val name: Flow<String> = context.dataStore.data.map { it[NAME_KEY] ?: "Jose" }
    val role: Flow<String> = context.dataStore.data.map { it[ROLE_KEY] ?: "Data Scientist" }
    val yearsExperience: Flow<Int> = context.dataStore.data.map { it[YEARS_KEY] ?: 2 }
    val phone: Flow<String> = context.dataStore.data.map { it[PHONE_KEY] ?: "6450-2300" }
    val email: Flow<String> = context.dataStore.data.map { it[EMAIL_KEY] ?: "jose.hernandez38@utp.ac.pa" }

    suspend fun saveData(name: String, role: String, years: Int, phone: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[NAME_KEY] = name
            prefs[ROLE_KEY] = role
            prefs[YEARS_KEY] = years
            prefs[PHONE_KEY] = phone
            prefs[EMAIL_KEY] = email
        }
    }
}


