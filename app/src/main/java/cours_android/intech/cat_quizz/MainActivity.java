package cours_android.intech.cat_quizz;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.droidsonroids.gif.GifDrawable;

public class MainActivity extends AppCompatActivity {

    List<Question> mylist = new ArrayList<Question>();
    String[] answerList;
    Random r = new Random();
    int j;
    TextView text;
    public MediaPlayer playr;

    ImageView catGif;
    ImageView flingCat;
    int gifRes;
    float dX = 0f;
    float dY = 0f;
    float STIFFNESS = SpringForce.STIFFNESS_MEDIUM;
    float DAMPING_RATIO = SpringForce.DAMPING_RATIO_LOW_BOUNCY;
    SpringAnimation xAnimation;
    SpringAnimation yAnimation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        catGif = findViewById(R.id.cat);
        flingCat = findViewById(R.id.flingCat);

        gifRes = R.raw.cat;
        showGif(100,100, catGif, gifRes);
        gifRes =R.raw.cat_fly_neutral;
        showGif(100, 100, flingCat, gifRes);

        catGif.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                xAnimation = createSpringAnimation(
                        catGif, SpringAnimation.X, catGif.getX(), STIFFNESS, DAMPING_RATIO);
                yAnimation = createSpringAnimation(
                        catGif, SpringAnimation.Y, catGif.getY(), STIFFNESS, DAMPING_RATIO);
                catGif.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        dragCat();

        InputStream is = getResources().openRawResource(R.raw.quiz);

        text = findViewById(R.id.question);

        try {
            ReadJson json = new ObjectMapper().readValue(is,ReadJson.class);
            mylist = json.getQuestions();
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
        j = r.nextInt(mylist.size());
        answerList = mylist.get(j).getAnswers();
        text.setText(mylist.get(j).getQuestion());


        int[]  ids = new int[]{R.id.resp1, R.id.resp2, R.id.resp3, R.id.resp4};
        final int answerBTid = ids[mylist.get(j).getGoodAnswer()];

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
            if (j < mylist.size() && j >= 0) {
                mylist.remove(j);
                if(mylist.size() > 0) {
                    showQuestion();
                }
                else{
                    setContentView(R.layout.activity_end_quiz);
                    ImageView catClap = findViewById(R.id.clappingCat);
                    gifRes = R.raw.cat_clap;
                    showGif(400,400, catClap, gifRes);
                }
            }
            else {
                setContentView(R.layout.activity_end_quiz);
            }
        }
        else {
            //sond plays + cat image change
            playSoud(R.raw.catsoud1);
            MakeVibrate(500);
        }
    }
    public void MakeVibrate(int mllisecond) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(mllisecond, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(mllisecond);
        }
    }

    //Show the gif on the layout
    //int width ; int height is for the size of the gif
    //imageView img is the must contain the id of the gif on the layout
    //gifRes must contain the ressources
    private void showGif(int width, int height, ImageView img, int gifRes){
        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(width, height);

        Glide.with(this)
                .load(gifRes)
                .apply(myOptions)
                .into(img);
    }
    public void playSoud(int sound){
        playr = MediaPlayer.create(this,sound);
        playr.start();
        //playr.pause();
        //playr.stop();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void dragCat(){


        catGif.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        flingCat.setVisibility(flingCat.VISIBLE);

                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();

                        flingCat.setX(view.getX() - event.getRawX());
                        flingCat.setY(view.getY() - event.getRawY());

                        xAnimation.cancel();
                        yAnimation.cancel();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        catGif.setVisibility(catGif.INVISIBLE);
                        catGif.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();

                        flingCat.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        catGif.setVisibility(catGif.VISIBLE);
                        flingCat.setVisibility(flingCat.INVISIBLE);
                        yAnimation.start();
                        break;
                }
                return true;
            }
        });
    }

    SpringAnimation createSpringAnimation(View view,
                                          DynamicAnimation.ViewProperty property,
                                          Float finalPosition,
                                          @FloatRange(from = 0.0) Float stiffness,
                                          @FloatRange(from = 0.0) Float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce spring = new SpringForce(finalPosition);
        spring.setStiffness(stiffness);
        spring.setDampingRatio(dampingRatio);
        animation.setSpring(spring);
        return animation;
    }

}
