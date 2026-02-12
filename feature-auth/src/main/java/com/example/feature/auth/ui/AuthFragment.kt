package com.example.feature.auth.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.feature.auth.R
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthFragment : Fragment() {

    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val title = view.findViewById<TextView>(R.id.authTitle)
        val pinInput = view.findViewById<EditText>(R.id.pinInput)
        val pinButton = view.findViewById<Button>(R.id.pinSubmitButton)
        val biometricButton = view.findViewById<Button>(R.id.biometricButton)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                title.text = if (state.isPinCreated) {
                    getString(R.string.enter_pin)
                } else {
                    getString(R.string.create_pin)
                }

                if (state.isAuthenticated) {
                    setFragmentResult(RESULT_KEY, Bundle().apply { putBoolean(RESULT_AUTH_OK, true) })
                }

                state.errorMessage?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    AuthEvent.PinCreated -> Toast.makeText(requireContext(), R.string.pin_created, Toast.LENGTH_SHORT).show()
                    AuthEvent.Authenticated -> Toast.makeText(requireContext(), R.string.auth_success, Toast.LENGTH_SHORT).show()
                }
            }
        }

        pinButton.setOnClickListener {
            viewModel.submitPin(pinInput.text.toString())
        }

        biometricButton.setOnClickListener {
            showBiometricPromptIfAvailable()
        }
    }

    private fun showBiometricPromptIfAvailable() {
        val biometricManager = BiometricManager.from(requireContext())
        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(requireContext(), R.string.biometric_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(requireContext())
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                viewModel.onBiometricAuthenticated()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(requireContext(), errString, Toast.LENGTH_SHORT).show()
            }
        }

        val prompt = BiometricPrompt(this, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()

        prompt.authenticate(promptInfo)
    }

    companion object {
        const val RESULT_KEY = "auth_result_key"
        const val RESULT_AUTH_OK = "auth_ok"
    }
}
