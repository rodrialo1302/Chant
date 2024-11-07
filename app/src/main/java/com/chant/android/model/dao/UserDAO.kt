package com.chant.android.model.dao

import com.chant.android.model.entities.Post
import com.chant.android.model.entities.Team
import com.chant.android.model.entities.User
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

object UserDAO {

    fun getUser(username : String) : User? {

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `user` u, `team` t WHERE username= ? AND u.team=t.id"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, username)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpUser = User(username, rs.getString("fullname"), rs.getString("mail"), rs.getString("password"),
                rs.getInt("coins"), Team(rs.getInt("id"), rs.getString("name")), rs.getString("bio"), rs.getInt("pfp"))

            ConnectionPool.getInstance().freeConnection(con)
            return tmpUser
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null


    }

    fun isUser(username : String, password : String) : Boolean{

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `user` WHERE username = ? AND password = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, username)
        stmt.setString(2, password)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            ConnectionPool.getInstance().freeConnection(con)
            return true


        }
        ConnectionPool.getInstance().freeConnection(con)
        return false


    }

    fun checkUser(username : String) : Boolean{

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `user` WHERE username = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, username)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            ConnectionPool.getInstance().freeConnection(con)
            return true


        }
        ConnectionPool.getInstance().freeConnection(con)
        return false


    }

    fun addUser(newUser: User) {

        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "INSERT INTO `user` VALUES (?,?,?,?,?,?,?,?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, newUser.username)
        stmt.setString(2, newUser.fullname)
        stmt.setString(3, newUser.mail)
        stmt.setString(4, newUser.password)
        stmt.setInt(5, newUser.coins)
        stmt.setInt(6, newUser.team.id)
        stmt.setString(7, newUser.bio)
        stmt.setInt(8, newUser.pfp)

        stmt.executeUpdate()

        ConnectionPool.getInstance().freeConnection(con)

    }

    fun updateUser(username: String, fullName : String, team : Int, bio : String){
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "UPDATE `user` SET fullname = ?, team = ?,bio = ? where username = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, fullName)
        stmt.setInt(2,team)
        stmt.setString(3,bio)
        stmt.setString(4,username)
        stmt.executeUpdate()
        ConnectionPool.getInstance().freeConnection(con)
    }

    fun updatePfp(username : String, pfp : Int){
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "UPDATE `user` SET pfp = ? where username = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, pfp)
        stmt.setString(2,username)
        stmt.executeUpdate()
        ConnectionPool.getInstance().freeConnection(con)

    }

    fun updateCoins(username : String, coinsChanged : Int){
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val newCoins : Int? = (getUser(username)?.coins ?: 0) + coinsChanged

        val query = "UPDATE `user` SET coins = ? WHERE username = ?"

        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, newCoins!!)
        stmt.setString(2, username)

        stmt.executeUpdate()
        ConnectionPool.getInstance().freeConnection(con)
    }



}