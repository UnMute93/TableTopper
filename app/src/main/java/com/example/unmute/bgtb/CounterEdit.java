/**
 * Alexander Andersson 930224
 */

package com.example.unmute.bgtb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class CounterEdit extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static final int WHITE = Color.argb(0, 0, 0, 0);
    private static final int BLUE = Color.argb(150, 0, 0, 255);
    private static final int RED = Color.argb(150, 255, 0, 0);
    private static final int GREEN = Color.argb(150, 0, 255, 0);
    private static final int YELLOW = Color.argb(150, 255, 255, 0);

    TextView counterName;
    ImageView counterImage;
    Spinner spnShape;
    Spinner spnColor;
    EditText txtfldName;
    EditText txtfldDefValue;
    Button btnCancel;
    Button btnSave;

    int counterId;
    int defValue;
    int imageId;
    int imageColor;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_edit);

        pref = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        editor = pref.edit();

        counterName = (TextView) findViewById(R.id.txtEditCounter);
        counterImage = (ImageView) findViewById(R.id.imgEditCounter);
        spnShape = (Spinner) findViewById(R.id.spnShape);
        spnColor = (Spinner) findViewById(R.id.spnColor);
        txtfldName = (EditText) findViewById(R.id.txtfldName);
        txtfldDefValue = (EditText) findViewById(R.id.txtfldDefValue);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);

        Intent intent = getIntent(); //Hämta värden skickade från Counters
        counterId = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        defValue = intent.getIntExtra("def_value", 0);
        imageId = intent.getIntExtra("image_id", 0);
        imageColor = intent.getIntExtra("color", 0);

        initSpinners();

        counterName.setText(name);
        counterImage.setImageResource(imageId);
        counterImage.setColorFilter(imageColor);
        txtfldName.setText(name);
        txtfldName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtfldDefValue.setText(Integer.toString(defValue));
        txtfldDefValue.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //Uppdaterar förhandsgranskning av namn.
        txtfldName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    counterName.setText(txtfldName.getText());
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounter();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_counter_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initSpinners() {
        switch (imageId) {
            case R.drawable.circle:
                spnShape.setSelection(0);
                break;
            case R.drawable.diamond:
                spnShape.setSelection(1);
                break;
        }

        if (imageColor == WHITE) {
            spnColor.setSelection(0);
        }
        else if (imageColor == BLUE) {
            spnColor.setSelection(1);
        }
        else if (imageColor == RED) {
            spnColor.setSelection(2);
        }
        else if (imageColor == GREEN) {
            spnColor.setSelection(3);
        }
        else if (imageColor == YELLOW) {
            spnColor.setSelection(4);
        }

        spnShape.setOnItemSelectedListener(CounterEdit.this);
        spnColor.setOnItemSelectedListener(CounterEdit.this);
    }

    public void saveCounter() {
        editor.putString("counter" + Integer.toString(counterId) + "_name", txtfldName.getText().toString());
        editor.putInt("counter" + Integer.toString(counterId) + "_cur_value", Integer.parseInt(txtfldDefValue.getText().toString()));
        editor.putInt("counter" + Integer.toString(counterId) + "_def_value", Integer.parseInt(txtfldDefValue.getText().toString()));
        editor.putInt("counter" + Integer.toString(counterId) + "_image_id", imageId);
        editor.putInt("counter" + Integer.toString(counterId) + "_color", imageColor);
        editor.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.spnShape:
                switch(parent.getSelectedItem().toString()) {
                    case "Circle":
                        imageId = R.drawable.circle;
                        break;
                    case "Diamond":
                        imageId = R.drawable.diamond;
                        break;
                }
                counterImage.setImageResource(imageId);
                break;
            case R.id.spnColor:
                switch(parent.getSelectedItem().toString()) {
                    case "White":
                        imageColor = WHITE;
                        break;
                    case "Blue":
                        imageColor = BLUE;
                        break;
                    case "Red":
                        imageColor = RED;
                        break;
                    case "Green":
                        imageColor = GREEN;
                        break;
                    case "Yellow":
                        imageColor = YELLOW;
                        break;
                }
                counterImage.setColorFilter(imageColor);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }
}
