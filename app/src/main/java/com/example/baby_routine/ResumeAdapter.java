package com.example.baby_routine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResumeAdapter extends RecyclerView.Adapter {

    private List<String> resumeList;
    AppCompatActivity activity;
    AppDatabase db;

    public ResumeAdapter(AppCompatActivity activity) {
        this.activity = activity;
        this.resumeList = new ArrayList<String>();
        db = AppDatabase.getDatabase(activity);

        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {

                List<Event> list = db.eventDao().getAll();
                List<String> sleepingHours;
                List<String> wakingupHours;
                double milissegundos = 0;
                String date = "";

                for (int i = 0; i < list.size(); i++) {
                    if (!date.equals(list.get(i).getDate())) {
                        date = list.get(i).getDate();
                        resumeList.add("\nData: " + list.get(i).getDate());

                        sleepingHours = db.eventDao().getHours("Dormiu", date);
                        wakingupHours = db.eventDao().getHours("Acordou", date);

                        if(wakingupHours.size() > sleepingHours.size()){
                            wakingupHours.remove(0);
                        }

                        for (int j = 0; j < wakingupHours.size(); j++) {
                            milissegundos += calcularDiferencaHoras(sleepingHours.get(j), wakingupHours.get(j));
                        }

                        int segundos = (int) (milissegundos / 1000) % 60;
                        int minutos = (int) (milissegundos / 60000) % 60;
                        int horas = (int) milissegundos / 3600000;

                        resumeList.add("Bebe mamou " + db.eventDao().countEvent("Mamou", list.get(i).getDate()) + " vez (es)." +
                                "\nBebe foi trocado " + db.eventDao().countEvent("Trocou", list.get(i).getDate()) + " vez (es)." +
                                "\nBebe dormiu por " + horas + ":" + minutos + ":" + segundos + " horas.");
                        milissegundos = 0;
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resume_card, parent, false);
        ResumeAdapter.ResumeViewHolder viewHolder = new ResumeAdapter.ResumeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ResumeAdapter.ResumeViewHolder viewHolder = (ResumeAdapter.ResumeViewHolder) holder;
        viewHolder.txtResume.setText(resumeList.get(position));
    }

    public double calcularDiferencaHoras(String horaInicial, String horaFinal){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        try {
            Date horaIni = sdf.parse(horaInicial);
            Date horaFim = sdf.parse(horaFinal);

            double horaI = horaIni.getTime();
            double horaF = horaFim.getTime();
            double fim = horaF - horaI;

            return fim;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }

    @Override
    public int getItemCount() {
        return resumeList.size();
    }

    public void clearList(){
        while (!resumeList.isEmpty()){
            resumeList.remove(0);
            notifyItemRemoved(0);
            notifyItemRangeChanged(0, this.getItemCount());
        }
    }

    public static class ResumeViewHolder extends RecyclerView.ViewHolder{

        TextView txtResume;

        public ResumeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);

            txtResume = (TextView) itemView.findViewById(R.id.txtCardResume);
        }
    }}