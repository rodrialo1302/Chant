package com.chant.android.model.dao

import com.chant.android.model.entities.Team
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


object TeamDAO {

    fun getTeam(id : Int) : Team?{
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `team` WHERE id= ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, id)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpTeam = Team(id, rs.getString("name"))

            ConnectionPool.getInstance().freeConnection(con)
            return tmpTeam
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null



    }

    fun getTeamList() : ArrayList<Team> {
        var teamList = ArrayList<Team>()
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `team`"
        val stmt: PreparedStatement = con!!.prepareStatement(query)

        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            var tmpTeam = Team(rs.getInt("id"), rs.getString("name"))

            teamList.add(tmpTeam)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return teamList
    }


}