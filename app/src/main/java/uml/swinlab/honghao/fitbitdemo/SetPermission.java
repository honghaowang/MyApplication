package uml.swinlab.honghao.fitbitdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Honghao on 4/1/2016.
 * You have to set
 * compileSdkVersion 23
 * buildToolsVersion "23.0.3"
 * targetSdkVersion 23
 */
public class SetPermission {
    private Context context;
    private Activity activity;
    private ArrayList<String> permissionList = new ArrayList<String>();
    private final int PERMISSION_STATE = 123;
    private final String TAG = "SetPermission";

    public SetPermission(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void addPermission(String permission){
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(activity, new String[]{permission}, PERMISSION_STATE);
            }
        }
    }

    public void addPermissionList(ArrayList<String> List){
        for(int i=0; i<List.size(); i++){
            if(ContextCompat.checkSelfPermission(context, List.get(i)) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, List.get(i))) {
                    Log.e(TAG, "miss READ_PHONE_STATE");
                    permissionList.add("android.permission.READ_PHONE_STATE");
                }
            }
        }

        ActivityCompat.requestPermissions(activity,List.toArray(new String[permissionList.size()]), PERMISSION_STATE);
    }

    public void addPermissionList(String permission){
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
                permissionList.add(permission);
            }
        }
    }

    public void getPermission(){
        if(!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), PERMISSION_STATE);
        }
    }
}