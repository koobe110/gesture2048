package com.along.a2048;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GameView game;
    private TextView scoreText;
    private int score = 0;
    private TextView topScoreText;
    private int topScore;
    SharedPreferences preference;
    SharedPreferences.Editor editor;
    private GestureOverlayView overlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        game = (GameView) findViewById(R.id.gameView);
        scoreText = (TextView) findViewById(R.id.scoreText);
        topScoreText = (TextView) findViewById(R.id.topScoreText);
        preference = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        editor = preference.edit();
        topScore = preference.getInt("topScore", 0);
        topScoreText.setText("Top:" + topScore);
        overlayView = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
        final GestureLibrary library = GestureLibraries.fromRawResource(this, R.raw.gestures);
        library.load();
        overlayView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                ArrayList<Prediction> myGesture = library.recognize(gesture);
                Prediction prediction = myGesture.get(0);
                if (prediction.score > 1) {
                    if (prediction.name.equals("cheat")) {
                        Toast.makeText(MainActivity.this, "折线手势", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 3; i++) {
                            game.cards[0][i].setNum(0);
                        }
                    }
                    if (prediction.name.equals("circle")) {
                        Toast.makeText(MainActivity.this, "圆圈手势", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < 3; i++) {
                            game.cards[0][i].setNum(2 * game.cards[0][i].getNum());
                        }
                    }
                }
            }
        });

    }

    private static MainActivity mainActivity;

    public void addScore(int s) {
        score += s;
        showScore();
    }

    public void showScore() {
        scoreText.setText("Score:" + score);
    }

    public void showTopScore() {
        topScoreText.setText("Top:" + score);
    }

    public MainActivity() {
        mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void gameOver() {
        if (score > topScore) {
            editor.putInt("topScore", score);
            editor.commit();
            showTopScore();
        }
        new AlertDialog.Builder(this).setTitle("星星铺满整个夜空")
                .setMessage("今天天气不错！")
                .setPositiveButton("人生不过重头再来", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        score = 0;
                        showScore();
                        game.startGame();
                    }
                }).show();
    }
}
