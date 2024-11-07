package com.chant.android.model.entities

import java.io.Serializable

data class Team (
    val id : Int,
    var name : String
    ) : Serializable {
    override fun toString(): String = name
    }
