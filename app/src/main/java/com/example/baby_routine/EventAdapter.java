package com.example.baby_routine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLOutput;
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

        public void remove(final int position) {
            recently_removed_position = position;
            recently_removed_event = eventList.get(position);

            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    db.eventDao().delete(recently_removed_event.getDate(), recently_removed_event.getHour(), recently_removed_event.getAction());
                }
            });

            eventList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.getItemCount());

            if(recently_removed_event.getAction().equals("Dormiu") || recently_removed_event.getAction().equals("Acordou")) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = LayoutInflater.from(activity);
                View v = inflater.inflate(R.layout.dialog_remove, null);

                Button btnConfirma = (Button) v.findViewById(R.id.btnConfirma);
                Button btnCancela = (Button) v.findViewById(R.id.btnCancela);

                mBuilder.setView(v);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnConfirma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Removido com sucesso! ", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                btnCancela.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.eventDao().insertEvent(recently_removed_event);
                            }
                        });

                        eventList.add(recently_removed_position, recently_removed_event);
                        notifyItemInserted(recently_removed_position);
                        dialog.dismiss();
                    }
                });
            }
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

            AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    if(db.eventDao().findLastEvent() != null) {
                        if (db.eventDao().findLastEvent().getAction().equals("Dormiu") && !event.getAction().equals("Acordou")) {
                            Event wokeup = new Event(R.drawable.bbwokeup, "Acordou", full_date, hour);
                            db.eventDao().insertEvent(wokeup);
                            eventList.add(wokeup);
                        }
                    }

                    db.eventDao().insertEvent(event);
                    eventList.add(event);
                }
            });

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

            if(event.getAction().equals("Acordou") || event.getAction().equals("Dormiu")) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = LayoutInflater.from(activity);
                View v = inflater.inflate(R.layout.dialog_remove, null);

                Button btnConfirma = (Button) v.findViewById(R.id.btnConfirma);
                Button btnCancela = (Button) v.findViewById(R.id.btnCancela);

                mBuilder.setView(v);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btnConfirma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                db.eventDao().updateHour(event.getHour(), db.eventDao().getId(event.getDate(), eventList.get(position).getHour(), event.getAction()));
                                eventList.get(position).setHour(event.getHour());
                            }
                        });

                        notifyItemChanged(position);
                        Toast.makeText(view.getContext(), "Dados atualizados com sucesso! ", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                btnCancela.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            } else {

                AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        db.eventDao().updateHour(event.getHour(), db.eventDao().getId(event.getDate(), eventList.get(position).getHour(), event.getAction()));
                        eventList.get(position).setHour(event.getHour());
                    }
                });

                notifyItemChanged(position);
            }
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