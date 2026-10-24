package com.example.clipboardassist;

import android.app.Activity;
import android.os.Bundle;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private EditText name, age, gender, idProof, idNumber, email, city, state, country, pin;

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(24), dp(20), dp(24));
        page.setBackgroundColor(0xFFF7F8FC);
        scroll.addView(page);

        TextView title = new TextView(this);
        title.setText("Clipboard Assist");
        title.setTextSize(26);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF183B66);
        page.addView(title);

        TextView intro = new TextView(this);
        intro.setText("Save pilgrim details and copy them quickly when filling forms.");
        intro.setTextSize(14);
        intro.setPadding(0, dp(8), 0, dp(18));
        page.addView(intro);

        TextView section = new TextView(this);
        section.setText("Pilgrim details");
        section.setTextSize(19);
        section.setTypeface(null, Typeface.BOLD);
        page.addView(section);

        name = field(page, "Full name");
        age = field(page, "Age");
        gender = field(page, "Gender");
        idProof = field(page, "ID proof type");
        idNumber = field(page, "ID proof number");

        TextView general = new TextView(this);
        general.setText("General details");
        general.setTextSize(19);
        general.setTypeface(null, Typeface.BOLD);
        general.setPadding(0, dp(18), 0, 0);
        page.addView(general);

        email = field(page, "Email");
        city = field(page, "City");
        state = field(page, "State");
        country = field(page, "Country");
        pin = field(page, "PIN code");

        Button copy = new Button(this);
        copy.setText("Copy all details");
        page.addView(copy, new LinearLayout.LayoutParams(-1, -2));
        copy.setOnClickListener(v -> {
            String text = "Name: " + value(name) + "\nAge: " + value(age) + "\nGender: " + value(gender)
                + "\nID proof: " + value(idProof) + "\nID number: " + value(idNumber)
                + "\nEmail: " + value(email) + "\nCity: " + value(city) + "\nState: " + value(state)
                + "\nCountry: " + value(country) + "\nPIN: " + value(pin);
            ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("Pilgrim details", text));
            Toast.makeText(this, "Details copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        TextView note = new TextView(this);
        note.setText("Starter version: details are not saved after closing the app yet. This is a starting template, not an exact clone of the original.");
        note.setTextSize(12);
        note.setPadding(0, dp(12), 0, 0);
        page.addView(note);

        setContentView(scroll);
    }

    private EditText field(LinearLayout page, String hint) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setHint(hint);
        e.setTextSize(16);
        e.setPadding(dp(12), dp(8), dp(12), dp(8));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(7);
        page.addView(e, lp);
        return e;
    }

    private String value(EditText e) { return e.getText().toString().trim(); }
}
