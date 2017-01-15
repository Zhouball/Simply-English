package com.c0xif.simplyenglish;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL;

public class TextToSimpleFragment extends Fragment {
    public final String TAG = "TTSFrag";

    private FlexboxLayout FL;
    public Button CurrentButton = null;
    public HashMap<Button, ArrayList<String>> LocalThesaurus = new HashMap<>();

    public static TextToSimpleFragment newInstance() {
        return new TextToSimpleFragment();
    }

    public TextToSimpleFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_to_simple, container, false);
        FL = (FlexboxLayout) view.findViewById(R.id.simple_text_space);

        return view;
    }

    public void receiveWord(String word) {
        String text = getUserPref(word);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Button item = (Button) inflater.inflate(R.layout.flexbox_item, FL, false);
        final Button textButton = (Button) item.findViewById(android.R.id.button1);
        /*
        TextView textView = (TextView) item.findViewById(android.R.id.text1);
        textView.setText(getActivity().getString(R.string.item_default, flexboxLayout.getChildCount()));
        FlexboxLayout.LayoutParams layoutParams = (FlexboxLayout.LayoutParams) item.getLayoutParams();
        item.setLayoutParams(layoutParams);
        */
        final TextToSimpleFragment ttsf = this;
        textButton.setText(text);

        textButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ttsf.CurrentButton != null && ttsf.CurrentButton != textButton)
                    ttsf.setPreferences(ttsf.CurrentButton);

                ttsf.CurrentButton = textButton;

                ArrayList<String> synonyms = ttsf.LocalThesaurus.get(textButton);

                if (synonyms != null) {
                    textButton.setText(ttsf.getWordFromSynonymList(synonyms, (String) textButton.getText()));
                } else {
                    ArrayList<String> toInsert = new ArrayList<String>();
                    toInsert.add((String)textButton.getText());
                    // TODO:  Fetch the thesaurus API info and set mSynonyms.
                    toInsert.add("Zhouball");
                    toInsert.add("Zhou");
                    ttsf.LocalThesaurus.put(textButton, toInsert);
                    synonyms = toInsert;
                    textButton.setText(ttsf.getWordFromSynonymList(synonyms, (String) textButton.getText()));
                }
            }
        });

        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams) item.getLayoutParams();
        item.setLayoutParams(lp);

        FL.addView(item);

    }

    public void clearHist() {
        SharedPreferences userPref = this.getContext().getSharedPreferences("com.c0xif.simplyenglish", Context.MODE_PRIVATE);
        userPref.edit().clear();
    }

    public String getUserPref(String key) {
        SharedPreferences userPref = this.getContext().getSharedPreferences("com.c0xif.simplyenglish", Context.MODE_PRIVATE);

        if (userPref == null) {
            Log.d(TAG, "You must initialize the User Preferences first");
            return key;
        }
        Log.d(TAG, key + " IS MAPPED TO " + userPref.getString(key, key));
        return userPref.getString(key, key);
    }

    public void storeUserPref(String key, String value) {
        SharedPreferences userPref = this.getContext().getSharedPreferences("com.c0xif.simplyenglish", Context.MODE_PRIVATE);
        if (userPref == null) {
            Log.d(TAG, "You must initialize the User Preferences first 2");
            return;
        }
        Log.d(TAG, "MAPPED " + key + " TO " + value);

        if(userPref.edit().putString(key, value).commit())
            Log.d(TAG, "SUCCESS!");

    }

    private String getWordFromSynonymList(ArrayList<String> synonyms, String currentWord) {
        if (synonyms == null) {
            Log.d(TAG, "Error, Synonym List is null");
            return currentWord;
        }

        for(int i = 0; i < synonyms.size(); i++) {
            if(synonyms.get(i) == currentWord)
                return synonyms.get((i + 1) % synonyms.size());
        }

        return currentWord;
    }

    private void setPreferences(Button bKey) {
        ArrayList<String> values = LocalThesaurus.get(bKey);

        if (values == null) {
            Log.d(TAG, "Error, Key does not exist");
            return;
        }

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == bKey.getText())
                break;

            storeUserPref(values.get(i), (String)bKey.getText());
        }
    }

    private void clear() {
        FL.removeAllViewsInLayout();
    }
}