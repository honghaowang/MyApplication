package uml.swinlab.honghao.fitbitdemo;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.util.MovesStatus;

import uml.swinlab.honghao.fitbitdemo.logDataBase.*;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.util.MovesStatus;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    private Intent intent;
    private FitbitAPI fitbit = new FitbitAPI();
    private Context context = this;
    private TextView text;
    MyAlarmReceiver alarmReceiver;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(this, FitbitActivity.class);
        SetPermission set = new SetPermission(context, this);
        alarmReceiver = new MyAlarmReceiver();
        text = (TextView) findViewById(R.id.text);
        set.addPermissionList("android.permission.READ_EXTERNAL_STORAGE");
        set.addPermissionList("android.permission.WRITE_EXTERNAL_STORAGE");
        set.addPermissionList("android.permission.INTERNET");
        set.addPermission("android.permission.WAKE_LOCK");
        set.getPermission();
        db = new DBhelper(this).getReadableDatabase();

        if(!fitbit.loadAuthor()) {
            Intent intent = new Intent(MainActivity.this, FitbitActivity.class);
            startActivity(intent);
        }
        else{
            //alarmReceiver.
            alarmReceiver.setAlarm(context);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "--->>>onResume");
        if(!fitbit.loadAuthor()) {
            Intent intent = new Intent(MainActivity.this, FitbitActivity.class);
            startActivity(intent);
        }
        else{

            alarmReceiver.setAlarm(context);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    public void updata(){

        //db.execSQL(DBcontract.DELETE_FOODLOG_TABLE);
        String r = "-----" + DBcontract.FOOD_LOG_TABLE + "-------------\n";
        Cursor queryResult = db.query(DBcontract.FOOD_LOG_TABLE, null, null, null, null, null, null);
        if(queryResult==null)
            return;
        queryResult.moveToFirst();
        //String[] colunmnName = queryResult.getColumnNames();
        int colunmnCount = queryResult.getColumnCount();

        Log.e(TAG, String.valueOf(queryResult.getCount()) + "recorders");
        for(int i = 0; i<queryResult.getCount(); i++)
        {

            for(int j = 0; j<colunmnCount; j++) {
                r += queryResult.getString(j) + "==";
                //Log.i("Query Result", "[" + colunmnName[j] + "] = " + r);
            }
            queryResult.moveToNext();
            r += "\n**************************\n";
        }
        //Log.d(TAG, r);

        r += "-----" + DBcontract.MOVES_TABLE + "-------------\n";
        queryResult = db.query(DBcontract.MOVES_TABLE,null, null, null, null, null, null);
        if(queryResult==null)
            return;
        queryResult.moveToFirst();

        colunmnCount = queryResult.getColumnCount();

        Log.e(TAG, String.valueOf(queryResult.getCount()) + "recorders");
        for(int i = 0; i<queryResult.getCount(); i++)
        {

            for(int j = 0; j<colunmnCount; j++) {
                r += queryResult.getString(j) + "==";
                //Log.i("Query Result", "[" + colunmnName[j] + "] = " + r);
            }
            queryResult.moveToNext();
            r += "\n**************************\n";
        }
        text.setText(r);
    }

    private void doRequestAuthInApp(){
        try {
            MovesAPI.init(this, Constant.CLIENT_ID, Constant.clientSecret, Constant.MOVES_SCOPES,Constant.REDIRECT_URI);
            //Log.e(TAG, "----->" + MovesAPI.getClientDetails().getClientID());
            MovesAPI.authenticate(authDialogHandler, MainActivity.this);
            //startActivityForResult(intent, Constant.REQUEST_AUTHORIZE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Moves app not installed", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
        @Override
        public void onSuccess(AuthData arg0) {
            Log.e(TAG, "Access Token : " + arg0.getAccessToken() + "\n"
                    + "Expires In : " + arg0.getExpiresIn() + "\n"
                    + "User ID : " + arg0.getUserID());

        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.e(TAG, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.author_fitbit:
                Intent intent = new Intent(MainActivity.this, FitbitActivity.class);
                startActivity(intent);
                break;
            case R.id.author_moves:
                doRequestAuthInApp();
                break;
            case R.id.check_data:
                updata();
                break;
            case R.id.reset:
                db.execSQL(DBcontract.DELETE_MOVES_TABLE);
                db.execSQL(DBcontract.DELETE_FOODLOG_TABLE);
                db.execSQL(DBcontract.CREATE_FOODLOG_TABLE);
                db.execSQL(DBcontract.CREATE_MOVES_TABLE);
                db.execSQL(DBcontract.CREATE_AUTOR_TABLE);
                break;
        }
        return true;
    }
}
