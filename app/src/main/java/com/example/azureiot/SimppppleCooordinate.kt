package com.example.azureiot

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleGpsCoordinate(
    val latitude: Number,
    val longitude: Number
) : Parcelable
