package com.example.firebasephonenumberauthentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasephonenumberauthentication.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var homeActivityBinding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeActivityBinding.root)
    }
}