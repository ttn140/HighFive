package com.example.highfive.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact( var userId: String,
                    var firstName: String,
                    var lastName: String,
                    var email: String,
                    var phone: String,
                    var website: String?,
                    var company: String?,
                    var jobTitle: String?,
                    var picture: String?,
                    var notes: String?,
                    var connections: ArrayList<String>?) : Parcelable {
    constructor() : this("", "","","","","","","","","", ArrayList())
}