package pt.ipca.pa.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.google.gson.annotations.SerializedName
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Payment.Payment
import pt.ipca.pa.Revervation.Reservation
import java.util.jar.Attributes

class DataBaseHandlerPayments(ctx:Context):SQLiteOpenHelper(ctx, DB_NAME ,null,DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        var CREATE_TABLE = "CREATE TABLE $TABLE_NAME($ID TEXT PRIMARY KEY, $Reservation_ID TEXT, $AMOUNT INTEGRE, $DATE TEXT);"
        db?.execSQL(CREATE_TABLE)
    }


    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        Log.w(
            DataBaseHandlerPark::class.java.name,
            "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data"
        )
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.version = oldVersion
    }
    fun addPayment(payment: Payment) {
        try {
            val db = writableDatabase
            val selectquery = "SELECT * FROM $TABLE_NAME WHERE $ID = '${payment.id}';"
            val cursor = db.rawQuery(selectquery, null)
            if (cursor.count <= 0) {
                val values = ContentValues().apply {
                    put(ID, payment.id)
                    put(Reservation_ID, payment.reservationId)
                    put(AMOUNT, payment.amount)
                    put(DATE, payment.date)
                }
                db.insert(TABLE_NAME, null, values)
            }
            cursor.close()
        } catch (e: Exception) {
            println("$e")
        }
    }

    fun getPayment(id: String): Payment? {
        try {
            val db = readableDatabase
            val selectquery = "SELECT * FROM $TABLE_NAME WHERE $ID = '$id';"
            val cursor = db.rawQuery(selectquery, null)
            cursor?.moveToFirst()
            val payment = ppPayment(cursor)
            cursor.close()
            return payment
        } catch (e: Exception) {
            println("$e")
            return null
        }
    }

    fun getPaymentList(): ArrayList<Payment> {
        val paymentList = ArrayList<Payment>()
        try {
            val db = readableDatabase
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val payment = ppPayment(cursor)
                        paymentList.add(payment)
                    } while (cursor.moveToNext())
                }
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return paymentList
    }



    fun ppPayment(cursor: Cursor): Payment {
        val payment = Payment(null, "", "", "", "","")
        payment.id = cursor.getString(cursor.getColumnIndexOrThrow(ID))
        payment.reservationId = cursor.getString(cursor.getColumnIndexOrThrow(Reservation_ID))
        payment.amount = cursor.getString(cursor.getColumnIndexOrThrow(AMOUNT))
        payment.date = cursor.getString(cursor.getColumnIndexOrThrow(DATE))
        return payment
    }




    companion object{
        private val DB_VERSION=1
        private val DB_NAME="PDM"
        private val ID="ID"
        private val Reservation_ID="ReservatioN_Id"
        private val AMOUNT="Amount"
        private val DATE="DATE"
        private val TABLE_NAME="Payments"


    }
}