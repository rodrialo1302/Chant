package com.chant.android.model.entities

import java.io.Serializable

data class User(val username: String,
    var fullname : String,
    var mail : String,
    var password : String,
    var coins : Int,
    var team : Team,
    var bio : String,
    var pfp : Int
    ) : Serializable