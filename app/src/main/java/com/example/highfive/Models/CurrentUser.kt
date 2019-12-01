package com.example.highfive.Models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CurrentUser() {
    companion object {
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
                sharedPreferences =
                    context.getSharedPreferences("CurrentUser", Context.MODE_PRIVATE)
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

        fun clearUser() {
            val editor = sharedPreferences!!.edit()
            editor.clear().apply()
        }

        fun writeArray(array: ArrayList<String>){
            val gson = Gson()
            val json = gson.toJson(array)
            val editor = sharedPreferences!!.edit()
            editor.putString(CONNECTIONS, json)
            editor.apply()
        }

        fun readArray(): ArrayList<String> {
            val gson = Gson()
            val json = sharedPreferences!!.getString(CONNECTIONS, null)
            val type = object : TypeToken<ArrayList<String>>() {

            }.type
            return gson.fromJson(json, type)
        }

        val user: Contact
            get() = Contact(
                read(USER_ID) ?: "",
                read(FIRST_NAME) ?: "",
                read(LAST_NAME) ?: "",
                read(EMAIL) ?: "",
                read(PHONE) ?: "",
                read(WEBSITE),
                read(COMPANY),
                read(JOB_TITLE),
                read(PICTURE),
                read(NOTES),
                readArray()
            )

        fun setUser(user: Contact){
            write(USER_ID, user.userId)
            write(FIRST_NAME, user.firstName)
            write(LAST_NAME, user.lastName)
            write(EMAIL, user.email)
            write(PHONE, user.phone)
            write(WEBSITE, user.website ?: "")
            write(COMPANY, user.company ?: "")
            write(JOB_TITLE, user.jobTitle ?: "")
            write(PICTURE, user.picture ?: "")
            write(NOTES, user.notes ?: "")
            writeArray(user.connections ?: ArrayList())

        }

    }
}