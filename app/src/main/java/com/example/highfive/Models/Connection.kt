package com.example.highfive.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Connection(
    var userId: String,
    var name: String,
    var picture: String,
    var company: String,
    var phone: String,
    var email: String,
    var note: String
) : Parcelable {
    constructor() : this("","", "", "", "", "", "")
}