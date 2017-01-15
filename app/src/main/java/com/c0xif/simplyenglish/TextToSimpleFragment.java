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
import android.widget.TextView;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

public class TextToSimpleFragment extends Fragment {
    public final String TAG = "TTSFrag";

    private int currentRow = 0;
    private int currentRowWidth = 0;
    private LinearLayout LL;
    private GridLayout GL[];
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
        GL[0] = (GridLayout) view.findViewById(R.id.row_1);
        GL[1] = (GridLayout) view.findViewById(R.id.row_2);
        GL[2] = (GridLayout) view.findViewById(R.id.row_3);
        GL[3] = (GridLayout) view.findViewById(R.id.row_4);
        GL[4] = (GridLayout) view.findViewById(R.id.row_5);
        GL[5] = (GridLayout) view.findViewById(R.id.row_6);

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

        GridLayout.LayoutParams lp = new GridLayout().LayoutParams(GridLayout.LayoutParams.WRAP_CONTENT, GridLayout.LayoutParams.WRAP_CONTENT);

        if (LL != null) {
            if (textButton.getX() + textButton.getWidth() + currentRowWidth > LL.getWidth()) {
                currentRow = (currentRow + 1) % 6;
                currentRowWidth = 0;
            } else {
                currentRowWidth += textButton.getWidth();
            }

            GL[currentRow].addView(textButton, lp);
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
        for (GridLayout g: GL) {
            g.removeAllViewsInLayout();
        }
        
        currentRow = 0;
        currentRowWidth = 0;
    }
}