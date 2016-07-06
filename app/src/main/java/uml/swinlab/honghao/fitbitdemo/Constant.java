package uml.swinlab.honghao.fitbitdemo;

import android.os.Environment;

import java.io.File;

/**
 * Created by Honghao on 4/11/2016.
 */
public class Constant {
    public static final String floderPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "swin.honghao.fitbit";
    public static final String authorFile = floderPath + File.separator + "author.txt";

    //Use at moves authorization
    public static final String CLIENT_ID = "ly0YbQ9pzd9ntGsVi0GZMB03rHwy3df5";
    public static final String clientSecret = "Q_ajEqdN38sfrocNlgh6YC_4i3Eb4j455HA3him988Pn0txG72K7woP8TU8uzPU2";
    public static final String REDIRECT_URI = "http://swin06.cs.uml.edu/processdata/";
    public static final int REQUEST_AUTHORIZE = 1;
    public static final String MOVES_SCOPES = "location activity";

    //
    public static final String POST_FILE_URL = "http://swin06.cs.uml.edu/processdata/autotest";
}
