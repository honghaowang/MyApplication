package uml.swinlab.honghao.fitbitdemo;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by Honghao on 4/5/2016.
 */
public class FitbitActivity extends AppCompatActivity {
    private final String TAG = "FitbitActivity";
    private WebView view;
    private FitbitAPI fitbit = new FitbitAPI();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fitbit_activity);
        view = (WebView) findViewById(R.id.webview);
        view.setWebViewClient(new myWebClient());

        view.loadUrl(fitbit.AuthorizationURL);
    }

    public class myWebClient extends WebViewClient{
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, view.getUrl());
            if(view.getUrl().contains("http://swin06.cs.uml.edu/")) {
                String[] result = view.getUrl().split("&");
                String[] token = result[4].split("=");
                fitbit.setToken(token[1]);

                new Authentication().execute();
            }
        }
    }

    public class Authentication extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try{
                HttpResponse response = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(fitbit.profileRequest);
                get.setHeader("Authorization", "Bearer " + fitbit.token);
                response = client.execute(get);
                String jsonStr = EntityUtils.toString(response.getEntity());
                Log.d("Authentication", jsonStr);
                System.out.print(jsonStr);
                JSONObject jsonObj = new JSONObject(jsonStr);
                String[] profile =  jsonObj.optString("user").split(",");
                String[] userID = profile[8].split(":");
                Log.e("Authentication", userID[1].substring(1, userID[1].length()-1));
                fitbit.setUserID(userID[1].substring(1, userID[1].length()-1));
                fitbit.saveAuthor();

            } catch(IOException e){
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
