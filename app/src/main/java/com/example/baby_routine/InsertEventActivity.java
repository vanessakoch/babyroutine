package com.example.baby_routine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class InsertEventActivity extends AppCompatActivity {

    ImageView imageView;
    Spinner spinner;
    Button btnConcluir;
    Event event;
    int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_event);

        imageView = (ImageView) findViewById(R.id.imageAddView);
        spinner = (Spinner) findViewById(R.id.actionSpinner);
        btnConcluir = (Button) findViewById(R.id.insertButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.eventsbaby_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        requestCode = bundle.getInt("request_code");

        event = new Event();
        event.setImage(R.drawable.bbsmile);
        imageView.setImageResource(event.getImage());

    }

    public void clickInsert(View view){

        Bundle bundle = new Bundle();

        event.setAction(spinner.getSelectedItem().toString());

        if (event.getAction().equals("Acordou")){
            event.setImage(R.drawable.bbwokeup);
        } else if (event.getAction().equals("Dormiu")){
            event.setImage(R.drawable.bbsleep);
        } else if (event.getAction().equals("Mamou")){
            event.setImage(R.drawable.bbsuckling);
        } else if (event.getAction().equals("Trocou")){
            event.setImage(R.drawable.bbchanging);
        }

        bundle.putParcelable("event", event);

        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
