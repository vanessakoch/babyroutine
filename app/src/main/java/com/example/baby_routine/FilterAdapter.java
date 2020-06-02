package com.example.baby_routine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter {

    private List<Event> eventList;
    AppCompatActivity activity;
    AppDatabase db;

    public FilterAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.eventList = new ArrayList<Event>();
        db = AppDatabase.getDatabase(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_card, parent, false);
        FilterViewHolder viewHolder = new FilterViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        FilterViewHolder viewHolder = (FilterViewHolder) holder;
        viewHolder.actionView.setText(eventList.get(position).getAction());
        viewHolder.hourView.setText(eventList.get(position).getHour());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void clearList(){
        while (!eventList.isEmpty()){
            eventList.remove(0);
            notifyItemRemoved(0);
            notifyItemRangeChanged(0, this.getItemCount());
        }
    }

    public void filter(final String action){
        clearList();

        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (action.equals("Dormiu")) {
                    eventList = db.eventDao().getAllByAction("Dormiu");
                } else if (action.equals("Acordou")){
                    eventList = db.eventDao().getAllByAction("Acordou");
                } else if(action.equals("Trocou")){
                    eventList = db.eventDao().getAllByAction("Trocou");
                } else {
                    eventList = db.eventDao().getAllByAction("Mamou");
                }
            }
        });

        notifyItemInserted(getItemCount());
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder{

        TextView hourView;
        TextView actionView;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);

            hourView = (TextView) itemView.findViewById(R.id.viewHora);
            actionView = (TextView) itemView.findViewById(R.id.viewEvento);
        }
    }}