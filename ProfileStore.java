package com.example.clipboardassist;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ProfileStore {
    public static final String PREFS = "clipboard_assist_data";
    public static final String PROFILES = "profiles";
    public static final String GENERAL = "general";
    public static final String[] PILGRIM_FIELDS = {"Full name", "Age", "Gender", "ID proof type", "ID proof number", "Mobile number"};
    public static final String[] GENERAL_FIELDS = {"Email address", "City", "State", "Country", "Address", "PIN code", "Mobile number"};
    private ProfileStore() {}
    public static SharedPreferences prefs(Context c) { return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE); }
    public static JSONArray profiles(Context c) {
        try { return new JSONArray(prefs(c).getString(PROFILES, "[]")); }
        catch (Exception e) { return new JSONArray(); }
    }
    public static JSONObject general(Context c) {
        try { return new JSONObject(prefs(c).getString(GENERAL, "{}")); }
        catch (Exception e) { return new JSONObject(); }
    }
    public static String value(JSONObject o, String key) { return o == null ? "" : o.optString(key, ""); }
    public static void saveProfiles(Context c, JSONArray arr) { prefs(c).edit().putString(PROFILES, arr.toString()).apply(); }
    public static void saveGeneral(Context c, JSONObject obj) { prefs(c).edit().putString(GENERAL, obj.toString()).apply(); }
}
