package pt.ipca.pa.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import pt.ipca.pa.Park.Park

class DataBaseHandlerPark(ctx: Context) : SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {
    //Primeira vez que entrar cria a tabela
    override fun onCreate(p0: SQLiteDatabase?) {
        var CREATE_TABLE = "CREATE TABLE $TABLE_NAME($ID INTEGER ,$NAME TEXT, $LOCATION TEXT, $DESCRIPTION TEXT, $AVALAIBLESPOTS TEXT);"
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
    fun addPark(park: Park) {
        try {
            val p0 = readableDatabase
            val selectquery = "SELECT * FROM $TABLE_NAME WHERE $NAME = '${park.name}';"
            val mouse = p0.rawQuery(selectquery, null)
            if (mouse.count == 0) {
                val p1 = writableDatabase
                val values = ContentValues().apply {
                    put(NAME, park.name)
                    put(AVALAIBLESPOTS, park.availableSpots)
                }
                p1.insert(TABLE_NAME, null, values)
            }
            mouse.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //DEVOLVE VALORES POR PESQUISA
    fun getPark(name:String):Park? {
        try {
            val p0=readableDatabase
            val selectquery="SELECT * FROM $TABLE_NAME WHERE $NAME = $name;"
            var mouse =p0.rawQuery(selectquery,null)
            mouse?.moveToFirst()
            val park=ppPark(mouse)
            mouse.close()
            return park
        } catch (e: Exception) {
            e.printStackTrace()
            return null // ou retornar uma mensagem de erro
        }
    }


    fun getParksList():ArrayList<Park>{
        var parkList=ArrayList<Park>()
        try {
            val p0 = readableDatabase
            val selectQuery = "SELECT * FROM $TABLE_NAME"
            val mouse = p0.rawQuery(selectQuery, null)
            if (mouse != null) {
                if (mouse.moveToFirst()) {
                    do {
                        val park = ppPark(mouse)
                        parkList.add(park)
                    } while (mouse.moveToNext())
                }
            }
            mouse.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return parkList
    }


    fun ppPark(mouse:Cursor):Park{
        var park=Park("","",0,0,"",0)
        park.name=mouse.getString(mouse.getColumnIndexOrThrow(NAME))
        return park
    }


    companion object{
        private val DB_VERSION=1
        private val DB_NAME="PDM"
        private val TABLE_NAME="Park"
        private val ID="Id"
        private val NAME="Name"
        private val LOCATION="Location"
        private val DESCRIPTION="Description"
        private val AVALAIBLESPOTS="AvailableSpots"
    }
}

