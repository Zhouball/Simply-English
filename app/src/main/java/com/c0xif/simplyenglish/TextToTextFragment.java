package com.c0xif.simplyenglish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextToTextFragment extends Fragment {

    private TextView text;
    private Button submit;

    public static TextToTextFragment newInstance() {
        return new TextToTextFragment();
    }

    public TextToTextFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_to_text, container, false);
        submit = (Button) view.findViewById(R.id.submit_button);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    
            }
        });

        text = (TextView) view.findViewById(R.id.edit_text);
        return view;
    }



}

