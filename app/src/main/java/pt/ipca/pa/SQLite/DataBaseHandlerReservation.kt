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
        var CREATE_TABLE="CREATE TABLE $TABLE_NAME($ID TEXT ,$AMOUNT TEXT, $SLOT_ID TEXT, $START_TIME TEXT, $END_TIME TEXT, $DAY TEXT);"
        p0?.execSQL(CREATE_TABLE)
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

    fun addReservation(reservation:Reservation){
        try {
            val p0=writableDatabase
            val selectquery="SELECT * FROM $TABLE_NAME WHERE $ID = '${reservation.id}';"
            var cursor =p0.rawQuery(selectquery,null)
            if(cursor.count <= 0) {
                val values= ContentValues().apply{
                    put(ID, reservation.id)
                    put(AMOUNT, reservation.amount)
                    put(SLOT_ID, reservation.slotId)
                    put(START_TIME, reservation.startTime)
                    put(END_TIME, reservation.endTime)
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
            if(mouse!=null)
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
        reservation.slotId=mouse.getString(mouse.getColumnIndexOrThrow(SLOT_ID))
        reservation.startTime=mouse.getString(mouse.getColumnIndexOrThrow(START_TIME))
        reservation.endTime=mouse.getString(mouse.getColumnIndexOrThrow(END_TIME))
        reservation.day=mouse.getString(mouse.getColumnIndexOrThrow(DAY))
        return reservation
    }





    companion object{
        private val DB_VERSION=1
        private val DB_NAME="PDM"
        private val ID="ID"
        private val AMOUNT = "Amount"
        private val SLOT_ID="Slot_ID"
        private val TABLE_NAME="Reservations"
        private val START_TIME="Star_time"
        private val END_TIME="End_Time"
        private val DAY="Day"
    }
}