package cours_android.intech.cat_quizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputStream is = getResources().openRawResource(R.raw.quiz);

        try {
            ReadJson json = new ObjectMapper().readValue(is,ReadJson.class);
            int i = 0;
            for (Question a : json.getQuestions()) {
                i++;
                Log.i("question "+ i, a.getQuestion());
            };

        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Question> QList = new ArrayList<Question>();

    }
}
