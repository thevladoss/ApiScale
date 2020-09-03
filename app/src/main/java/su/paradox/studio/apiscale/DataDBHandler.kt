package su.paradox.studio.apiscale

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import kotlin.time.milliseconds

class DataDBHandler (context: Context, name: String?,
                     factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {
    val sdf = SimpleDateFormat("dd.MM.yyyy")

    override fun onCreate(db: SQLiteDatabase) {
        val createDateTable = ("CREATE TABLE " +
                TABLE_DATA + "("
                + COLUMN_DATEY + " TEXT," + COLUMN_DATEM + " TEXT," + COLUMN_DATED + " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_WEIGHT + " TEXT," + COLUMN_CHARGE + " INTEGER," + COLUMN_TEMP + " INTEGER" + ")")
        db.execSQL(createDateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DATA")
        onCreate(db)
    }

    fun addProduct(data: Data) {
        val values = ContentValues()
        values.put(COLUMN_DATEY, data.dateYear)
        values.put(COLUMN_DATEM, data.dateMonth)
        values.put(COLUMN_DATED, data.dateDay)
        values.put(COLUMN_TIME, data.time)
        values.put(COLUMN_WEIGHT, data.weight)
        values.put(COLUMN_CHARGE, data.charge)
        values.put(COLUMN_TEMP, data.temp)

        val db = this.writableDatabase

        db.insert(TABLE_DATA, null, values)
        db.close()
    }

    fun returnDataByFilter(dateStart: String, dateEnd: String): MutableList<Data> {
        val query =
            "SELECT * FROM $TABLE_DATA"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        val array: MutableList<Data> = mutableListOf()

        if (cursor.moveToFirst()){
            cursor.moveToFirst()
            do {
                val dateY = cursor.getString(0)
                val dateM = cursor.getString(1)
                val dateD = cursor.getString(2)
                if (sdf.parse(dateStart) <= sdf.parse("$dateD.$dateM.$dateY") && sdf.parse(dateEnd) >= sdf.parse("$dateD.$dateM.$dateY")) {
                    val time = cursor.getString(3)
                    val weight = cursor.getString(4).toFloat()
                    val charge = Integer.parseInt(cursor.getString(5))
                    val temp = Integer.parseInt(cursor.getString(6))
                    array.add(Data(dateY, dateM, dateD, time, weight, charge, temp))
                    println("f")
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        db.close()
        return array
    }

    fun returnAllData(): MutableList<Data> {
        val query =
            "SELECT * FROM $TABLE_DATA"

        val db = this.writableDatabase

        val cursor = db.rawQuery(query, null)

        val array: MutableList<Data> = mutableListOf()

        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            do {
                val dateY = cursor.getString(0)
                val dateM = cursor.getString(1)
                val dateD = cursor.getString(2)
                val time = cursor.getString(3)
                val weight = cursor.getString(4).toFloat()
                val charge = Integer.parseInt(cursor.getString(5))
                val temp = Integer.parseInt(cursor.getString(6))
                array.add(Data(dateY, dateM, dateD, time, weight, charge, temp))
            } while (cursor.moveToNext())
        }
        cursor.close()

        db.close()
        return array
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "dataDB.db"
        const val TABLE_DATA = "data"

        const val COLUMN_DATEY = "dateY"
        const val COLUMN_DATEM = "dateM"
        const val COLUMN_DATED = "dateD"
        const val COLUMN_TIME = "time"
        const val COLUMN_WEIGHT = "weight"
        const val COLUMN_CHARGE = "charge"
        const val COLUMN_TEMP = "temp"
    }
}