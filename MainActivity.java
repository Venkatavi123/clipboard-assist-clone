package com.example.clipboardassist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
  private final String[] labels={"Full name","Age","Gender","ID proof type","ID proof number","Mobile number","Email","Address","City","State","Country","PIN code"};
  private JSONArray profiles=new JSONArray(); private LinearLayout list; private SharedPreferences prefs;
  private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
  @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("govinda_profiles",MODE_PRIVATE); load(); render();}
  private void load(){try{profiles=new JSONArray(prefs.getString("profiles","[]"));}catch(Exception e){profiles=new JSONArray();}}
  private void save(){prefs.edit().putString("profiles",profiles.toString()).apply();}
  private void render(){
    ScrollView scroll=new ScrollView(this); LinearLayout page=new LinearLayout(this); page.setOrientation(1); page.setPadding(dp(18),dp(24),dp(18),dp(28)); page.setBackgroundColor(0xFFF6F5F1); scroll.addView(page);
    TextView title=new TextView(this); title.setText("🙏 GOVINDA"); title.setTextSize(28); title.setTypeface(null,Typeface.BOLD); title.setTextColor(0xFF5A3514); page.addView(title);
    TextView subtitle=new TextView(this); subtitle.setText("Pilgrim profiles • Quick copy • Offline storage"); subtitle.setTextSize(14); subtitle.setTextColor(0xFF65584C); subtitle.setPadding(0,dp(5),0,dp(16)); page.addView(subtitle);
    Button add=button("＋ Add pilgrim profile"); page.addView(add); add.setOnClickListener(v->editProfile(-1));
    Button all=button("Copy all profiles"); page.addView(all); all.setOnClickListener(v->copyAll());
    TextView hint=new TextView(this); hint.setText("Saved on this phone only. Copy a field or profile, then paste it manually into the booking form."); hint.setTextSize(13); hint.setTextColor(0xFF625D55); hint.setPadding(0,dp(12),0,dp(14)); page.addView(hint);
    list=new LinearLayout(this); list.setOrientation(1); page.addView(list);
    if(profiles.length()==0){TextView empty=new TextView(this);empty.setText("No profiles yet. Tap ‘Add pilgrim profile’ to begin.");empty.setTextSize(15);empty.setPadding(dp(12),dp(18),dp(12),dp(18));list.addView(empty);}
    for(int i=0;i<profiles.length();i++){final int index=i; JSONObject p=profiles.optJSONObject(i); if(p==null)continue;
      LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(14),dp(12),dp(14),dp(12));card.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.bottomMargin=dp(12);list.addView(card,cp);
      TextView name=new TextView(this);name.setText((i+1)+". "+get(p,"Full name","Unnamed pilgrim"));name.setTextSize(18);name.setTypeface(null,Typeface.BOLD);name.setTextColor(0xFF49301D);card.addView(name);
      TextView detail=new TextView(this);detail.setText("Age: "+get(p,"Age","")+"   •   ID: "+get(p,"ID proof type","")+" "+get(p,"ID proof number",""));detail.setTextSize(13);detail.setTextColor(0xFF5C5751);detail.setPadding(0,dp(5),0,dp(8));card.addView(detail);
      LinearLayout row=new LinearLayout(this);row.setOrientation(0);row.setGravity(Gravity.CENTER_VERTICAL);card.addView(row);
      small(row,"Copy all",()->copy(profileText(p)));small(row,"Edit",()->editProfile(index));small(row,"↑",()->move(index,-1));small(row,"↓",()->move(index,1));small(row,"Delete",()->delete(index));
      LinearLayout fields=new LinearLayout(this);fields.setOrientation(1);fields.setPadding(0,dp(5),0,0);card.addView(fields);
      for(String key:labels){String val=get(p,key,"");if(val.trim().isEmpty())continue; LinearLayout fr=new LinearLayout(this);fr.setOrientation(0);fr.setGravity(Gravity.CENTER_VERTICAL);fields.addView(fr);TextView t=new TextView(this);t.setText(key+": "+val);t.setTextSize(13);t.setTextColor(0xFF4B4945);fr.addView(t,new LinearLayout.LayoutParams(0,-2,1));Button c=new Button(this);c.setText("Copy");c.setTextSize(10);c.setMinHeight(dp(32));c.setMinimumHeight(dp(32));c.setPadding(dp(3),0,dp(3),0);fr.addView(c);c.setOnClickListener(v->copy(val));}
    }
    setContentView(scroll);
  }
  private Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextSize(15);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(7);b.setLayoutParams(p);return b;}
  private void small(LinearLayout row,String text,Runnable action){Button b=new Button(this);b.setText(text);b.setAllCaps(false);b.setTextSize(10);b.setPadding(dp(2),0,dp(2),0);b.setMinWidth(0);b.setMinimumWidth(0);row.addView(b,new LinearLayout.LayoutParams(0,dp(42),1));b.setOnClickListener(v->action.run());}
  private String get(JSONObject p,String k,String d){return p==null?d:p.optString(k,d);}
  private String profileText(JSONObject p){StringBuilder s=new StringBuilder();for(String k:labels){String v=get(p,k,"");if(!v.trim().isEmpty())s.append(k).append(": ").append(v).append('\n');}return s.toString().trim();}
  private void copyAll(){StringBuilder s=new StringBuilder();for(int i=0;i<profiles.length();i++){JSONObject p=profiles.optJSONObject(i);if(p!=null)s.append("PROFILE ").append(i+1).append('\n').append(profileText(p)).append("\n\n");}copy(s.toString().trim());}
  private void copy(String text){if(text==null||text.trim().isEmpty()){Toast.makeText(this,"Nothing to copy",Toast.LENGTH_SHORT).show();return;}ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);cm.setPrimaryClip(ClipData.newPlainText("Govinda details",text));Toast.makeText(this,"Copied to clipboard — paste manually",Toast.LENGTH_SHORT).show();}
  private void move(int i,int d){int j=i+d;if(j<0||j>=profiles.length())return;JSONObject a=profiles.optJSONObject(i),b=profiles.optJSONObject(j);profiles.put(j,a);profiles.put(i,b);save();render();}
  private void delete(int i){new AlertDialog.Builder(this).setTitle("Delete profile?").setMessage("This profile will be removed from this phone.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{JSONArray n=new JSONArray();for(int x=0;x<profiles.length();x++)if(x!=i)n.put(profiles.optJSONObject(x));profiles=n;save();render();}).show();}
  private void editProfile(int index){JSONObject old=index>=0?profiles.optJSONObject(index):new JSONObject();LinearLayout form=new LinearLayout(this);form.setOrientation(1);form.setPadding(dp(18),dp(8),dp(18),dp(4));ScrollView sv=new ScrollView(this);sv.addView(form);EditText[] inputs=new EditText[labels.length];for(int i=0;i<labels.length;i++){EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(15);e.setHint(labels[i]);e.setText(get(old,labels[i],""));form.addView(e);inputs[i]=e;}
    new AlertDialog.Builder(this).setTitle(index<0?"New pilgrim profile":"Edit pilgrim profile").setView(sv).setNegativeButton("Cancel",null).setPositiveButton("Save",(dialog,which)->{JSONObject p=new JSONObject();try{for(int i=0;i<labels.length;i++)p.put(labels[i],inputs[i].getText().toString().trim());if(index>=0)profiles.put(index,p);else profiles.put(p);save();render();}catch(Exception e){Toast.makeText(this,"Could not save profile",Toast.LENGTH_SHORT).show();}}).show();}
}
