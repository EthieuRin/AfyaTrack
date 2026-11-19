
package com.example.afyatrack.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.afyatrack.R

class RegisterFragment : Fragment() {

    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authRepository = AuthRepository(requireContext())

        val nameInput = view.findViewById<EditText>(R.id.nameInput)
        val emailInput = view.findViewById<EditText>(R.id.emailInput)
        val passwordInput = view.findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput =
            view.findViewById<EditText>(R.id.confirmPasswordInput)

        val registerButton = view.findViewById<Button>(R.id.registerButton)
        val loginText = view.findViewById<TextView>(R.id.loginText)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            when {
                name.isEmpty() -> {
                    nameInput.error = "Enter your name"
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    emailInput.error = "Enter a valid email"
                }

                password.length < 6 -> {
                    passwordInput.error = "Password must be at least 6 characters"
                }

                password != confirmPassword -> {
                    confirmPasswordInput.error = "Passwords do not match"
                }

                else -> {
                    val registered = authRepository.register(
                        name,
                        email,
                        password
                    )

                    if (registered) {
                        Toast.makeText(
                            requireContext(),
                            "Account created successfully. Please log in.",
                            Toast.LENGTH_LONG
                        ).show()

                        findNavController().navigate(
                            R.id.action_registerFragment_to_loginFragment
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Email already registered or registration failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        loginText.setOnClickListener {
            findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }
    }
}
