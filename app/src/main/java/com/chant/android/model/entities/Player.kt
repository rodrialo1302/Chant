package com.chant.android.model.entities

import java.io.Serializable
data class Player (
    val id : Int,
    val teamId : Int,
    var name : String,
    var avg : Int
) : Serializable