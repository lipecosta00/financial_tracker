package com.example.core.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

interface SecureTokenStore {
    fun saveToken(token: String)
    fun readToken(): String?
    fun savePinHash(pinHash: String)
    fun readPinHash(): String?
    fun clear()
}

class EncryptedTokenStore(context: Context) : SecureTokenStore {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_financial_store",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    override fun readToken(): String? = prefs.getString(TOKEN_KEY, null)

    override fun savePinHash(pinHash: String) {
        prefs.edit().putString(PIN_HASH_KEY, pinHash).apply()
    }

    override fun readPinHash(): String? = prefs.getString(PIN_HASH_KEY, null)

    override fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val TOKEN_KEY = "auth_token"
        const val PIN_HASH_KEY = "pin_hash"
    }
}
