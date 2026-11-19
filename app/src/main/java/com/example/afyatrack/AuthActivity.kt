
package com.example.afyatrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
import android.widget.*

class AuthActivity : Activity() {

    private lateinit var database: DatabaseHelper

    private lateinit var mainLayout: LinearLayout

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText

    private var isRegisterMode = false

    private val green = Color.rgb(25, 120, 75)
    private val darkGreen = Color.rgb(20, 65, 45)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseHelper(this)

        showLoginScreen()
    }

    // CREATE AUTHENTICATION SCREEN

    private fun createScreen() {

        val scrollView = ScrollView(this)

        mainLayout = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL

            gravity = Gravity.CENTER_HORIZONTAL

            setPadding(35, 45, 35, 35)

            setBackgroundColor(
                Color.rgb(244, 249, 246)
            )
        }

        scrollView.addView(mainLayout)

        setContentView(scrollView)
    }

    // ADD HEADING

    private fun addHeading() {

        val heading = TextView(this).apply {

            text = "AFYA TRACK"

            textSize = 32f

            typeface = Typeface.DEFAULT_BOLD

            gravity = Gravity.CENTER

            setTextColor(green)
        }

        mainLayout.addView(heading)

        val subtitle = TextView(this).apply {

            text = "HEALTH AND WELLNESS FOR ALL"

            textSize = 13f

            gravity = Gravity.CENTER

            setTextColor(darkGreen)
        }

        mainLayout.addView(subtitle)

        addSpace(35)
    }

    // CREATE INPUT FIELD

    private fun createInput(
        hint: String,
        type: Int
    ): EditText {

        val input = EditText(this).apply {

            this.hint = hint

            inputType = type

            textSize = 15f

            setSingleLine(true)

            setPadding(20, 15, 20, 15)

            background = GradientDrawable().apply {

                setColor(Color.WHITE)

                cornerRadius = 18f

                setStroke(
                    2,
                    Color.rgb(220, 230, 223)
                )
            }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = 15

        mainLayout.addView(input, params)

        return input
    }

    // CREATE BUTTON

    private fun createButton(
        text: String
    ): Button {

        return Button(this).apply {

            this.text = text

            textSize = 14f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(Color.WHITE)

            backgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    green
                )
        }
    }

    private fun addSpace(height: Int) {

        val space = android.view.View(this)

        mainLayout.addView(
            space,
            LinearLayout.LayoutParams(1, height)
        )
    }

    // LOGIN SCREEN

    private fun showLoginScreen() {

        isRegisterMode = false

        createScreen()

        addHeading()

        val title = TextView(this).apply {

            text = "Welcome Back"

            textSize = 23f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(darkGreen)
        }

        mainLayout.addView(title)

        addSpace(10)

        val description = TextView(this).apply {

            text = "Login to access your wellness dashboard."

            textSize = 14f

            setTextColor(Color.DKGRAY)
        }

        mainLayout.addView(description)

        addSpace(25)

        emailInput = createInput(
            "Enter your email address",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )

        passwordInput = createInput(
            "Enter your password",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        val loginButton = createButton("LOGIN")

        mainLayout.addView(loginButton)

        addSpace(15)

        val registerLink = TextView(this).apply {

            text = "Don't have an account? Register here"

            textSize = 15f

            gravity = Gravity.CENTER

            setTextColor(green)
        }

        mainLayout.addView(registerLink)

        loginButton.setOnClickListener {

            loginUser()
        }

        registerLink.setOnClickListener {

            showRegisterScreen()
        }
    }

    // REGISTRATION SCREEN

    private fun showRegisterScreen() {

        isRegisterMode = true

        createScreen()

        addHeading()

        val title = TextView(this).apply {

            text = "Create Account"

            textSize = 23f

            typeface = Typeface.DEFAULT_BOLD

            setTextColor(darkGreen)
        }

        mainLayout.addView(title)

        addSpace(10)

        val description = TextView(this).apply {

            text = "Register to start tracking your wellness."

            textSize = 14f

            setTextColor(Color.DKGRAY)
        }

        mainLayout.addView(description)

        addSpace(25)

        nameInput = createInput(
            "Enter your full name",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS
        )

        emailInput = createInput(
            "Enter your email address",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )

        passwordInput = createInput(
            "Create password (at least 8 characters)",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        confirmPasswordInput = createInput(
            "Confirm your password",
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD
        )

        val registerButton = createButton(
            "CREATE ACCOUNT"
        )

        mainLayout.addView(registerButton)

        addSpace(15)

        val loginLink = TextView(this).apply {

            text = "Already have an account? Login here"

            textSize = 15f

            gravity = Gravity.CENTER

            setTextColor(green)
        }

        mainLayout.addView(loginLink)

        registerButton.setOnClickListener {

            registerUser()
        }

        loginLink.setOnClickListener {

            showLoginScreen()
        }
    }

    // REGISTER USER

    private fun registerUser() {

        val name = nameInput.text.toString().trim()

        val email = emailInput.text.toString().trim()

        val password = passwordInput.text.toString()

        val confirmPassword =
            confirmPasswordInput.text.toString()

        if (name.length < 2) {

            nameInput.error = "Enter your full name"

            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailInput.error = "Enter a valid email address"

            return
        }

        if (password.length < 8) {

            passwordInput.error =
                "Password must contain at least 8 characters"

            return
        }

        if (password != confirmPassword) {

            confirmPasswordInput.error =
                "Passwords do not match"

            return
        }

        val registered = database.registerUser(
            name,
            email,
            password
        )

        if (registered) {

            Toast.makeText(
                this,
                "Registration successful! Please login.",
                Toast.LENGTH_LONG
            ).show()

            showLoginScreen()

            emailInput.setText(email)

        } else {

            Toast.makeText(
                this,
                "Registration failed. This email may already be registered.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // LOGIN USER

    private fun loginUser() {

        val email = emailInput.text.toString().trim()

        val password = passwordInput.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            emailInput.error = "Enter a valid email address"

            return
        }

        if (password.isEmpty()) {

            passwordInput.error = "Enter your password"

            return
        }

        val userName = database.loginUser(
            email,
            password
        )

        if (userName != null) {

            Toast.makeText(
                this,
                "Welcome $userName!",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            intent.putExtra(
                "user_name",
                userName
            )

            startActivity(intent)

            finish()

        } else {

            Toast.makeText(
                this,
                "Incorrect email or password.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}