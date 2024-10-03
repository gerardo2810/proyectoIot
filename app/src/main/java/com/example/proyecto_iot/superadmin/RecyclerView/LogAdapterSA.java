package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class LogAdapterSA extends RecyclerView.Adapter<LogAdapterSA.LogsSAViewHolder>{

    private List<LogSA> logs;

    public LogAdapterSA(List<LogSA> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public LogAdapterSA.LogsSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_logs, parent, false);
        return new LogAdapterSA.LogsSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapterSA.LogsSAViewHolder holder, int position) {
        LogSA log = logs.get(position);
        holder.textViewContenido.setText(log.getContenido());
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public static class LogsSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContenido;

        public LogsSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContenido = itemView.findViewById(R.id.textViewContenido);
        }
    }

}
