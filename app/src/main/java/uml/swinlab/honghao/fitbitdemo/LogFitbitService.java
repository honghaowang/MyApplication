package uml.swinlab.honghao.fitbitdemo;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.place.PlaceData;
import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.util.MovesStatus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uml.swinlab.honghao.fitbitdemo.fileupload.FileUploader;
import uml.swinlab.honghao.fitbitdemo.logDataBase.DBcontract;
import uml.swinlab.honghao.fitbitdemo.logDataBase.DBhelper;

/**
 * Created by Honghao on 4/7/2016.
 */
public class LogFitbitService extends IntentService{
    public LogFitbitService(){
        super("LogFitbitService");
    }
    private final String TAG = "LogFitbitService";
    SQLiteDatabase db;// = new DBhelper(this).getReadableDatabase();

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Start on Handle Intent");
        db = new DBhelper(this).getReadableDatabase();
        FitbitAPI fitbit = new FitbitAPI();
        fitbit.loadAuthor();
        try {
            HttpResponse response = null;
            HttpClient client = new DefaultHttpClient();
            String url = String.format(fitbit.foodLogRequest, fitbit.userID);
            HttpGet get = new HttpGet(url);
            System.out.print(fitbit.token);
            Log.d(TAG, fitbit.token);
            get.setHeader("Authorization", "Bearer " + fitbit.token);
            response = client.execute(get);
            Log.d(TAG, "URL: " + url);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String webServiceInfo2 = "";

            while ((webServiceInfo2 = rd.readLine()) != null) {
                System.out.print(webServiceInfo2);
                Log.d(TAG,webServiceInfo2);
                String foods = new JSONObject(webServiceInfo2).getString("foods");

                JSONArray jArray = new JSONArray(foods);
                for(int i=0; i<jArray.length(); i++){
                    ContentValues values = new ContentValues();
                    JSONObject data = jArray.getJSONObject(i);
                    values.put(DBcontract.logID, data.getString("logId"));
                    values.put(DBcontract.logDate, data.getString("logDate"));
                    JSONObject loggedfood = new JSONObject(data.getString("loggedFood"));
                    values.put(DBcontract.foodName, loggedfood.getString("name"));
                    values.put(DBcontract.mealTypeId, getMealType(loggedfood.getInt("mealTypeId")));
                    if(data.has("nutritionalValues")){
                        String nutritional = data.getString("nutritionalValues");

                        JSONObject nutritionalValues = new JSONObject(nutritional);
                        values.put(DBcontract.calories, nutritionalValues.getInt("calories"));
                        values.put(DBcontract.carbs, nutritionalValues.getInt("carbs"));
                        values.put(DBcontract.fat, nutritionalValues.getInt("fat"));
                        values.put(DBcontract.fiber, nutritionalValues.getInt("fiber"));
                        values.put(DBcontract.protein, nutritionalValues.getInt("protein"));
                        values.put(DBcontract.sodium, nutritionalValues.getInt("sodium"));
                    }

                    //check if the log exists
                    Cursor cursor = db.rawQuery(DBcontract.searchLodID, null);
                    if(cursor==null)
                        return;
                    cursor.moveToFirst();
                    Boolean insert = true;
                    for(i = 0; i<cursor.getCount(); i++)
                    {
                        if(cursor.getString(0) == data.getString("logId"))
                            insert = false;
                    }

                    if(insert) {
                        db.insert(DBcontract.FOOD_LOG_TABLE, null, values);

                    }else
                        Log.d(TAG, "log exists");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
        try{
            //MovesAPI.init(this, Constant.CLIENT_ID, Constant.clientSecret, Constant.MOVES_SCOPES,Constant.REDIRECT_URI);

            MovesAPI.getStoryline_SingleDay(storylineHandler, "20160425", null, false);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private MovesHandler<ArrayList<StorylineData>> storylineHandler = new MovesHandler<ArrayList<StorylineData>>() {
        @Override
        public void onSuccess(ArrayList<StorylineData> result) {
            Log.e(TAG, "Summary Items : " + result.size());
            ArrayList<SegmentData> segments = result.get(0).getSegments();
            ContentValues values = new ContentValues();
            for(int i=0;i<segments.size(); i++){
                SegmentData temp = segments.get(i);
                String type = temp.getType();
                if(type.equals("place")) {
                    values.put(DBcontract.type, type);
                    PlaceData place = temp.getPlace();
                    values.put(DBcontract.lat, place.getLocation().getLat());
                    values.put(DBcontract.lon, place.getLocation().getLon());
                    values.put(DBcontract.start, temp.getStartTime());
                    values.put(DBcontract.end, temp.getEndTime());
                    values.put(DBcontract.duration, "");
                    values.put(DBcontract.distance, "");
                    values.put(DBcontract.steps, "");
                    values.put(DBcontract.calories, "");
                    db.insert(DBcontract.MOVES_TABLE, null, values);
                    values.clear();
                }
                if(type.equals("move")){
                    ArrayList<ActivityData> activitys = temp.getActivities();
                    for(i=0; i<activitys.size(); i++){
                        values.put(DBcontract.type, activitys.get(i).getActivity());
                        values.put(DBcontract.lat, activitys.get(i).getTrackPoints().get(0).getLat());
                        values.put(DBcontract.lon, activitys.get(i).getTrackPoints().get(0).getLon());
                        values.put(DBcontract.start, activitys.get(i).getStartTime());
                        values.put(DBcontract.end, activitys.get(i).getEndTime());
                        values.put(DBcontract.duration, activitys.get(i).getDuration());
                        values.put(DBcontract.distance, activitys.get(i).getDistance());
                        values.put(DBcontract.steps, activitys.get(i).getSteps());
                        values.put(DBcontract.calories, activitys.get(i).getCalories());
                        db.insert(DBcontract.MOVES_TABLE, null, values);
                        values.clear();
                    }
                }

            }

        }

        @Override
        public void onFailure(MovesStatus status, String message) {
            Log.e(TAG, "Request Failed! \n"
                    + "Status Code : " + status + "\n"
                    + "Status Message : " + message + "\n\n"
                    + "Specific Message : " + status.getStatusMessage());
        }
    };


    public String getMealType(int type){
        switch(type){
            case 0:
                return "anytime";
            case 1:
                return "breakfast";
            case 2:
                return "lunch";
            case 3:
                return "dinner";
            case 4:
                return "morning snack";
            case 5:
                return "afternoon snack";
            case 6:
                return "evening snack";
        }
        return "ERROR";
    }
}
