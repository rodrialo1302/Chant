package com.chant.android.model.entities

import java.io.Serializable
import java.time.LocalDateTime

data class Notification (
     val author : String,
     val id : Int,
     val date : LocalDateTime,
     val text : String,
     val type : String
): Serializable