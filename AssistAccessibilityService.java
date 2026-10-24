package com.example.clipboardassist;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

public class AssistAccessibilityService extends AccessibilityService {
    private WindowManager wm; private Button bubble; private WindowManager.LayoutParams params;
    @Override public void onServiceConnected(){super.onServiceConnected();wm=(WindowManager)getSystemService(WINDOW_SERVICE);showBubble();}
    @Override public void onAccessibilityEvent(AccessibilityEvent e){if(bubble==null && Build.VERSION.SDK_INT>=23 && Settings.canDrawOverlays(this))showBubble();}
    @Override public void onInterrupt(){}
    @Override public void onDestroy(){removeBubble();super.onDestroy();}
    private void showBubble(){if(bubble!=null||Build.VERSION.SDK_INT<23||!Settings.canDrawOverlays(this))return;try{bubble=new Button(this);bubble.setText("＋");bubble.setTextSize(23);bubble.setTextColor(Color.WHITE);bubble.setTypeface(null,Typeface.BOLD);bubble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF6A329F));bubble.setPadding(0,0,0,0);params=new WindowManager.LayoutParams(dp(54),dp(54),WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);params.gravity=Gravity.TOP|Gravity.RIGHT;params.x=dp(12);params.y=dp(180);wm.addView(bubble,params);bubble.setOnClickListener(v->showProfiles());bubble.setOnTouchListener(new android.view.View.OnTouchListener(){float sx,sy;int ox,oy;public boolean onTouch(android.view.View v,android.view.MotionEvent e){if(e.getAction()==0){sx=e.getRawX();sy=e.getRawY();ox=params.x;oy=params.y;return false;}if(e.getAction()==2){params.x=Math.max(0,ox+(int)(sx-e.getRawX()));params.y=Math.max(0,oy+(int)(e.getRawY()-sy));try{wm.updateViewLayout(bubble,params);}catch(Exception ignored){}return true;}return false;}});}catch(Exception ignored){bubble=null;}}
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private void removeBubble(){try{if(bubble!=null)wm.removeView(bubble);}catch(Exception ignored){}bubble=null;}
    private void showProfiles(){JSONArray arr=ProfileStore.profiles(this);if(arr.length()==0){Toast.makeText(this,"Open Clipboard Assist and add a pilgrim first",Toast.LENGTH_LONG).show();return;}String[] names=new String[arr.length()+1];names[0]="General Details";for(int i=0;i<arr.length();i++)names[i+1]=ProfileStore.value(arr.optJSONObject(i),"Full name");AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Choose saved details").setItems(names,(d,which)->{if(which==0)showValues(ProfileStore.general(this),ProfileStore.GENERAL_FIELDS);else{JSONObject p=arr.optJSONObject(which-1);showValues(p,ProfileStore.PILGRIM_FIELDS);}}).setNegativeButton("Close",null).create(); if(dialog.getWindow()!=null)dialog.getWindow().setType(Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE); dialog.show();}
    private void showValues(JSONObject data,String[] fields){LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(10),dp(8),dp(10),dp(8));ScrollView sc=new ScrollView(this);sc.addView(box);for(String key:fields){String value=ProfileStore.value(data,key);if(value.isEmpty())continue;Button b=new Button(this);b.setAllCaps(false);b.setText(key+"  ·  "+value);box.addView(b);b.setOnClickListener(v->{boolean filled=tryFill(value);if(!filled){ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newPlainText(key,value));Toast.makeText(this,"Copied "+key+". Tap the field and paste.",Toast.LENGTH_SHORT).show();} });}AlertDialog dialog=new AlertDialog.Builder(this).setTitle("Tap a value to insert or copy").setView(sc).setNegativeButton("Close",null).create(); if(dialog.getWindow()!=null)dialog.getWindow().setType(Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE); dialog.show();}
    private boolean tryFill(String value){try{AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return false;AccessibilityNodeInfo focus=root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);if(focus==null)return false;android.os.Bundle args=new android.os.Bundle();args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,value);boolean ok=focus.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,args);if(ok)Toast.makeText(this,"Inserted into focused field",Toast.LENGTH_SHORT).show();return ok;}catch(Exception e){return false;}}
}
