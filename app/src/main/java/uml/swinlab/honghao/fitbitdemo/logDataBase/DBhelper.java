package uml.swinlab.honghao.fitbitdemo.logDataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Honghao on 4/11/2016.
 */
public class DBhelper extends SQLiteOpenHelper{
    private final String TAG = "SQLiteOpenHelper";

    public DBhelper(Context context){
        super(context, DBcontract.dbFile, null, DBcontract.dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(DBcontract.CREATE_FOODLOG_TABLE);
        db.execSQL(DBcontract.CREATE_MOVES_TABLE);
        db.execSQL(DBcontract.CREATE_AUTOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }


}
