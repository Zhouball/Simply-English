package com.c0xif.simplyenglish;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SpeechToTextFragment extends Fragment {

    public static TextToSimpleFragment newInstance() {
        return new TextToSimpleFragment();
    }

    public SpeechToTextFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_speech_to_text, container, false);
    }
}

