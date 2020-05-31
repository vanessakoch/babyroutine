package com.example.baby_routine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditEventActivity extends AppCompatActivity {

        ImageView imageView;
        EditText hourEditText;
        Button btnEditar;
        Event event;
        int position;
        int requestCode;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_event);

            imageView = (ImageView) findViewById(R.id.imageEditView);
            hourEditText = (EditText) findViewById(R.id.hourEditText);
            btnEditar = (Button) findViewById(R.id.concluirButton);

            Bundle bundle = getIntent().getExtras();
            requestCode = bundle.getInt("request_code");

            if (requestCode == MainActivity.REQUEST_EDIT_EVENT){
                event = (Event) bundle.getParcelable("event");
                position = bundle.getInt("position");

                imageView.setImageResource(event.getImage());
                hourEditText.setText(event.getHour());

            }

        }

        public void clickEdit(View view){

            Bundle bundle = new Bundle();

            bundle.putInt("position",position);

            event.setHour(hourEditText.getText().toString());

            bundle.putParcelable("event", event);

            Intent returnIntent = new Intent();
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }
