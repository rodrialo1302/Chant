package com.chant.android.model.entities

import java.io.Serial
import java.io.Serializable
import java.time.LocalDateTime

data class Like (
    val author : String,
    val postId : Int,
    val date : LocalDateTime) : Serializable