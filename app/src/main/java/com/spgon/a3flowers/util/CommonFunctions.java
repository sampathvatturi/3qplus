package com.spgon.a3flowers.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;

import com.spgon.a3flowers.database.AppDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonFunctions {

    static AppDatabase db = null;
    private static NotificationManager notificationManager;

    public static AppDatabase getDBinstance(Context context) {
        if (db == null) {
            CommonFunctions.Log(context, "In db null");

            CommonFunctions.db = Room.databaseBuilder(context,
                            AppDatabase.class, "com.spgon.3-room-db").allowMainThreadQueries()
                    //    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return db;
    }
    public static void Log(Context context, String message) {

        if (Constants.DEBUG_FLAG) {
            Log.v("Code Debug", message);
        }
        String currentTimestam = CommonFunctions.getCurrentTime();

    }
    public static String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentTime = Calendar.getInstance().getTime();
        return dateFormat.format(currentTime);
    }

    public static String getStringShared(Context applicationContext, String key) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String retrievedValue = sharedPreferences.getString(key, "");
        return retrievedValue;
    }

    public static void setStringShared(Context applicationContext, String key,String value){
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value); // Replace "key" with your desired key and "value" with the data to be stored
        editor.apply(); // Commit the changes asynchronously
    }

    public static String extractVideoId(String youtubeUrl) {
        String videoId = youtubeUrl;
//        String pattern = "(?:(?:(?:https?:)?//)?(?:www\\.|m\\.)?(?:youtube\\.com|youtu\\.be)(?:/|/embed/|/v/|/watch\\?v=|/watch\\?.+&v=|/watch/.*[\\?&]v=))([a-zA-Z0-9_-]{11})";
//        Pattern compiledPattern = Pattern.compile(pattern);
//        Matcher matcher = compiledPattern.matcher(youtubeUrl);
//
//        if (matcher.find()) {
//            videoId = matcher.group(1);
//        }
        return videoId;
    }

    public static String getCurrent_date_in_YYYY_MM_DD_HH_MM_SS_format()
    {
        // Get the current date and time
        Date currentDate = new Date();

        // Create a SimpleDateFormat with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the current date and time as a string
        String formattedDate = dateFormat.format(currentDate);
        return formattedDate;
    }

    public static Date add_years_to_date(Date date,int years_to_add)
    {
        Calendar calendar = Calendar.getInstance();

        // Set the date you want to add one year to
        calendar.setTime(date); // Use your desired date here

        // Add one year
        calendar.add(Calendar.YEAR, years_to_add);

        // Get the resulting date
        Date newDate = calendar.getTime();
        return newDate;
    }

    public static String convertToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }
}
