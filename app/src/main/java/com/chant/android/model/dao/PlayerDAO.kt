package com.chant.android.model.dao

import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Purchase
import com.chant.android.model.entities.PurchaseLine
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


object PlayerDAO {

    fun getPlayer(id : Int) : Player?{
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `player` WHERE id= ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, id)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpPlayer = Player(id, rs.getInt("teamId"), rs.getString("name"), rs.getInt("avg"))

            ConnectionPool.getInstance().freeConnection(con)
            return tmpPlayer
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null



    }

    fun getRandPlayer() : Player? {
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `player` ORDER BY RAND() LIMIT 1"
        val stmt: PreparedStatement = con!!.prepareStatement(query)

        val rs: ResultSet = stmt.executeQuery()
        if (rs.next()){
            var tmpPlayer = Player(rs.getInt("id"), rs.getInt("teamId"), rs.getString("name"), rs.getInt("avg"))
            ConnectionPool.getInstance().freeConnection(con)
            return tmpPlayer
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null
        }

    fun getPlayersInTeam(teamId : Int) : ArrayList<Player> {
        var playerList = ArrayList<Player>()
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `player` WHERE teamId = ? ORDER BY AVG DESC"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, teamId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            var tmpPlayer = Player(rs.getInt("id"), teamId, rs.getString("name"), rs.getInt("avg"))

            playerList.add(tmpPlayer)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return playerList
    }

    fun getPlayersInTeamFromUser(user : String, teamId: Int) : ArrayList<Player> {
        var playerList = ArrayList<Player>()
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT DISTINCT plr.* FROM purchase p JOIN purchase_line pl ON p.id = pl.purchaseId JOIN player plr ON pl.playerId = plr.id JOIN team t ON plr.teamId = t.id WHERE p.author = ? AND t.id = ? ORDER BY plr.avg DESC"

        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, user)
        stmt.setInt(2, teamId)

        val rs: ResultSet = stmt.executeQuery()
        while (rs.next()){
            var tmpPlayer = Player(rs.getInt("id"), teamId, rs.getString("name"), rs.getInt("avg"))
            playerList.add(tmpPlayer)
        }

        return playerList
    }

}