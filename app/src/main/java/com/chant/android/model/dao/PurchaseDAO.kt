package com.chant.android.model.dao

import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Purchase
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


object PurchaseDAO {

    fun createPurchase(author : String, points : Int) : Int{
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "INSERT INTO `purchase` (author, points) VALUES(?, ?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS
        )
        stmt.setString(1, author)
        stmt.setInt(2, points)

        stmt.executeUpdate()
        val generatedKeys = stmt.generatedKeys
        if(generatedKeys.next()){
            val generatedId: Long = generatedKeys.getLong(1)
            return generatedId.toInt()
        }
        val purchaseId = stmt.generatedKeys.getLong(1)
        ConnectionPool.getInstance().freeConnection(con)
        return purchaseId.toInt()
    }

    fun getPurchasesFromAuthor(author: String) : ArrayList<Purchase> {
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        var purchaseList = ArrayList<Purchase>()
        val query = "SELECT * FROM `purchase` WHERE author = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setString(1, author)

        val rs: ResultSet = stmt.executeQuery()

        while (rs.next()){
            var tmpPurchase = Purchase(rs.getInt("id"), rs.getString("author"), rs.getInt("points"))
            purchaseList.add(tmpPurchase)
        }
        ConnectionPool.getInstance().freeConnection(con)
        return purchaseList
    }

}