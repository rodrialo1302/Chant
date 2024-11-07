package com.chant.android.model.dao

import com.chant.android.model.entities.Like
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

object LikeDAO {


    fun getMatchLikes(matchId : Int) : Map<Int, ArrayList<Like>> {
        val likeMap: HashMap<Int, ArrayList<Like>> = HashMap()

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "select l.* from `like` l inner join `post` p on  p.id = l.postId where p.matchId = ?;"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, matchId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            val tmpLike = Like(rs.getString("author"), rs.getInt("postId"),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()))

            if (!likeMap.contains(tmpLike.postId))
                likeMap[tmpLike.postId] = ArrayList()

            likeMap[tmpLike.postId]!!.add(tmpLike)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return likeMap
    }

    fun getPostLikes(parentId : Int) : Map<Int, ArrayList<Like>> {
        val likeMap: HashMap<Int, ArrayList<Like>> = HashMap()

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "select l.* from `like` l inner join `post` p on  p.id = l.postId where p.parentId = ?;"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, parentId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            val tmpLike = Like(rs.getString("author"), rs.getInt("postId"),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()))

            if (!likeMap.contains(tmpLike.postId))
                likeMap[tmpLike.postId] = ArrayList()

            likeMap[tmpLike.postId]!!.add(tmpLike)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return likeMap
    }

    fun getUserLikes(author : String) : Map<Int, ArrayList<Like>> {
        val likeMap: HashMap<Int, ArrayList<Like>> = HashMap()

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "select l.* from `like` l inner join `post` p on  p.id = l.postId where p.author = ?;"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, author)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            val tmpLike = Like(author, rs.getInt("postId"),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()))

            if (!likeMap.contains(tmpLike.postId))
                likeMap[tmpLike.postId] = ArrayList()

            likeMap[tmpLike.postId]!!.add(tmpLike)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return likeMap
    }




    fun getLikeList(postId : Int) : ArrayList<Like> {
        val likeList = ArrayList<Like>()

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `like` WHERE postId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, postId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            val tmpLike = Like(rs.getString("author"), postId,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()))

            likeList.add(tmpLike)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return likeList
    }


    fun getLike(postId : Int, author : String) : Like? {

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT * FROM `like` WHERE postId= ? AND author = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, postId)
        stmt.setString(2, author)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpLike = Like(author, postId,
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()))

            ConnectionPool.getInstance().freeConnection(con)
            return tmpLike
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null


    }

    fun addLike (newLike : Like) {
        val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "INSERT INTO `like` VALUES (?,?,?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, newLike.author)
        stmt.setInt(2, newLike.postId)
        stmt.setTimestamp(3, Timestamp.valueOf(newLike.date.format(formatter)))


        stmt.executeUpdate()

        ConnectionPool.getInstance().freeConnection(con)

    }

    fun deleteLike(postId: Int, author : String) {
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "DELETE FROM `like` WHERE author = ? AND postId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, author)
        stmt.setInt(2,postId)

        stmt.executeUpdate()

        ConnectionPool.getInstance().freeConnection(con)

    }

    fun deleteAllLikes(postId: Int) {
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "DELETE FROM `like` WHERE postId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1,postId)

        stmt.executeUpdate()

        ConnectionPool.getInstance().freeConnection(con)

    }


}