package de.reedwilliam2004.logoquiz;

import static de.reedwilliam2004.logoquiz.R.id.btnHint;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnHint;
    Button btnSkip;
    Button btnCheck;
    ImageView ivLogo;
    EditText etLogoName;
    TextView tvHint;

    LottieAnimationView av_correct;

    int currentLevel;
    String companyName;
    String imageName;
    String toastWrongMessage;
    String toastSkipMessage;
    String tvHintMessage;

    final int maxLevel = 4;
    final String prefNameFirstStart = "firstAppStart";
    final String databaseName = "level.db";
    final String databaseTableName;

    {
        databaseTableName = "level";
    }

    final String prefLevel = "currentLevel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivLogo = findViewById(R.id.ivLogo);

        tvHint = findViewById(R.id.tvHint);
        etLogoName = findViewById(R.id.etLogoName);

        btnHint = findViewById(R.id.btnHint);
        btnSkip = findViewById(R.id.btnSkip);
        btnCheck = findViewById(R.id.btnCheck);

        av_correct = findViewById(R.id.av_correct);

        btnSkip.setOnClickListener(this);
        btnCheck.setOnClickListener(this);
        btnHint.setOnClickListener(this);

        if (firstAppStart()){
            createDatabase();
        }

        loadLevel();

    }

    public void loadLevel() {
        SharedPreferences preferencesLoad = getSharedPreferences(prefLevel, MODE_PRIVATE);
        currentLevel = preferencesLoad.getInt(prefLevel, 1);
        if (currentLevel <= maxLevel) {
            SQLiteDatabase database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM " + databaseTableName + " WHERE id = '" + currentLevel + "'", null);
            cursor.moveToFirst();
            if (cursor.getCount() == 1){
                companyName = cursor.getString(1);
                imageName = cursor.getString(2);

                cursor.close();
                database.close();
            }
            int imageID = getResources().getIdentifier(imageName, "drawable", getPackageName());
            ivLogo.setImageResource(imageID);
        }
        ivLogo.setVisibility(View.VISIBLE);
        etLogoName.setVisibility(View.VISIBLE);
        btnSkip.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.VISIBLE);
        btnHint.setVisibility(View.VISIBLE);
        av_correct.setVisibility(View.INVISIBLE);

        etLogoName.setText("");
    }

    public void saveLevel() {
        SharedPreferences preferencesLevel = getSharedPreferences(prefLevel, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencesLevel.edit();

        editor.putInt(prefLevel, currentLevel);
        editor.commit();
    }

    public void animateLevelCompleted() {
        ivLogo.setVisibility(View.INVISIBLE);
        etLogoName.setVisibility(View.INVISIBLE);
        btnSkip.setVisibility(View.INVISIBLE);
        btnCheck.setVisibility(View.INVISIBLE);
        btnHint.setVisibility(View.INVISIBLE);
        tvHint.setVisibility(View.INVISIBLE);

        av_correct.setVisibility(View.VISIBLE);
        av_correct.playAnimation();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadLevel();
            }
        }, 2500);
    }

    public boolean firstAppStart() {
        SharedPreferences preferences = getSharedPreferences(prefNameFirstStart, MODE_PRIVATE);
        if (preferences.getBoolean(prefNameFirstStart, true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(prefNameFirstStart, false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    public void createDatabase(){
        SQLiteDatabase database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE " + databaseTableName + " (id INTEGER, company TEXT, imageName TEXT)");
        database.execSQL("INSERT INTO " + databaseTableName + " VALUES('1', 'NASA', 'nasa')");
        database.execSQL("INSERT INTO " + databaseTableName + " VALUES('2', 'YouTube', 'youtube')");
        database.execSQL("INSERT INTO " + databaseTableName + " VALUES('3', 'Instagram', 'instagram')");
        database.execSQL("INSERT INTO " + databaseTableName + " VALUES('4', 'Audi', 'audi')");
        database.close();
    }

    //@Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCheck:
                if (etLogoName.getText().toString().equalsIgnoreCase(companyName)){
                    if (currentLevel < maxLevel) {
                        currentLevel++;
                    } else {
                        currentLevel = 1;
                    }
                    saveLevel();
                    animateLevelCompleted();
                } else {
                    toastWrongMessage = getString(R.string.toast_wrong_answer);
                    Toast.makeText(getApplicationContext(), toastWrongMessage, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnHint:
                tvHintMessage = getString(R.string.tv_hint_begin);
                tvHint.setText(tvHintMessage + companyName.substring(0, 1));
                tvHint.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSkip:
                if (currentLevel < maxLevel) {
                    currentLevel++;
                } else {
                    currentLevel = 1;
                }
                saveLevel();
                toastSkipMessage = getString(R.string.toast_skip);
                Toast.makeText(getApplicationContext(), toastSkipMessage + companyName, Toast.LENGTH_LONG).show();
                animateLevelCompleted();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

}