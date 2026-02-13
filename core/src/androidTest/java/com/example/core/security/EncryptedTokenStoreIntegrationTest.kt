package com.example.core.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedTokenStoreIntegrationTest {

    private lateinit var tokenStore: EncryptedTokenStore

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        tokenStore = EncryptedTokenStore(context)
        tokenStore.clear()
    }

    @Test
    fun saveReadAndClear_shouldPersistSecureValues() {
        tokenStore.saveToken("session-123")
        tokenStore.savePinHash("hash-abc")

        assertEquals("session-123", tokenStore.readToken())
        assertEquals("hash-abc", tokenStore.readPinHash())

        tokenStore.clear()

        assertNull(tokenStore.readToken())
        assertNull(tokenStore.readPinHash())
    }
}
