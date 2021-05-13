package com.jeibniz.lbsapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.jeibniz.lbsapp.database.CreditCardEntity
import com.jeibniz.lbsapp.database.LbsRoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cardNumberET: EditText = findViewById(R.id.creditCardNumber_edit_text)
        val dateET: EditText = findViewById(R.id.date_edit_text)
        val cvvET: EditText = findViewById(R.id.cvv_edit_text)

        loadDataFromDatabase()

        val usernameTV: TextView = findViewById(R.id.userNameText)
        val sharedPref = baseContext.getSharedPreferences("LBS_SP" ,Context.MODE_PRIVATE)
        val userName = sharedPref.getString("username", "")
        usernameTV.setText(String.format("Logged in as: %s", userName))

//         Secure screen solution start
        window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE)
//         Secure screen solution end

        // Credit card number formatting
        cardNumberET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputLength = cardNumberET.text.toString().length

                // Add a space every 4 characters only if user is inputting
                if (start < inputLength && (inputLength == 4 ||
                    inputLength == 9 || inputLength == 14)){

                    cardNumberET.setText(cardNumberET.text.toString() + " ")

                    val pos = cardNumberET.text.length
                    cardNumberET.setSelection(pos)
                }
            }
        })

        dateET.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputLength = dateET.text.toString().length

                if (start < inputLength && inputLength == 2 ){

                    dateET.setText(dateET.text.toString() + "/")

                    val pos = dateET.text.length
                    dateET.setSelection(pos)

                }
            }
        })

        // Saving the data
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            // Fetching the text
            val cardNumber = cardNumberET.text.toString()
            val date = dateET.text.toString()
            val cvv = Integer.parseInt(cvvET.text.toString())

            // To the shared preferences
            val sharedPref = baseContext.getSharedPreferences("LBS_SP" ,Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString("cardNumber", cardNumber)
                putString("date", date)
                putInt("cvv", cvv)
                apply()
            }

            // To the database
            val dao = Room.databaseBuilder(
                baseContext,
                LbsRoomDatabase::class.java,
                LbsRoomDatabase.DATABASE_NAME
            ).build().getCreditCardDao()

            val card = CreditCardEntity(1, cardNumber, date, cvv)
            GlobalScope.launch {
                dao.insert(card)
            }

           Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show()
        }

        // Delete data from database on logout
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            deleteSensitiveData()
        }

        // Simulating a crash/attack
        findViewById<TextView>(R.id.explainationText).setOnClickListener {
            throw RuntimeException("Intended crash")
        }

        // Extreme solution, wiping data on crash

//        Thread.setDefaultUncaughtExceptionHandler { _, _ ->
//            val dao = Room.databaseBuilder(
//                baseContext,
//                LbsRoomDatabase::class.java,
//                LbsRoomDatabase.DATABASE_NAME
//            ).build().getCreditCardDao()
//
//            GlobalScope.launch {
//                dao.deleteAll()
//            }
//        }
    }

    // Auto filling data on login
    private fun loadDataFromDatabase() {
        val cardNumberET: EditText = findViewById(R.id.creditCardNumber_edit_text)
        val dateET: EditText = findViewById(R.id.date_edit_text)
        val cvvET: EditText = findViewById(R.id.cvv_edit_text)

        val dao = Room.databaseBuilder(
            baseContext,
            LbsRoomDatabase::class.java,
            LbsRoomDatabase.DATABASE_NAME
        ).build().getCreditCardDao()

        val cardFlow = dao.getById()
        GlobalScope.launch {
            cardFlow.collect {
                if (it.isEmpty()) {
                    Log.d("TEST", "loadDataFromDatabase: Empty list")
                    return@collect
                }
                val card = it[0]
                cardNumberET.setText(card.number)
                dateET.setText(card.date)
                cvvET.setText("" + card.cvv)
            }

            val numberOfCards= cardFlow.count { true }
            Log.d("TEST", "loadDataFromDatabase: $numberOfCards")
        }
    }


    // Function to delete the sensitive data from the database
    private fun deleteSensitiveData(){

        // Deleting shared preferences
        val sharedPref = baseContext.getSharedPreferences("LBS_SP" ,Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            remove("cardNumber")
            remove("date")
            remove("cvv")

            commit()
        }

        // Deleting from the database
        val dao = Room.databaseBuilder(
            baseContext,
            LbsRoomDatabase::class.java,
            LbsRoomDatabase.DATABASE_NAME
        ).build().getCreditCardDao()

        val intent = Intent(this, LoginActivity::class.java)

        // Deleting logged in state
        with (sharedPref.edit()) {
            remove("logged_in")
            commit()
        }

        GlobalScope.launch {
            Log.d("TEST", "onCreate: Start")
            delay(2000)
            dao.deleteAll()

            startActivity(intent)
            finish()
            Log.d("TEST", "onCreate: DONE")
        }
    }
}