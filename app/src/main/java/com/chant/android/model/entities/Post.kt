package com.chant.android.model.entities

import java.io.Serializable
import java.time.LocalDateTime

data class Post (
    val id : Int,
    val matchId : Int,
    var parentId : Int,
    val author : String,
    var type : Int,
    val date : LocalDateTime,
    var text : String,
    var likes : ArrayList<Like>,
    var pfp : Int
    ) : Serializable

