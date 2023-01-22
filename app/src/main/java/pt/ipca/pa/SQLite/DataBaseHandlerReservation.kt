package pt.ipca.pa.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pt.ipca.pa.Revervation.Reservation

class DataBaseHandlerReservation(ctx:Context):SQLiteOpenHelper(ctx, DB_NAME ,null,DB_VERSION) {
    //Primeira vez que entrar cria a tabela
    override fun onCreate(p0: SQLiteDatabase?) {
        var CREATE_TABLE="CREATE TABLE $TABLE_NAME($ID TEXT ,$AMOUNT TEXT, $DAY TEXT);"
        p0?.execSQL(CREATE_TABLE)
    }


    override fun onUpgrade(
        p0: SQLiteDatabase?, p1: Int, p2: Int
    ) {
        println("dropped table")
        val DROP_TABLE="DROP TABLE IF EXISTS $TABLE_NAME;"
        p0?.execSQL(DROP_TABLE)
        onCreate(p0)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.version = oldVersion
    }

    fun addReservation(reservation:Reservation){
        try {
            val p0=writableDatabase
            val selectquery="SELECT * FROM $TABLE_NAME WHERE $ID = '${reservation.id}';"
            var cursor =p0.rawQuery(selectquery,null)
            if(cursor.count <= 0) {
                val values= ContentValues().apply{
                    put(ID, reservation.id)
                    put(AMOUNT, reservation.amount)
                    put(DAY, reservation.day)
                }
                p0.insert(TABLE_NAME,null,values)
            }
            cursor.close()
        } catch (e: Exception) {
            println("$e")
        }
    }

    fun getReservation(id:String):Reservation? {
        try {
            val p0=readableDatabase
            val selectquery="SELECT * FROM $TABLE_NAME WHERE $ID = $id;"
            var mouse =p0.rawQuery(selectquery,null)
            mouse?.moveToFirst()
            val reservation=ppReservation(mouse)
            mouse.close()
            return reservation
        } catch (e: Exception) {
            println("$e")
            return null
        }
    }
    fun getReservationList():ArrayList<Reservation>{
        var reservationList=ArrayList<Reservation>()
        try {
            val p0 =readableDatabase
            val selectQuery="SELECT * FROM $TABLE_NAME"
            val mouse =p0.rawQuery(selectQuery,null)
            if(mouse.count > 0)
                if(mouse.moveToFirst()){
                    do{
                        val reservation=ppReservation(mouse)
                        reservationList.add(reservation)
                    }while (mouse.moveToNext())
                }
            mouse.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return reservationList
    }



    fun ppReservation(mouse:Cursor):Reservation{
        var reservation=Reservation("","","","","","","")
        reservation.id=mouse.getString(mouse.getColumnIndexOrThrow(ID))
        reservation.amount=mouse.getString(mouse.getColumnIndexOrThrow(AMOUNT))
        reservation.day= mouse.getString(mouse.getColumnIndexOrThrow(DAY))
        return reservation
    }

    companion object{
        private val DB_VERSION=1
        private val DB_NAME="PDM"
        private val ID="ID"
        private val AMOUNT = "Amount"
        private val DAY = "Day"
        private val TABLE_NAME="Reservations"

    }
}
