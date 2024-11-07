package com.chant.android.model.dao

import java.sql.Connection
import java.sql.SQLException
import java.sql.DriverManager
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class ConnectionPool private constructor() {
    val url = "jdbc:mysql://ip/chant"
    val user = "chant"
    val password = "redacted"
    val maxPoolSize = 10
    val initPoolSize = 3


    private val connectionQueue: BlockingQueue<Connection> = ArrayBlockingQueue(maxPoolSize)




    init {
        initializePool()
    }

    private fun initializePool() {
        repeat(initPoolSize) {
            addNewConnnectiontoQueue()
        }
    }

    private fun addNewConnnectiontoQueue(){
        try {
            val connection = DriverManager.getConnection(url, user, password)
            connectionQueue.offer(connection)
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        }
    }

    fun getConnection(): Connection? {

        if (connectionQueue.size == 0)
        {
            addNewConnnectiontoQueue()
        }
        return connectionQueue.poll()
    }

    fun freeConnection(connection: Connection) {
        try {
            if (!connection.isClosed && !connectionQueue.offer(connection)) {
                connection.close()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile private var instance: ConnectionPool? = null

        fun getInstance(): ConnectionPool {
            return instance ?: synchronized(this) {
                instance ?: ConnectionPool().also { instance = it }
            }
        }
    }
}



















/*package com.chant.android.model.dao

import java.sql.DriverManager

object Connection {
    private const val connectionString = "jdbc:mysql://34.175.148.186:3306/chant"
    private const val username = "chant"
    private const val password = "sQLF2Zk_493XsC"

    fun getConnection(): java.sql.Connection {
        Class.forName("com.mysql.jdbc.Driver")
        return DriverManager.getConnection(connectionString, username, password)

    }

    fun freeConnection(conn: java.sql.Connection) {
        conn.close()
    }
}*/