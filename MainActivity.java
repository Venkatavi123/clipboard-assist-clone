package com.example.clipboardassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private JSONArray profiles;
    private LinearLayout list;
    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    @Override public void onCreate(Bundle b) { super.onCreate(b); render(); }
    @Override protected void onResume() { super.onResume(); if (list != null) render(); }
    private void render() {
        profiles = ProfileStore.profiles(this);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(1); page.setPadding(dp(18),dp(22),dp(18),dp(30)); page.setBackgroundColor(0xFFF4F1F8); scroll.addView(page);
        TextView title = new TextView(this); title.setText("Clipboard Assist"); title.setTextSize(27); title.setTypeface(null,Typeface.BOLD); title.setTextColor(0xFF542B7C); page.addView(title);
        TextView sub = new TextView(this); sub.setText("Saved Pilgrims · Autofill · Floating Copy · Quick Insert"); sub.setTextSize(13); sub.setTextColor(0xFF5F5965); sub.setPadding(0,dp(5),0,dp(14)); page.addView(sub);
        addButton(page,"＋ Add pilgrim",()->editProfile(-1));
        addButton(page,"General Details (email, city, state, address)",this::editGeneral);
        addButton(page,"Copy all saved details",this::copyAll);
        TextView section = new TextView(this); section.setText("SETUP — enable only the features you want"); section.setTypeface(null,Typeface.BOLD); section.setTextSize(14); section.setTextColor(0xFF542B7C); section.setPadding(0,dp(16),0,dp(7)); page.addView(section);
        addButton(page,"Enable floating copy button",()->{ if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) { Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())); startActivity(i); } else startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)); Toast.makeText(this,"In Accessibility settings, enable Clipboard Assist floating button.",Toast.LENGTH_LONG).show(); });
        addButton(page,"Enable Autofill service",()->{ if(Build.VERSION.SDK_INT>=26) startActivity(new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE, Uri.parse("package:"+getPackageName()))); });
        addButton(page,"Choose Quick Insert keyboard",()->{ ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker(); Toast.makeText(this,"If not listed, enable Clipboard Assist Quick Insert in keyboard settings first.",Toast.LENGTH_LONG).show(); });
        TextView note = new TextView(this); note.setText("How to use: save profiles, enable Android Autofill in Settings, or select the Quick Insert keyboard. The floating button opens your saved details while another app is on screen. You choose what to insert; it never submits a booking for you."); note.setTextSize(13); note.setTextColor(0xFF57515E); note.setPadding(0,dp(10),0,dp(14)); page.addView(note);
        TextView heading = new TextView(this); heading.setText("SAVED PILGRIMS (drag-free reorder with ↑ / ↓)"); heading.setTypeface(null,Typeface.BOLD); heading.setTextSize(14); heading.setTextColor(0xFF542B7C); heading.setPadding(0,dp(6),0,dp(8)); page.addView(heading);
        list = new LinearLayout(this); list.setOrientation(1); page.addView(list);
        if(profiles.length()==0){ TextView empty=new TextView(this); empty.setText("No pilgrims saved yet. Tap Add pilgrim to create your first profile."); empty.setTextSize(15); empty.setPadding(dp(12),dp(20),dp(12),dp(20)); list.addView(empty); }
        for(int i=0;i<profiles.length();i++) { final int index=i; JSONObject p=profiles.optJSONObject(i); if(p==null)continue;
            LinearLayout card=new LinearLayout(this); card.setOrientation(1); card.setPadding(dp(14),dp(12),dp(14),dp(12)); card.setBackgroundColor(Color.WHITE); LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.bottomMargin=dp(10); list.addView(card,cp);
            TextView name=new TextView(this); name.setText((i+1)+". "+ProfileStore.value(p,"Full name")); name.setTextSize(18); name.setTypeface(null,Typeface.BOLD); name.setTextColor(0xFF30243B); card.addView(name);
            TextView detail=new TextView(this); detail.setText("Age: "+ProfileStore.value(p,"Age")+"   ·   "+ProfileStore.value(p,"Gender")+"\nID: "+ProfileStore.value(p,"ID proof type")+" "+ProfileStore.value(p,"ID proof number")); detail.setTextSize(13); detail.setTextColor(0xFF625B68); detail.setPadding(0,dp(5),0,dp(8)); card.addView(detail);
            LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); card.addView(row);
            small(row,"Copy",()->copy(profileText(p))); small(row,"Edit",()->editProfile(index)); small(row,"↑",()->move(index,-1)); small(row,"↓",()->move(index,1)); small(row,"Delete",()->delete(index));
            for(String key:ProfileStore.PILGRIM_FIELDS){ String val=ProfileStore.value(p,key); if(val.isEmpty())continue; LinearLayout r=new LinearLayout(this); r.setGravity(Gravity.CENTER_VERTICAL); card.addView(r); TextView t=new TextView(this); t.setText(key+": "+val); t.setTextSize(13); t.setTextColor(0xFF4B4550); r.addView(t,new LinearLayout.LayoutParams(0,-2,1)); Button c=new Button(this); c.setText("Copy"); c.setTextSize(10); c.setAllCaps(false); r.addView(c); c.setOnClickListener(v->copy(val)); }
        }
        setContentView(scroll);
    }
    private void addButton(LinearLayout p,String text,Runnable action){ Button b=new Button(this); b.setText(text); b.setAllCaps(false); b.setTextSize(14); LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=dp(5); p.addView(b,lp); b.setOnClickListener(v->action.run()); }
    private void small(LinearLayout row,String text,Runnable action){ Button b=new Button(this); b.setText(text); b.setAllCaps(false); b.setTextSize(10); b.setPadding(dp(1),0,dp(1),0); b.setMinWidth(0); row.addView(b,new LinearLayout.LayoutParams(0,dp(42),1)); b.setOnClickListener(v->action.run()); }
    private String profileText(JSONObject p){StringBuilder s=new StringBuilder();for(String k:ProfileStore.PILGRIM_FIELDS){String v=ProfileStore.value(p,k);if(!v.isEmpty())s.append(k).append(": ").append(v).append('\n');}return s.toString().trim();}
    private void copyAll(){StringBuilder s=new StringBuilder();JSONObject g=ProfileStore.general(this);for(String k:ProfileStore.GENERAL_FIELDS){String v=ProfileStore.value(g,k);if(!v.isEmpty())s.append(k).append(": ").append(v).append('\n');}for(int i=0;i<profiles.length();i++){JSONObject p=profiles.optJSONObject(i);if(p!=null)s.append("\nPILGRIM ").append(i+1).append("\n").append(profileText(p)).append('\n');}copy(s.toString().trim());}
    private void copy(String s){if(s==null||s.trim().isEmpty()){Toast.makeText(this,"Nothing to copy",Toast.LENGTH_SHORT).show();return;}ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newPlainText("Clipboard Assist",s));Toast.makeText(this,"Copied. Paste into the booking form.",Toast.LENGTH_SHORT).show();}
    private void move(int i,int d){int j=i+d;if(j<0||j>=profiles.length())return;JSONObject a=profiles.optJSONObject(i),b=profiles.optJSONObject(j);profiles.put(j,a);profiles.put(i,b);ProfileStore.saveProfiles(this,profiles);render();}
    private void delete(int i){new AlertDialog.Builder(this).setTitle("Delete pilgrim?").setMessage("This removes the saved profile from this phone.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{JSONArray n=new JSONArray();for(int x=0;x<profiles.length();x++)if(x!=i)n.put(profiles.optJSONObject(x));ProfileStore.saveProfiles(this,n);render();}).show();}
    private void editProfile(int index){JSONObject old=index>=0?profiles.optJSONObject(index):new JSONObject();String[] fields=ProfileStore.PILGRIM_FIELDS;showForm(index<0?"Add pilgrim":"Edit pilgrim",fields,old,obj->{try{if(index>=0)profiles.put(index,obj);else profiles.put(obj);ProfileStore.saveProfiles(this,profiles);render();}catch(Exception ignored){}});}
    private void editGeneral(){showForm("General Details",ProfileStore.GENERAL_FIELDS,ProfileStore.general(this),obj->{ProfileStore.saveGeneral(this,obj);render();});}
    interface SaveAction{void save(JSONObject o);}
    private void showForm(String title,String[] fields,JSONObject old,SaveAction save){ScrollView sc=new ScrollView(this);LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(dp(16),dp(8),dp(16),dp(8));sc.addView(form);EditText[] edits=new EditText[fields.length];for(int i=0;i<fields.length;i++){EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(15);e.setHint(fields[i]);e.setText(ProfileStore.value(old,fields[i]));if(fields[i].equals("Age")||fields[i].contains("number")||fields[i].equals("PIN code"))e.setInputType(2);form.addView(e);edits[i]=e;}new AlertDialog.Builder(this).setTitle(title).setView(sc).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{JSONObject o=new JSONObject();try{for(int i=0;i<fields.length;i++)o.put(fields[i],edits[i].getText().toString().trim());save.save(o);}catch(Exception ignored){}}).show();}
}
