package com.chant.android.model.entities

import java.io.Serializable
import java.time.LocalDateTime

data class Match (
    val id : Int,
    var team1 : Team,
    var team2 : Team,
    var startDate : LocalDateTime,
    var goals1 : Int,
    var goals2 : Int,
    var status : Int,
    var quota1 : Double,
    var quota2 : Double,
    var quotaTie : Double) : Serializable