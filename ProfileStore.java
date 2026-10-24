
package com.example.clipboardassist;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileStore {
    private static final String PREFS = "govinda_profiles";
    private static final String KEY = "profiles";

    public static JSONArray getProfiles(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        try {
            return new JSONArray(prefs.getString(KEY, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static void saveProfiles(Context context, JSONArray profiles) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY, profiles.toString())
                .apply();
    }

    public static JSONObject createProfile(String name, String phone, String email,
                                           String address, String city, String state,
                                           String idNumber) {
        JSONObject profile = new JSONObject();
        try {
            profile.put("name", name);
            profile.put("phone", phone);
            profile.put("email", email);
            profile.put("address", address);
            profile.put("city", city);
            profile.put("state", state);
            profile.put("idNumber", idNumber);
        } catch (Exception ignored) {
        }
        return profile;
    }

    public static void addProfile(Context context, JSONObject profile) {
        JSONArray profiles = getProfiles(context);
        profiles.put(profile);
        saveProfiles(context, profiles);
    }

    public static void deleteProfile(Context context, int index) {
        JSONArray old = getProfiles(context);
        JSONArray updated = new JSONArray();
        for (int i = 0; i < old.length(); i++) {
            if (i != index) updated.put(old.optJSONObject(i));
        }
        saveProfiles(context, updated);
    }

    public static void moveProfile(Context context, int from, int to) {
        JSONArray old = getProfiles(context);
        if (from < 0 || to < 0 || from >= old.length() || to >= old.length()) return;

        List<JSONObject> items = new ArrayList<>();
        for (int i = 0; i < old.length(); i++) {
            items.add(old.optJSONObject(i));
        }

        Collections.swap(items, from, to);
        JSONArray updated = new JSONArray();
        for (JSONObject item : items) updated.put(item);
        saveProfiles(context, updated);
    }

    public static void updateProfile(Context context, int index, JSONObject profile) {
        JSONArray old = getProfiles(context);
        if (index < 0 || index >= old.length()) return;

        JSONArray updated = new JSONArray();
        for (int i = 0; i < old.length(); i++) {
            updated.put(i == index ? profile : old.optJSONObject(i));
        }
        saveProfiles(context, updated);
    }
}
