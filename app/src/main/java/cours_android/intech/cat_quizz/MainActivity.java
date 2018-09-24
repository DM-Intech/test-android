package cours_android.intech.cat_quizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {

    List<Question> mylist = new ArrayList<Question>();
    String[] answerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        InputStream is = getResources().openRawResource(R.raw.quiz);
        TextView text = findViewById(R.id.question);
        Button bt1 = findViewById(R.id.resp1);
        Button bt2 = findViewById(R.id.resp2);
        Button bt3 = findViewById(R.id.resp3);
        Button bt4 = findViewById(R.id.resp4);

        try {
            ReadJson json = new ObjectMapper().readValue(is,ReadJson.class);
            int i = 0;
            for (Question a : json.getQuestions()) {
                i++;
                Log.i("question "+ i, a.getQuestion());
            };
            mylist = json.getQuestions();
            answerList = mylist.get(0).getAnswers();
            text.setText(mylist.get(0).getGoodAnswer());
            bt1.setText(answerList[0]);
            bt2.setText(answerList[1]);
            bt3.setText(answerList[2]);
            bt4.setText(answerList[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
