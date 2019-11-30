package com.example.highfive.Models

import android.content.Context
import android.content.SharedPreferences

class CurrentUser(){
    companion object{
        val USER_ID = "userId"
        val FIRST_NAME = "firstName"
        val LAST_NAME = "lastName"
        val EMAIL = "email"
        val PHONE = "phone"
        val WEBSITE = "website"
        val COMPANY = "company"
        val JOB_TITLE = "jobTitle"
        val PICTURE = "picture"
        val NOTES = "notes"
        val CONNECTIONS = "connections"
        private var sharedPreferences: SharedPreferences? = null

        fun init(context: Context) {
            if (sharedPreferences == null) {
                sharedPreferences = context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE)
            }
        }

        fun read(key: String): String? {
            return sharedPreferences!!.getString(key, "")
        }

        fun write(key: String, value: String) {
            val editor = sharedPreferences!!.edit()
            editor.putString(key, value)
            editor.commit()
        }

        fun clearUser(){
            val editor = sharedPreferences!!.edit()
            editor.clear().apply()
        }
    }
}