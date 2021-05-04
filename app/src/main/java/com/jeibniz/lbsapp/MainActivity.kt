package com.jeibniz.lbsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        loadDataFromDatabase()

        findViewById<Button>(R.id.saveButton).setOnClickListener {

            val dao = Room.databaseBuilder(
                baseContext,
                LbsRoomDatabase::class.java,
                LbsRoomDatabase.DATABASE_NAME
            ).build().getCreditCardDao()

            val cardNumber = findViewById<EditText>(R.id.creditCardNumber).text.toString()
            val date = findViewById<EditText>(R.id.date).text.toString()
            val cvv = Integer.parseInt(findViewById<EditText>(R.id.cvv).text.toString())

            val card = CreditCardEntity(1, cardNumber, date, cvv)
            GlobalScope.launch {
                dao.insert(card)
            }

           Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show()
        }

        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            val dao = Room.databaseBuilder(
                baseContext,
                LbsRoomDatabase::class.java,
                LbsRoomDatabase.DATABASE_NAME
            ).build().getCreditCardDao()

            val intent = Intent(this, LoginActivity::class.java)
            GlobalScope.launch {
                Log.d("TEST", "onCreate: Start")
                delay(2000)
                dao.deleteAll()

                startActivity(intent)
                finish()
                Log.d("TEST", "onCreate: DONE")
            }
        }

        findViewById<TextView>(R.id.explainationText).setOnClickListener {
            throw RuntimeException("Intended crash")
        }
    }

    private fun loadDataFromDatabase() {
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
                val card = it.get(0)
                findViewById<EditText>(R.id.creditCardNumber).setText(card.number)
                findViewById<EditText>(R.id.date).setText(card.date)
                findViewById<EditText>(R.id.cvv).setText("" + card.cvv)
            }

            val numberOfCards= cardFlow.count { true }
            Log.d("TEST", "loadDataFromDatabase: " + numberOfCards)
        }


    }
}