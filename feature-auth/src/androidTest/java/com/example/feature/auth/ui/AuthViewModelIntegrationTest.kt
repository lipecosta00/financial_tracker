package com.example.feature.auth.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.security.EncryptedTokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthViewModelIntegrationTest {

    private lateinit var secureStore: EncryptedTokenStore

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        secureStore = EncryptedTokenStore(context)
        secureStore.clear()
    }

    @Test
    fun submitPin_shouldCreatePinAndAuthenticateWithSecureStore() = runBlocking {
        val viewModel = AuthViewModel(secureStore)

        viewModel.submitPin("1234")

        val event = withTimeout(3_000) { viewModel.events.first() }

        assertEquals(AuthEvent.PinCreated, event)
        assertTrue(viewModel.state.value.isAuthenticated)
        assertTrue(viewModel.state.value.isPinCreated)
        assertNotNull(secureStore.readPinHash())
        assertEquals("valid-session", secureStore.readToken())
    }
}
