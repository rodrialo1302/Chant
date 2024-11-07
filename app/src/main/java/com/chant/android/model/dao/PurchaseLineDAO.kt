package com.chant.android.model.dao

import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Purchase
import com.chant.android.model.entities.PurchaseLine
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet


object PurchaseLineDAO {

    fun createPurchaseLine(purchaseId : Int, playerId : Int){
        val con : Connection? = ConnectionPool.getInstance().getConnection()

        val query = "INSERT INTO `purchase_line` VALUES(?, ?)"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, purchaseId)
        stmt.setInt(2, playerId)

        stmt.executeUpdate()
        ConnectionPool.getInstance().freeConnection(con)
    }

    fun getPurchaseLines(purchaseId : Int) : ArrayList<PurchaseLine>{
        val con : Connection? = ConnectionPool.getInstance().getConnection()
        var purchaseLineList = ArrayList<PurchaseLine>()
        val query = "SELECT * FROM `purchase_line` WHERE purchaseId = ?"
        val stmt: PreparedStatement = con!!.prepareStatement(query)
        stmt.setInt(1, purchaseId)

        val rs: ResultSet = stmt.executeQuery()
        while (rs.next()){

            var tmpPurchaseLine = PurchaseLine(rs.getInt("purchaseId"), rs.getInt("playerId"))

            purchaseLineList.add(tmpPurchaseLine)

        }
        return purchaseLineList
    }
}