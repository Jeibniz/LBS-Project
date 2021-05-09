package com.jeibniz.lbsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.room.Room
import com.jeibniz.lbsapp.database.LbsRoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameET: EditText = findViewById(R.id.username_edit_text)

        usernameET.setText("User")
        findViewById<EditText>(R.id.password_edit_text).setText("Password")

        usernameET.setSelection(4)

        findViewById<Button>(R.id.login_button).setOnClickListener {
            val dao = Room.databaseBuilder(
                baseContext,
                LbsRoomDatabase::class.java,
                LbsRoomDatabase.DATABASE_NAME
            ).build().getCreditCardDao()

            GlobalScope.launch {
                dao.deleteAll()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}