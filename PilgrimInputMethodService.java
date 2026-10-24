package com.example.clipboardassist;

import android.inputmethodservice.InputMethodService;
import android.graphics.Color;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.view.inputmethod.InputConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class PilgrimInputMethodService extends InputMethodService {
    @Override public View onCreateInputView(){return buildKeyboard();}
    @Override public View onCreateCandidatesView(){return buildKeyboard();}
    private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private View buildKeyboard(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(6),dp(6),dp(6),dp(6));root.setBackgroundColor(0xFFF0EAF7);
        TextView title=new TextView(this);title.setText("Clipboard Assist · Quick Insert");title.setTextColor(0xFF542B7C);title.setTextSize(14);title.setGravity(Gravity.CENTER_VERTICAL);root.addView(title);
        HorizontalScrollView hs=new HorizontalScrollView(this);LinearLayout profilesRow=new LinearLayout(this);profilesRow.setOrientation(LinearLayout.HORIZONTAL);hs.addView(profilesRow);root.addView(hs);
        JSONArray profiles=ProfileStore.profiles(this);JSONObject general=ProfileStore.general(this);
        addProfileChip(profilesRow,"General",general,ProfileStore.GENERAL_FIELDS);
        for(int i=0;i<profiles.length();i++){JSONObject p=profiles.optJSONObject(i);if(p!=null)addProfileChip(profilesRow,ProfileStore.value(p,"Full name").isEmpty()?"Pilgrim "+(i+1):ProfileStore.value(p,"Full name"),p,ProfileStore.PILGRIM_FIELDS);}
        TextView hint=new TextView(this);hint.setText("Tap a saved value below to insert it into the focused field.");hint.setTextSize(12);hint.setTextColor(0xFF5F5965);hint.setPadding(0,dp(6),0,dp(4));root.addView(hint);
        LinearLayout values=new LinearLayout(this);values.setOrientation(LinearLayout.VERTICAL);root.addView(values);
        if(general.length()>0)addValues(values,general,ProfileStore.GENERAL_FIELDS);
        if(profiles.length()>0){JSONObject p=profiles.optJSONObject(0);if(p!=null)addValues(values,p,ProfileStore.PILGRIM_FIELDS);}
        Button close=new Button(this);close.setText("Hide keyboard");close.setAllCaps(false);close.setOnClickListener(v->requestHideSelf(0));root.addView(close);
        return root;
    }
    private void addProfileChip(LinearLayout row,String name,JSONObject p,String[] fields){Button b=new Button(this);b.setText(name);b.setAllCaps(false);b.setTextSize(12);row.addView(b);b.setOnClickListener(v->{LinearLayout parent=(LinearLayout)((View)row.getParent()).getParent();LinearLayout values=(LinearLayout)parent.getChildAt(3);values.removeAllViews();addValues(values,p,fields);});}
    private void addValues(LinearLayout box,JSONObject p,String[] fields){for(String field:fields){String value=ProfileStore.value(p,field);if(value.isEmpty())continue;Button b=new Button(this);b.setText(field+": "+value);b.setTextSize(12);b.setAllCaps(false);b.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.WHITE));box.addView(b);b.setOnClickListener(v->{InputConnection ic=getCurrentInputConnection();if(ic!=null)ic.commitText(value,1);});}}
}
