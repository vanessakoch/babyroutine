package com.example.baby_routine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    public final static int REQUEST_EDIT_EVENT = 1;
    public final static int REQUEST_INSERT_EVENT = 2;
    public final static int REQUEST_FILTER_EVENT = 3;
    public final static int REQUEST_RESUME_EVENT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new EventAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelp(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_EDIT_EVENT){
            Bundle bundle = data.getExtras();
            Event event = (Event) bundle.getParcelable("event");
            int position = bundle.getInt("position");
            adapter.edit(event, position);
        }

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_INSERT_EVENT){
            Bundle bundle = data.getExtras();
            Event event = (Event) bundle.getParcelable("event");
            adapter.insert(event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.insertMenu){
            Bundle bundle = new Bundle();
            bundle.putInt("request_code",REQUEST_INSERT_EVENT);
            Intent intent = new Intent(this, InsertEventActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_INSERT_EVENT);
        }

        if (id == R.id.filterMenu) {
            Bundle bundle = new Bundle();
            bundle.putInt("request_code",REQUEST_FILTER_EVENT);
            Intent intent = new Intent(this, FilterEventActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_FILTER_EVENT);
        }

        if (id == R.id.resumeMenu) {
            Bundle bundle = new Bundle();
            bundle.putInt("request_code",REQUEST_RESUME_EVENT);
            Intent intent = new Intent(this, ResumeEventActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, REQUEST_RESUME_EVENT);
        }

        return super.onOptionsItemSelected(item);
    }
}