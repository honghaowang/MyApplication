package uml.swinlab.honghao.fitbitdemo.logDataBase;

/**
 * Created by Honghao on 4/11/2016.
 */
public class DBcontract {
    public static final int dbVersion = 1;
    public static final String dbFile = "fitbitDemo.db";
    public static final String dbName = "fitbitDemo";
    //table name
    public static final String FOOD_LOG_TABLE = "FoodLog";
    public static final String MOVES_TABLE = "MovesTable";
    public static final String AUTHOR_TABLE = "AuthorTable";
    //Attribute from fitbit
    public static final String logID = "logID";
    public static final String logDate = "logDate";
    public static final String calories = "calories";
    public static final String foodName = "name";
    public static final String mealTypeId = "mealTypeId";
    public static final String carbs = "carbs";
    public static final String fat = "fat";
    public static final String fiber = "fiber";
    public static final String protein = "protein";
    public static final String sodium = "sodium";

    //Attribute from Moves
    public static final String type = "locationType";
    public static final String lat = "lat";
    public static final String lon = "lon";
    public static final String start = "startTime";
    public static final String end = "endTime";
    public static final String duration = "duration";
    public static final String distance = "distance";
    public static final String steps = "steps";
    //public static final String calories = "calories";

    //Attribute from author
    public static final String appName = "APPname";
    public static final String token = "AccessToken";
    public static final String expires = "ExpiresIn";

    public static final String CREATE_FOODLOG_TABLE = "CREATE TABLE IF NOT EXISTS " + FOOD_LOG_TABLE + "("
            + logID + " INTEGER,"
            + logDate + " TEXT,"
            + foodName + " TEXT,"
            + mealTypeId + " TEXT,"
            + calories + " INTEGER,"
            + carbs + " INTEGER,"
            + fat + " INTEGER,"
            + fiber + " INTEGER,"
            + protein + " INTEGER,"
            + sodium + " INTEGER)";

    public static final String CREATE_MOVES_TABLE = "CREATE TABLE IF NOT EXISTS " + MOVES_TABLE + "("
            + type + " TEXT,"
            + lat + " TEXT,"
            + lon + " TEXT,"
            + start + " TEXT,"
            + end + " TEXT,"
            + duration + " TEXT,"
            + distance + " TEXT,"
            + steps + " TEXT,"
            + calories + " TEXT)";

    public static final String CREATE_AUTOR_TABLE = "CREATE TABLE IF NOT EXISTS " + AUTHOR_TABLE + "("
            + appName + " TEXT,"
            + token + " TEXT,"
            + expires + " TEXT)";

    public static final String DELETE_FOODLOG_TABLE = "DROP TABLE IF EXISTS " + FOOD_LOG_TABLE;
    public static final String DELETE_MOVES_TABLE = "DROP TABLE IF EXISTS " + MOVES_TABLE;
    public static final String searchFoodLog = "SELECT * FROM " + FOOD_LOG_TABLE;
    public static final String searchLocation = "SELECT * FROM " + MOVES_TABLE;

    public static final String searchLodID = "SELECT " + logID + " FROM " + FOOD_LOG_TABLE;
    public static final String searchMovesToken = "SELECT * FROM " + AUTHOR_TABLE;

}
