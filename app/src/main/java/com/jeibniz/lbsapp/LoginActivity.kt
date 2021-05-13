package com.jeibniz.lbsapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.content.edit
import androidx.room.Room
import com.jeibniz.lbsapp.database.LbsRoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameET: EditText = findViewById(R.id.username_edit_text)

        usernameET.setText("Test_User")
        findViewById<EditText>(R.id.password_edit_text).setText("Password")

        usernameET.setSelection(4)

        val sharedPref = baseContext.getSharedPreferences("LBS_SP" ,Context.MODE_PRIVATE)

        findViewById<Button>(R.id.login_button).setOnClickListener {
            sharedPref.edit {
                val username = findViewById<EditText>(R.id.username_edit_text).text.toString()
                putString("username", username)
                putBoolean("logged_in", true)
                apply()
            }
            login()
        }

        if (sharedPref.getBoolean("logged_in", false)) {
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login() {
        val dao = Room.databaseBuilder(
            baseContext,
            LbsRoomDatabase::class.java,
            LbsRoomDatabase.DATABASE_NAME
        ).build().getCreditCardDao()

        val deleteTask = GlobalScope.launch {
            dao.deleteAll()
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}