
package com.example.afyatrack

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

class MainActivity : Activity() {

    private lateinit var database: DatabaseHelper

    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var sleepInput: EditText
    private lateinit var waterInput: EditText
    private lateinit var stepsInput: EditText

    private lateinit var resultText: TextView
    private lateinit var historyLayout: LinearLayout

    private val green = Color.rgb(22, 120, 75)
    private val dark = Color.rgb(25, 45, 38)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseHelper(this)

        showDashboard()
    }

    private fun showDashboard() {

        val scrollView = ScrollView(this)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(35, 35, 35, 35)
            setBackgroundColor(Color.rgb(245, 250, 247))
        }

        scrollView.addView(mainLayout)

        val heading = TextView(this).apply {
            text = "AFYA TRACK"
            textSize = 30f
            setTextColor(green)
            gravity = Gravity.CENTER
        }

        mainLayout.addView(heading)

        val subtitle = TextView(this).apply {
            text = "Health and Wellness for All"
            textSize = 16f
            setTextColor(dark)
            gravity = Gravity.CENTER
        }

        mainLayout.addView(subtitle)

        addSpace(mainLayout, 20)

        val welcome = TextView(this).apply {
            text = "Welcome to your Wellness Dashboard"
            textSize = 20f
            setTextColor(dark)
        }

        mainLayout.addView(welcome)

        addSpace(mainLayout, 15)

        nameInput = createInput(
            mainLayout, "Enter your full name"

        )
        nameInput.setText(
            intent.getStringExtra("user_name") ?: ""
        )

        ageInput = createInput(
            mainLayout, "Enter your age",
            true
        )

        sleepInput = createInput(
            mainLayout, "Sleeping hours (e.g. 8)"
        )

        waterInput = createInput(
            mainLayout, "Glasses of water consumed",
            true
        )

        stepsInput = createInput(
            mainLayout, "Number of steps taken",
            true
        )

        addSpace(mainLayout, 15)

        val saveButton = Button(this).apply {
            text = "SAVE WELLNESS RECORD"
            setBackgroundColor(green)
            setTextColor(Color.WHITE)
        }

        mainLayout.addView(saveButton)

        resultText = TextView(this).apply {
            textSize = 17f
            setTextColor(dark)
            setPadding(10, 25, 10, 25)
        }

        mainLayout.addView(resultText)

        val historyButton = Button(this).apply {
            text = "VIEW ACTIVITY HISTORY"
        }

        mainLayout.addView(historyButton)

        val adviceButton = Button(this).apply {
            text = "WELLNESS TIPS"
        }

        mainLayout.addView(adviceButton)

        historyLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        mainLayout.addView(historyLayout)

        saveButton.setOnClickListener {
            saveWellnessRecord()
        }

        historyButton.setOnClickListener {
            showHistory()
        }

        adviceButton.setOnClickListener {
            showWellnessTips()
        }

        setContentView(scrollView)
    }

    private fun createInput(
        layout: LinearLayout,
        hintText: String,
        numbersOnly: Boolean = false
    ): EditText {

        val input = EditText(this)

        input.hint = hintText
        input.textSize = 16f

        if (numbersOnly) {
            input.inputType =
                android.text.InputType.TYPE_CLASS_NUMBER
        } else {
            input.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }

        layout.addView(input)

        addSpace(layout, 8)

        return input
    }

    private fun addSpace(
        layout: LinearLayout,
        height: Int
    ) {
        val space = View(this)

        layout.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                height
            )
        )
    }

    private fun saveWellnessRecord() {

        val name = nameInput.text.toString().trim()

        if (name.isEmpty()) {
            nameInput.error = "Enter your name"
            return
        }

        val age = ageInput.text.toString().toIntOrNull()
        val sleep = sleepInput.text.toString().toDoubleOrNull()
        val water = waterInput.text.toString().toIntOrNull()
        val steps = stepsInput.text.toString().toIntOrNull()

        if (age == null || age !in 1..120) {
            ageInput.error = "Enter a valid age"
            return
        }

        if (sleep == null || sleep !in 0.0..24.0) {
            sleepInput.error = "Enter sleep hours between 0 and 24"
            return
        }

        if (water == null || water !in 0..30) {
            waterInput.error = "Enter water glasses between 0 and 30"
            return
        }

        if (steps == null || steps !in 0..100000) {
            stepsInput.error = "Enter a valid step count"
            return
        }

        val score = calculateScore(
            sleep, water, steps
        )

        val date = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        val saved = database.saveRecord(
            name,
            age,
            sleep,
            water,
            steps,
            score,
            date
        )

        if (saved) {

            val category = when {
                score >= 80 -> "Good progress"
                score >= 60 -> "Moderate progress"
                else -> "Room for improvement"
            }

            resultText.text = """
                WELLNESS RESULTS

                Name: $name

                Sleep: $sleep hours
                Water: $water glasses
                Steps: $steps

                Wellness Score: $score/100

                Category: $category

                ${getRecommendation(sleep, water, steps)}
            """.trimIndent()

            Toast.makeText(
                this,
                "Wellness record saved successfully!",
                Toast.LENGTH_LONG
            ).show()

        } else {

            Toast.makeText(
                this,
                "Could not save record",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun calculateScore(
        sleep: Double,
        water: Int,
        steps: Int
    ): Int {

        val sleepScore =
            min(sleep / 8.0, 1.0) * 35

        val waterScore =
            min(water / 8.0, 1.0) * 30

        val stepsScore =
            min(steps / 8000.0, 1.0) * 35

        return (sleepScore + waterScore + stepsScore)
            .toInt()
    }

    private fun getRecommendation(
        sleep: Double,
        water: Int,
        steps: Int
    ): String {

        val tips = mutableListOf<String>()

        if (sleep < 8) {
            tips.add("Try to maintain a regular sleep routine.")
        }

        if (water < 8) {
            tips.add("Remember to drink water regularly.")
        }

        if (steps < 8000) {
            tips.add("Consider adding comfortable daily movement.")
        }

        if (tips.isEmpty()) {
            tips.add("Keep maintaining your healthy daily habits!")
        }

        return "WELLNESS RECOMMENDATIONS\n\n" +
                tips.joinToString("\n")
    }

    private fun showHistory() {

        historyLayout.removeAllViews()

        val heading = TextView(this).apply {
            text = "\nYOUR WELLNESS HISTORY\n"
            textSize = 21f
            setTextColor(green)
        }

        historyLayout.addView(heading)

        val records = database.getRecords()

        if (records.isEmpty()) {

            val empty = TextView(this).apply {
                text = "No records available yet."
                textSize = 16f
            }

            historyLayout.addView(empty)

        } else {

            for (record in records) {

                val recordView = TextView(this).apply {
                    text = record
                    textSize = 15f
                    setTextColor(dark)
                    setPadding(15, 15, 15, 15)
                    setBackgroundColor(Color.WHITE)
                }

                historyLayout.addView(recordView)

                addSpace(historyLayout, 10)
            }
        }
    }

    private fun showWellnessTips() {

        val tips = """
            HEALTH AND WELLNESS TIPS

            1. Maintain a regular sleeping routine.

            2. Drink enough water throughout the day.

            3. Engage in physical activities suitable
               for your ability.

            4. Eat a balanced and varied diet.

            5. Take breaks and manage stress.

            6. Maintain good personal hygiene.

            7. Seek professional healthcare advice
               when needed.

            Small healthy habits can improve
            general wellbeing.
        """.trimIndent()

        android.app.AlertDialog.Builder(this)
            .setTitle("Afya Track Wellness Tips")
            .setMessage(tips)
            .setPositiveButton("CLOSE", null)
            .show()
    }
}
