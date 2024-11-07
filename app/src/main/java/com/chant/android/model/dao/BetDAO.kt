package com.chant.android.model.dao

import com.chant.android.model.entities.Bet
import com.chant.android.model.entities.Match
import com.chant.android.model.entities.User
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object BetDAO {

    fun getBetList(postId : Int) : ArrayList<Bet> {
        var betList = ArrayList<Bet>()

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `bet` WHERE postId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, postId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            var tmpBet = Bet(rs.getString("author"), postId,
                rs.getInt("points"),
                rs.getInt("team"))

            betList.add(tmpBet)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return betList
    }


    fun getBet(postId : Int, author : String) : Bet? {

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `bet` WHERE postId= ? AND author = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, postId)
        stmt.setString(2, author)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpBet = Bet(author, postId,
                rs.getInt("points"),
                rs.getInt("team"))

            ConnectionPool.getInstance().freeConnection(con)
            return tmpBet
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null


    }

    fun addBet(user : User, partido : Match, betNo : Int, coins : Int){

        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "INSERT INTO `bet` VALUES (?,?,?,?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, user.username)
        stmt.setInt(2, partido.id)
        stmt.setInt(3, coins)
        stmt.setInt(4, betNo)


        stmt.executeUpdate()

        val newCoins = user.coins - coins

        val queryUser = "UPDATE `user` SET coins=? WHERE username=?"

        val stmt2 = con.prepareStatement(queryUser)
        stmt2.setInt(1, newCoins)
        stmt2.setString(2, user.username)

        stmt2.executeUpdate()


        ConnectionPool.getInstance().freeConnection(con)

    }


    fun deleteBet(bet: Bet) {
        val user = UserDAO.getUser(bet.author)

        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "DELETE FROM `bet` WHERE author = ? AND postId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, bet.author)
        stmt.setInt(2, bet.postId)

        stmt.executeUpdate()

        val newCoins = user!!.coins + bet.points

        val queryUser = "UPDATE `user` SET coins=? WHERE username=?"

        val stmt2 = con.prepareStatement(queryUser)
        stmt2.setInt(1, newCoins)
        stmt2.setString(2, user.username)

        stmt2.executeUpdate()


        ConnectionPool.getInstance().freeConnection(con)

        ConnectionPool.getInstance().freeConnection(con)

    }



}