package com.chant.android.model.dao

import com.chant.android.model.entities.Like
import com.chant.android.model.entities.Post
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone


object PostDAO {

    fun getPostList(matchId: Int): ArrayList<Post> {
        var postList = ArrayList<Post>()

        val likesMap = LikeDAO.getMatchLikes(matchId)

        val con : Connection? = ConnectionPool.getInstance().getConnection()



        val query = "SELECT p.*, u.pfp FROM `post` p, `user` u WHERE p.author = u.username AND p.matchId = ? AND p.parentId = -1 ORDER BY DATE DESC"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, matchId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()) {

            var tmpPost = Post(
                rs.getInt("id"),
                matchId,
                rs.getInt("parentId"),
                rs.getString("author"),
                rs.getInt("type"),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("date").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getString("text"),
                if (likesMap.contains(rs.getInt("id"))) likesMap[rs.getInt("id")]!! else ArrayList<Like>(),
                rs.getInt("pfp")
            )

            postList.add(tmpPost)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return postList
    }


    fun getPostList(username : String) : ArrayList<Post>{
        var postList = ArrayList<Post>()

        val likesMap = LikeDAO.getUserLikes(username)

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT p.*, u.pfp FROM `post` p, `user` u WHERE p.author=u.username AND author = ? ORDER BY DATE DESC"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, username)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){

            var tmpPost = Post(rs.getInt("id"),rs.getInt("matchId"),
                rs.getInt("parentId"), rs.getString("author"),rs.getInt("type"),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(rs.getTimestamp("date").time), TimeZone.getDefault().toZoneId()),
                rs.getString("text"), if (likesMap.contains(rs.getInt("id"))) likesMap[rs.getInt("id")]!! else ArrayList<Like>(),
                rs.getInt("pfp"))

            postList.add(tmpPost)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return postList
    }




    fun getPost(id: Int): Post? {

        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT p.*, u.pfp FROM `post` p, `user` u WHERE p.id= ? AND p.author = u.username"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, id)

        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            var tmpPost = Post(
                rs.getInt("id"), rs.getInt("matchId"),
                rs.getInt("parentId"), rs.getString("author"), rs.getInt("type"),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("date").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getString("text"), LikeDAO.getLikeList(rs.getInt("id")),
                rs.getInt("pfp")
            )

            ConnectionPool.getInstance().freeConnection(con)
            return tmpPost
        }
        ConnectionPool.getInstance().freeConnection(con)
        return null


    }

    fun getReplyList(parentId: Int): ArrayList<Post> {
        var postList = ArrayList<Post>()

        val likesMap = LikeDAO.getPostLikes(parentId)
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "SELECT p.*, u.pfp FROM `post` p, `user` u WHERE p.author = u.username AND p.parentId = ? ORDER BY DATE DESC"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, parentId)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()) {

            var tmpPost = Post(
                rs.getInt("id"),
                rs.getInt("matchId"),
                parentId,
                rs.getString("author"),
                rs.getInt("type"),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("date").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getString("text"),
                if (likesMap.contains(rs.getInt("id"))) likesMap[rs.getInt("id")]!! else ArrayList<Like>(),
                rs.getInt("pfp")
            )

            postList.add(tmpPost)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return postList
    }



    fun addPost(newPost: Post) {
        val formatter : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        val query = "INSERT INTO `post` VALUES (NULL,?,?,?,?,?,?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, newPost.matchId)
        stmt.setInt(2, newPost.parentId)
        stmt.setString(3, newPost.author)
        stmt.setInt(4, newPost.type)
        stmt.setTimestamp(5, Timestamp.valueOf(newPost.date.format(formatter)))
        stmt.setString(6, newPost.text)

        stmt.executeUpdate()

        ConnectionPool.getInstance().freeConnection(con)

    }


    fun deletePost(post : Post) {
        var replySize = PostDAO.getReplyList(post.id).size

        if (replySize == 0) {
            val con: Connection? = ConnectionPool.getInstance().getConnection()
            val query = "DELETE FROM `post` WHERE id = ?"
            val stmt: PreparedStatement = con!!.prepareStatement(query)
            stmt.setInt(1, post.id)

            stmt.executeUpdate()
            ConnectionPool.getInstance().freeConnection(con)
        }
        else {

            val con: Connection? = ConnectionPool.getInstance().getConnection()
            val query =
                "UPDATE `post` SET author = \"deleted\", text = \"El autor original ha borrado este Post\", date = \"1970-01-01 00:00:00\" WHERE id = ?"
            val stmt: PreparedStatement = con!!.prepareStatement(query)
            stmt.setInt(1, post.id)
            stmt.executeUpdate()
            ConnectionPool.getInstance().freeConnection(con)
        }
    }



}