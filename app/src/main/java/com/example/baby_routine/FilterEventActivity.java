package com.example.baby_routine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FilterEventActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FilterAdapter adapter;

    Button btnDormiu;
    Button btnAcordou;
    Button btnMamou;
    Button btnTrocou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_event);

        adapter = new FilterAdapter(this);

        btnDormiu = (Button) findViewById(R.id.btnDormir);
        btnAcordou = (Button) findViewById(R.id.btnAcordar);
        btnMamou = (Button) findViewById(R.id.btnMamar);
        btnTrocou = (Button) findViewById(R.id.btnTrocar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerFilterView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    public void filterSleeping(View v){
        adapter.filter("Dormiu");
    }

    public void filterWakingup(View v){
        adapter.filter("Acordou");
    }

    public void filterSuckling(View v){
        adapter.filter("Mamou");
    }

    public void filterChanging(View v){
        adapter.filter("Trocou");
    }

    public void backing(View view) {
        Bundle bundle = new Bundle();
        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
