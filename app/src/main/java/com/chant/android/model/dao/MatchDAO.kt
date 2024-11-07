package com.chant.android.model.dao

import com.chant.android.model.entities.Match
import com.chant.android.model.entities.Team
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone
import java.sql.DriverManager
import kotlin.concurrent.thread

object MatchDAO {

    fun getMatchList(): ArrayList<Match> {

        var matchList = ArrayList<Match>()

        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query =
            "select t1.name AS team1Name, t2.name AS team2Name, m.* from `match` m inner join team t1 on m.team1Id=t1.id inner join team t2 on m.team2Id=t2.id"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()) {


            var tmpMatch = Match(
                rs.getInt("id"),
                Team(rs.getInt("team1Id"), rs.getString("team1Name")),
                Team(rs.getInt("team2Id"), rs.getString("team2Name")),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("startDate").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getInt("goals1"),
                rs.getInt("goals2"),
                rs.getInt("status"),
                rs.getDouble("quota1"),
                rs.getDouble("quota2"),
                rs.getDouble("quotaTie")
            )

            matchList.add(tmpMatch)

        }

        ConnectionPool.getInstance().freeConnection(con)

        return matchList


    }


    fun getMatch(id: Int): Match? {

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "select t1.name AS team1Name, t2.name AS team2Name, m.* from `match` m inner join team t1 on m.team1Id=t1.id inner join team t2 on m.team2Id=t2.id where m.id = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, id)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpMatch = Match(
                rs.getInt("id"),
                Team(rs.getInt("team1Id"), rs.getString("team1Name")),
                Team(rs.getInt("team2Id"), rs.getString("team2Name")),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("startDate").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getInt("goals1"),
                rs.getInt("goals2"),
                rs.getInt("status"),
                rs.getDouble("quota1"),
                rs.getDouble("quota2"),
                rs.getDouble("quotaTie")
            )
            ConnectionPool.getInstance().freeConnection(con)
            return tmpMatch
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null


    }


}