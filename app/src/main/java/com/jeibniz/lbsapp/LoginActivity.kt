package com.jeibniz.lbsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.Room
import com.jeibniz.lbsapp.database.LbsRoomDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
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