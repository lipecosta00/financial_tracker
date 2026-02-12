package com.example.feature.auth.ui

import com.example.core.security.SecureTokenStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should create pin and authenticate when no pin exists`() = runTest(dispatcher) {
        val store = FakeSecureTokenStore()
        val viewModel = AuthViewModel(store)

        viewModel.submitPin("1234")
        advanceUntilIdle()

        assertThat(viewModel.state.value.isAuthenticated).isTrue()
        assertThat(store.readPinHash()).isNotNull()
        assertThat(store.readToken()).isEqualTo("valid-session")
    }
}

private class FakeSecureTokenStore : SecureTokenStore {
    private var token: String? = null
    private var pinHash: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun readToken(): String? = token

    override fun savePinHash(pinHash: String) {
        this.pinHash = pinHash
    }

    override fun readPinHash(): String? = pinHash

    override fun clear() {
        token = null
        pinHash = null
    }
}
