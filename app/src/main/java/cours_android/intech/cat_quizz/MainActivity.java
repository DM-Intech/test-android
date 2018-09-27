package cours_android.intech.cat_quizz;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Vibrator;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class MainActivity extends AppCompatActivity {

    List<Question> mylist = new ArrayList<Question>();
    String[] answerList;
    Random r = new Random();
    State state = new State();
    int j;
    TextView text;
    TextView score;
    GifDrawable cat;
    public MediaPlayer playr;
    ObjectMapper maper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        showGif();

        InputStream is = getResources().openRawResource(R.raw.quiz);
        text = findViewById(R.id.question);
        score = findViewById(R.id.score);

        try {
            ReadJson json = maper.readValue(is,ReadJson.class);
            mylist = json.getQuestions();

            File file = new File(getFilesDir(),"state.json");
            boolean exists = file.exists();
            if(exists){
                state = maper.readValue(new File(getFilesDir(), "state.json"), State.class);
                if(state.getQuestionList().size() == 0){
                    state.setScore(0);
                    state.setQuestionList(mylist);
                    maper.writeValue(new File(getFilesDir(),"state.json"), state);
                }
            }
            else{
                state.setQuestionList(mylist);
                maper.writeValue(new File(getFilesDir(),"state.json"), state);
            }
            showQuestion();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Button btHelp = findViewById(R.id.helping);

        btHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),R.string.help_message,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showQuestion(){
        //j = r.nextInt(mylist.size());
        j = r.nextInt(state.getQuestionList().size());
        //answerList = mylist.get(j).getAnswers();
        answerList = state.getQuestionList().get(j).getAnswers();
        //text.setText(mylist.get(j).getQuestion());
        text.setText(state.getQuestionList().get(j).getQuestion());
        score.setText(""+state.getScore());


        int[]  ids = new int[]{R.id.resp1, R.id.resp2, R.id.resp3, R.id.resp4};
        //final int answerBTid = ids[mylist.get(j).getGoodAnswer()];
        final int answerBTid = ids[state.getQuestionList().get(j).getGoodAnswer()];

        for(int i = 0; i < ids.length; i++) {

            Button temp = findViewById(ids[i]);
            temp.setText(answerList[i]);
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyAnswer(answerBTid, v.getId());
                }
            });
        }
    }

    private void verifyAnswer(int answerBTid, int curentBTid){
        if(answerBTid == curentBTid) {
            if (j < state.getQuestionList().size() && j >= 0) {
                state.getQuestionList().remove(j);
                if(state.getQuestionList().size() > 0) {
                    state.setScore(state.getScore()+1);
                    showQuestion();
                }
                else{
                    setContentView(R.layout.activity_end_quiz);
                }
            }
            else {
                setContentView(R.layout.activity_end_quiz);
            }
        }
        else {
            //cat image change
            playSoud(R.raw.catsoud1);
            MakeVibrate(500);
        }
        saveState();
    }
    public void MakeVibrate(int mlliseconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(mlliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(mlliseconds);
        }
    }

    private void showGif(){
        try {
            GifDrawable gifFromResource = new GifDrawable( getResources(), R.raw.cat);
            gifFromResource.setSpeed(3f);
            gifFromResource.start();

        } catch (IOException e) {
            Log.e("gif error", "Une erreur est survenue lors du chargement", e);
        }
    }
    public void playSoud(int sound){
        playr = MediaPlayer.create(this,sound);
        playr.start();
        //playr.pause();
        //playr.stop();
    }

    private void saveState(){
        try {
            maper.writeValue(new File(getFilesDir(),"state.json"), state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        saveState();
        super.onDestroy();
    }

}
