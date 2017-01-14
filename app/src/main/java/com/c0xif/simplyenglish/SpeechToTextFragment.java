package com.c0xif.simplyenglish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SpeechToTextFragment extends Fragment {

    TextView tv;

    //public static SpeechToTextFragment newInstance() {
    //    return new SpeechToTextFragment();
    //}

    public SpeechToTextFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_speech_to_text, container, false);
        tv = (TextView)v.findViewById(R.id.speech2textBox);
        return v;
    }

    public boolean addText(String s) {
        tv.setText(tv.getText() + s);
        return true;
    }

    public String sendWord() {
        String raw = (String) tv.getText();

        int i = raw.indexOf(' ');
        String word = raw.substring(0, i);
        String rest = raw.substring(i);
        tv.setText(rest.trim());
        return word.replaceAll(".","").trim();
    }



}

