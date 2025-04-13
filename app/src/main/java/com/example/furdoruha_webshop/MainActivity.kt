package com.example.furdoruha_webshop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            startActivity(Intent(this, ProductListActivity::class.java))
        } else {

            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
