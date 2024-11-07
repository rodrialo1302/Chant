package com.chant.android.model.dao

import com.chant.android.model.entities.Notification
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

object NotificationDAO {


    fun getNotifications(author: String): ArrayList<Notification> {
        var notiList = ArrayList<Notification>()

        val con : Connection? = ConnectionPool.getInstance().getConnection()


        val query = """
                    SELECT 
                    author, date, postId, text, type
                    FROM
                    (
                    select l.author as author, l.date as date, l.postId as postId , p.text as text, "like" as type, p.author as parentAuthor from 
                    `like`l inner join `post`p ON p.id = l.postId
                    UNION ALL
                    select p.author as author, p.date as date, p.id as postId , p.text as text, "post" as type, pauth.author as parentAuthor from
                    `post`p inner join `post`pauth ON p.parentId = pauth.id
                    where p.parentId <> -1
                    )t
                    WHERE parentAuthor = ? AND author <> parentAuthor
                    ORDER BY DATE DESC
                    """
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, author)
        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()) {

            var tmpNoti = Notification(
                rs.getString("author"),
                rs.getInt("postId"),
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(rs.getTimestamp("date").time),
                    TimeZone.getDefault().toZoneId()
                ),
                rs.getString("text"),
                rs.getString("type")
            )

            notiList.add(tmpNoti)

        }

        ConnectionPool.getInstance().freeConnection(con)
        return notiList
    }



}