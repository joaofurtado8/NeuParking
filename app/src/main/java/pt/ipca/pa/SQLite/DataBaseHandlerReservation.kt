package pt.ipca.pa.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import pt.ipca.pa.Park.Park
import pt.ipca.pa.Revervation.Reservation
import java.util.jar.Attributes

class DataBaseHandlerReservation(ctx:Context):SQLiteOpenHelper(ctx, DB_NAME ,null,DB_VERSION) {

//Primeira vez que entrar cria a tabela
    override fun onCreate(p0: SQLiteDatabase?) {
var CREATE_TABLE="CREATE TABLE $TABLE_NAME($SLOT_ID INTEGER PRIMARY KEY,$START_TIME DATA,$END_TIME DATA,$DAY TEXT);"
        p0?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
val DROP_TABLE="DROP TABLE IF EXISTS $TABLE_NAME;"
        p0?.execSQL(DROP_TABLE)
        onCreate(p0)
    }

    //adicionar park
fun addReservation(reservation:Reservation)
{
    val p0=writableDatabase
    val values= ContentValues().apply{
        put(START_TIME,reservation.startTime)
        put(END_TIME,reservation.endTime)
        put(DAY,reservation.day)

    }
    p0.insert(TABLE_NAME,null,values)
}


//DEVOLVE VALORES POR PESQUISA
    fun getReservation(slot_id:String):Reservation
    {
        val p0=readableDatabase
        val selectquery="SELECT * FROM $TABLE_NAME WHERE $SLOT_ID = $slot_id;"
        var mouse =p0.rawQuery(selectquery,null)
        mouse?.moveToFirst()
         val reservation=ppReservation(mouse)
        mouse.close()
        return park
    }


    fun getParksList():ArrayList<Park>{
        var parkList=ArrayList<Park>()
        val p0 =readableDatabase
        val selectQuery="SELECT * fROM $TABLE_NAME"
        val mouse =p0.rawQuery(selectQuery,null)
        if(mouse!=null)
            if(mouse.moveToFirst()){
                do{
val park=ppPark(mouse)
                    parkList.add(park)
                }while (mouse.moveToNext())
            }
        mouse.close()
        return parkList
    }


    fun ppPark(mouse:Cursor):Park{
        var park=Park("","","",0,"",)
        park.name=mouse.getString(mouse.getColumnIndexOrThrow(NAME))
        return park
    }
//falta funcoes
    //ver video


    companion object{
        private val DB_VERSION=1
        private val DB_NAME="PDM"
        private val SLOT_ID="ID"
        private val TABLE_NAME="Reservation"
        private val START_TIME="Star_time"
        private val END_TIME="End_Time"
        private val DAY="Day"
    }
}