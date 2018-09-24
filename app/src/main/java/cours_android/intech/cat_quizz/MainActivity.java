package cours_android.intech.cat_quizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
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
        final TextView text = findViewById(R.id.question);
        int[]  ids = new int[]{R.id.resp1, R.id.resp2, R.id.resp3, R.id.resp4};

        try {
            ReadJson json = new ObjectMapper().readValue(is,ReadJson.class);

            mylist = json.getQuestions();
            answerList = mylist.get(1).getAnswers();
            text.setText(mylist.get(1).getGoodAnswer());
            for(int i = 0; i < ids.length; i++) {

                final Button temp = findViewById(ids[i]);
                temp.setText(answerList[i]);
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyAnswer("" + temp.getText(), mylist.get(1).getGoodAnswer());
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Boolean verifyAnswer(String answer, String goodAnswer){
        if(answer == goodAnswer) {
            // good answer action
            return true;
        }
        else {
            // bad answer action
            return false;
        }
    }
}
