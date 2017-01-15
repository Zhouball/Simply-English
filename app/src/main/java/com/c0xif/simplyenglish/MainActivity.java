package com.c0xif.simplyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import android.view.View;
import android.widget.TextView;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements RecognitionListener{

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String NGRAM_SEARCH = "recog";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private SpeechRecognizer recognizer;

    SpeechToTextFragment s2tfrag;
    TextToSimpleFragment t2sfrag;

    StringBuilder actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //s2tfrag.addText("Preparing listener.");
        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        runRecognizerSetup();

        actual = new StringBuilder("");

        //getSupportFragmentManager().beginTransaction().replace(R.id.s2tFrag, new SpeechToTextFragment(),"s2tFrag").commit();
        s2tfrag = ((SpeechToTextFragment) getSupportFragmentManager().findFragmentById(R.id.s2tFrag));
        //getSupportFragmentManager().beginTransaction().replace(R.id.t2sFrag, new TextToSimpleFragment(),"t2sFrag").commit();
        t2sfrag = ((TextToSimpleFragment) getSupportFragmentManager().findFragmentById(R.id.t2sFrag));
    }

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    makeText(getApplicationContext(), "Listener died", Toast.LENGTH_SHORT).show();
                } else {
                    makeText(getApplicationContext(), "Listener is on!", Toast.LENGTH_SHORT).show();
                   // s2tfrag.addText("Listener is on");
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        //String text = hypothesis.getHypstr();
        //((TextView) findViewById(R.id.caption_text)).setText(text);
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            //actual.append(text + ". ");
            processWords(text);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        recognizer.stop();
        recognizer.startListening(NGRAM_SEARCH);
    }

    /*
    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }
    */

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        recognizer.addNgramSearch(NGRAM_SEARCH, new File(assetsDir, "en-70k-0.2-pruned.lm.bin"));
        recognizer.startListening(NGRAM_SEARCH);

    }

    @Override
    public void onError(Exception error) {
        //((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }


    @Override
    public void onTimeout() {
        //switchSearch(KWS_SEARCH);
    }

    public void processWords(String s) {
        Log.d("MainActivity","processing stuffz");
        String[] words = s.split(" ");
        //if in s2t state
        for (String word : words) {
            actual.append(" " + word);
            Log.d("MainActivity", actual.toString());
            s2tfrag.updateText("<span style=\"background-color:#f9f03b;\">" + actual.toString() + "</span>");
            t2sfrag.receiveWord(word);
        }
        //if in t2t state
        actual.append(". ");
    }

    public void clear() {
        actual.setLength(0);
        s2tfrag.updateText("");
        //TODO clear t2sfrag as well
    }
}
