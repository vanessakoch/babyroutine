package com.example.baby_routine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter {

        private List<Event> eventList;
        private int recently_removed_position;
        private Event recently_removed_event;
        AppCompatActivity activity;
        AppDatabase db;

        public EventAdapter(AppCompatActivity activity) {
            this.activity = activity;
            this.eventList = new ArrayList<Event>();
            db = AppDatabase.getDatabase(activity);

            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    List<Event> list = db.eventDao().getAll();
                    for(Event e : list)
                        eventList.add(e);
                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
            EventViewHolder viewHolder = new EventViewHolder(view);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            EventViewHolder viewHolder = (EventViewHolder) holder;
            viewHolder.image.setImageResource(eventList.get(position).getImage());
            viewHolder.action.setText(eventList.get(position).getAction());
            viewHolder.hour.setText(eventList.get(position).getHour());

            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity.getBaseContext(), EditEventActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", eventList.get(holder.getAdapterPosition()));
                    bundle.putInt("position", holder.getAdapterPosition());
                    bundle.putInt("request_code", MainActivity.REQUEST_EDIT_EVENT);
                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, MainActivity.REQUEST_EDIT_EVENT);
                }
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public void remove(int position){
            recently_removed_position = position;
            recently_removed_event = eventList.get(position);

            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    db.eventDao().delete(recently_removed_event.getDate(), recently_removed_event.getHour());
                }
            });

            eventList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.getItemCount());

            Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.main_activity), "Item deleted", Snackbar.LENGTH_LONG);
            snackbar.setAction("Take it back?", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.eventDao().insertEvent(recently_removed_event);
                        }
                    });

                    eventList.add(recently_removed_position, recently_removed_event);
                    notifyItemInserted(recently_removed_position);
                }
            });

            snackbar.show();

        }

        public void insert(final Event event){

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");

            Date data = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);
            Date actually_date = calendar.getTime();

            final String full_date = dateFormat.format(actually_date);
            final String hour = dateFormat_hora.format(actually_date);

            event.setDate(full_date);
            event.setHour(hour);

            boolean is_sleeping = false;
            Event wokeup = null;

            if (eventList.size() > 0) {
                Event last = eventList.get(eventList.size() - 1);

                if (last.getAction().equals("Dormiu") && !event.getAction().equals("Acordou")) {
                    wokeup = new Event(R.drawable.bbwokeup, "Acordou", full_date, hour);
                    is_sleeping = true;
                }
            }

            final boolean final_sleeping = is_sleeping;
            final Event final_wokeup = wokeup;

            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    if(final_sleeping)
                        db.eventDao().insertEvent(final_wokeup);

                    db.eventDao().insertEvent(event);

                }
            });

            if(is_sleeping)
                eventList.add(wokeup);

            eventList.add(event);
            notifyItemInserted(getItemCount());
        }

        public void move(int fromPosition, int toPosition){
            if (fromPosition < toPosition)
                for (int i = fromPosition; i < toPosition; i++)
                    Collections.swap(eventList, i, i+1);
            else
                for (int i = fromPosition; i > toPosition; i--)
                    Collections.swap(eventList, i, i-1);
            notifyItemMoved(fromPosition,toPosition);
        }

        public void edit(final Event event, final int position) {
            eventList.get(position).setHour(event.getHour());
            System.out.println(event.getId());
            System.out.println(eventList.get(position).getId());
            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(db.eventDao().getId(event.getDate(),event.getHour()));

                    db.eventDao().update(event.getDate(), event.getHour(), db.eventDao().getId(event.getDate(),event.getHour()));
                }
            });

            notifyItemChanged(position);
        }

        public void deleteAll() {
            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    db.eventDao().deleteAll();
                }
            });

            while (!eventList.isEmpty()){
                eventList.remove(0);
                notifyItemRemoved(0);
                notifyItemRangeChanged(0, this.getItemCount());
            }
        }

        public static class EventViewHolder extends RecyclerView.ViewHolder{

            ImageView image;
            TextView action;
            TextView hour;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                itemView.setTag(this);
                image = (ImageView) itemView.findViewById(R.id.imageView);
                action = (TextView) itemView.findViewById(R.id.actionTextView);
                hour = (TextView) itemView.findViewById(R.id.hourTextView);
            }
        }
    }