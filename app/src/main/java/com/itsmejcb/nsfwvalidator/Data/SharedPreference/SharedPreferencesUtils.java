package com.itsmejcb.nsfwvalidator.Data.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import com.itsmejcb.nsfwvalidator.Data.Model.Image;
import com.itsmejcb.nsfwvalidator.Data.Model.SubList;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesUtils {
    private static final String PREFERENCES_NAME = "cache";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void saveArray(Context context, String key, ArrayList<SubList> jsonArray) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, jsonArray.toString()); // Serialize JSONArray to string
        editor.apply();
    }

    public static JSONArray loadArray(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String jsonArrayString = sharedPreferences.getString(key, defaultValue);

        try {
            return new JSONArray(jsonArrayString); // Deserialize string to JSONArray
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveImageArray(Context context, String key, ArrayList<Image> imageList) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String jsonArrayString = gson.toJson(imageList); // Serialize ArrayList to JSON string
        editor.putString(key, jsonArrayString);
        editor.apply();
    }
    public static ArrayList<Image> loadImageArray(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String jsonArrayString = sharedPreferences.getString(key, null);

        if (jsonArrayString != null) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Image>>() {}.getType();
                return gson.fromJson(jsonArrayString, listType); // Deserialize JSON string to ArrayList<Image>
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>(); // Return an empty ArrayList if data is not found or cannot be parsed
    }

    // public static String loadImageArray(Context context, String key, String defaultValue) {
    //     SharedPreferences sharedPreferences = getSharedPreferences(context);
    //     String jsonArrayString = sharedPreferences.getString(key, defaultValue);
    //
    //     if (jsonArrayString != null) {
    //         try {
    //             Gson gson = new Gson();
    //             Type listType = new TypeToken<ArrayList<Image>>() {}.getType();
    //             return gson.fromJson(jsonArrayString, listType); // Deserialize JSON string to ArrayList<Image>
    //         } catch (JsonSyntaxException e) {
    //             e.printStackTrace();
    //         }
    //     }
    //
    //     return String.valueOf(new ArrayList<>()); // Return an empty ArrayList if data is not found or cannot be parsed
    // }

    public static void removeString(Context context, String key) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        editor.remove(key);
        editor.apply();
    }

    public static void clearSharedPreferences(Context context) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        editor.clear();
        editor.apply();
    }

    public static boolean verifiedString(Context context, String string) {
        return !loadString(context, string, "").isEmpty();
    }

    public static void signOut(Context context) {
        SharedPreferences.Editor editor = getSharedPreferencesEditor(context);
        editor.clear();
        editor.apply();
    }
}
