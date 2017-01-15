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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL;

public class TextToSimpleFragment extends Fragment {
    public final String TAG = "TTSFrag";

    private LinearLayout LL;
    public Button CurrentButton = null;
    private SharedPreferences UserPreferences;
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
        LL = (LinearLayout) view.findViewById(R.id.simple_text_space);

        initUserPref();
        return view;
    }

    public void receiveWord(String word) {
        String text = getUserPref(word);

        final Button textButton = new Button(this.getContext());
        final TextToSimpleFragment ttsf = this;
        textButton.setText(text);

        textButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CurrentButton != null && CurrentButton != textButton)
                    setPreferences(CurrentButton);

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

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        LL.addView(textButton, lp);

        if (LL != null) {
            LL.addView(textButton, lp);
        }

    }

    public void initUserPref() {
        if (UserPreferences == null)
            UserPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (UserPreferences == null)
            Log.d(TAG, "Error Loading User Preferences");
    }

    public void closeUserPref() {
    }

    public String getUserPref(String key) {
        if (UserPreferences == null) {
            Log.d(TAG, "You must initialize the User Preferences first");
            return key;
        }

        return UserPreferences.getString(key, key);
    }

    public void storeUserPref(String key, String value) {
        SharedPreferences.Editor editor = UserPreferences.edit();
        if (editor != null) {
            editor.putString(key, value);
            editor.commit();
        } else {
            Log.d(TAG, "Error when editing User Preferences");
        }
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

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) == bKey.getText())
                break;

            storeUserPref(values.get(i), (String)bKey.getText());
        }
    }

    private void clear() {
        LL.removeAllViewsInLayout();
    }
}