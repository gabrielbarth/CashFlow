package com.gabrielbarth.cashflow.database

import com.gabrielbarth.cashflow.entity.FinancialTransaction

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, detail TEXT, amount DOUBLE, date TEXT) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${TABLE_NAME}")
        onCreate(db)
    }

    fun insert(transaction: FinancialTransaction) {
        val db = this.writableDatabase
        val value = ContentValues()
        value.put("type", transaction.type)
        value.put("detail", transaction.detail)
        value.put("amount", transaction.amount)
        value.put("date", transaction.date)
        db.insert(TABLE_NAME, null, value)
    }

    // not used yet
    fun update(transaction: FinancialTransaction) {
        val db = this.writableDatabase
        val value = ContentValues()
        value.put("type", transaction.type)
        value.put("detail", transaction.detail)
        value.put("amount", transaction.amount)
        value.put("date", transaction.date)
        db.update(TABLE_NAME, value, "_id=${transaction._id}", null)
    }

    fun delete(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "_id=${id}", null)
    }

    // not used yet
    fun find(id: Int): FinancialTransaction? {
        val db = this.writableDatabase
        val register = db.query(
            TABLE_NAME,
            null,
            "_id=${id}",
            null,
            null,
            null,
            null
        )
        if (register.moveToNext()) {
            val value = FinancialTransaction(
                register.getInt(COD),
                register.getString(TYPE),
                register.getString(DETAIL),
                register.getDouble(AMOUNT),
                register.getString(DATE)
            )
            return value
        } else {
            return null
        }
    }

    // not used yet
    fun list(): MutableList<FinancialTransaction> {
        val db = this.writableDatabase
        val register = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )
        var registerList = mutableListOf<FinancialTransaction>()
        while (register.moveToNext()) {
            val transaction = FinancialTransaction(
                register.getInt(COD),
                register.getString(TYPE),
                register.getString(DETAIL),
                register.getDouble(AMOUNT),
                register.getString(DATE)
            )
            registerList.add(transaction)
        }
        return registerList
    }

    fun cursorList(): Cursor {
        val db = this.writableDatabase
        val register = db.query(
            "transactions",
            null,
            null,
            null,
            null,
            null,
            null
        )
        return register
    }


    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "transactions"
        private const val COD = 0
        private const val TYPE = 1
        private const val DETAIL = 2
        private const val AMOUNT = 3
        private const val DATE = 4
    }
}
