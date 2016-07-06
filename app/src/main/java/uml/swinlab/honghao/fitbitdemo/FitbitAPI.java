package uml.swinlab.honghao.fitbitdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FitbitAPI {
    private final String TAG = "FitbitAPI";

    //basic info from register APP
    public final String clientID = "227PRW";
    public final String clientSecret = "6bca3e00b9700f9518402e3135acaf87";
    public final String scope = "activity%20nutrition%20heartrate%20location%20nutrition%20profile%20settings%20sleep%20social%20weight";
    public final String redirectURI = "http%3A%2F%2Fswin06.cs.uml.edu%2Fprocessdata%2F";
    public final String AuthorizationURL = "https://www.fitbit.com/oauth2/authorize?response_type=token&client_id=" + clientID + "&redirect_uri=" + redirectURI + "&scope=" + scope + "&expires_in=2592000";
    public final String basic = clientID + ":" + clientSecret;

    //URL of different log request
    public String profileRequest = "https://api.fitbit.com/1/user/-/profile.json";
    public String foodLogRequest = "https://api.fitbit.com/1/user/%s/foods/log/date/"+ formatDate() +".json";
    public String waterLogRequest = "https://api.fitbit.com/1/user/%s/foods/log/water/date/" + formatDate() + ".json";
    public String loggingFoodRequest = "https://api.fitbit.com/1/user/%s/foods/%s";

    // userID and token will get after Authentication
    public String userID;
    public String token;
    private Context context;
    /* @Function: Fitbit
     *            Initial a Fitbit object and detect author file and log file
     */
    public FitbitAPI(){
        try{
            File file = new File(Constant.floderPath);
            if(!file.exists())
                file.mkdir();
            file = new File(Constant.authorFile);
            if (!file.exists())
                file.createNewFile();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public class Authentication extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try{
                HttpResponse response = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(profileRequest);
                get.setHeader("Authorization", "Bearer " + token);
                response = client.execute(get);
                String jsonStr = EntityUtils.toString(response.getEntity());
                Log.d("Authentication", jsonStr);
                System.out.print(jsonStr);
                JSONObject jsonObj = new JSONObject(jsonStr);
                String[] profile =  jsonObj.optString("user").split(",");
                String[] userID = profile[8].split(":");
                Log.e("Authentication", userID[1].substring(1, userID[1].length()-1));
                setUserID(userID[1].substring(1, userID[1].length()-1));
                saveAuthor();
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void saveAuthor(){
        try {
            File file = new File(Constant.authorFile);
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file, true);
            Log.d("Fitbit", file.getAbsolutePath());
            String msg = "clientID=" + clientID + ">clientSecret=" + clientSecret + ">userID=" + userID + ">token=" + token + "\n";
            outputStream.write(msg.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Boolean loadAuthor(){
        try{
            File file = new File(Constant.authorFile);
            InputStream in = new FileInputStream(file);
            if (in != null) {
                InputStreamReader inputreader = new InputStreamReader(in);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = buffreader.readLine();
                if(line != null){

                //if(!line.isEmpty()) {

                    Log.d(TAG, line);
                    String[] author = line.split(">");
                    String[] user = author[2].split("=");
                    String[] accessToken = author[3].split("=");
                    setToken(accessToken[1]);
                    setUserID(user[1]);
                    Log.e("Fitbit", "----" + userID + "-----" + token);
                    return true;
                }
                else
                    return false;
            }
            in.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getUserID(){
        return userID;
    }

    public String formatDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public class uploadFood extends AsyncTask<String, Void, String> {
        private String foodName;
        private int mealTypeId;
        private int unitId;
        private float amount;
        private String date;

        public uploadFood(String foodName, int mealTypeId, int unitId, float amount, String date){
            this.foodName = foodName;
            this.mealTypeId = mealTypeId;
            this.unitId = unitId;
            this.amount = amount;
            this.date = date;
        }

        @Override
        protected String doInBackground(String... params) {
            if(loadAuthor()){
                try {
                    JSONObject log = new JSONObject();
                    log.put("foodName", foodName);
                    log.put("mealTypeId", mealTypeId);

                    String jsonString = log.toString();
                    HttpResponse response = null;
                    HttpClient client = new DefaultHttpClient();

                    HttpPost get = new HttpPost(String.format(loggingFoodRequest, userID, jsonString));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}