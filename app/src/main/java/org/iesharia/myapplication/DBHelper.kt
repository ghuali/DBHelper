package org.iesharia.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory? = null) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COl + " TEXT," +
                AGE_COL + " TEXT" + ")")

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }


    fun addName(name : String, age : String ){

        val values = ContentValues()

        values.put(NAME_COl, name)
        values.put(AGE_COL, age)

        val db = this.writableDatabase

        db.insert(TABLE_NAME, null, values)

        db.close()
    }

    fun getName(): Cursor? {

        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun deleteName(id: String): Boolean {
        val db = this.writableDatabase

        // Establecemos la condición para eliminar el registro
        val whereClause = "$ID_COL = ?"
        val whereArgs = arrayOf(id)

        // Ejecutamos el DELETE en la base de datos
        val rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs)

        db.close()

        // Retornamos true si se eliminó al menos una fila
        return rowsDeleted > 0
    }

    companion object{
        private val DATABASE_NAME = "nombres"

        private val DATABASE_VERSION = 1

        val TABLE_NAME = "name_table"

        val ID_COL = "id"

        val NAME_COl = "nombre"

        val AGE_COL = "edad"
    }
}