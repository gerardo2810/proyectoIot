package com.example.proyecto_iot.superadmin.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class ReporteAdapterSA extends RecyclerView.Adapter<ReporteAdapterSA.ReporteSAViewHolder> {

    private List<ReporteSA> reportes;

    public ReporteAdapterSA(List<ReporteSA> reportes) {
        this.reportes = reportes;
    }

    @NonNull
    @Override
    public ReporteAdapterSA.ReporteSAViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.superadmin_item_restaurantes, parent, false);
        return new ReporteAdapterSA.ReporteSAViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteAdapterSA.ReporteSAViewHolder holder, int position) {
        ReporteSA reporte = reportes.get(position);

        holder.textViewNombreRestaurante.setText(reporte.getNombre_restaurante());
        holder.textViewAdminRestaurante.setText(reporte.getAdmin_restaurante());
        holder.textViewFechaReporte.setText(reporte.getFecha());

        // Cambiar la imagen según el tipo de reporte
        if (reporte.getTipo_reporte().equals("Por Usuarios")) {
            holder.imageViewTipoReporte.setImageResource(R.drawable.usuarios);
        } else if (reporte.getTipo_reporte().equals("Por Plato")) {
            holder.imageViewTipoReporte.setImageResource(R.drawable.platos_sa);
        }
    }

    @Override
    public int getItemCount() {
        return reportes.size();
    }

    public static class ReporteSAViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreRestaurante, textViewAdminRestaurante, textViewFechaReporte;
        ImageView imageViewTipoReporte;

        public ReporteSAViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreRestaurante = itemView.findViewById(R.id.textViewNombreRestaurante);
            textViewAdminRestaurante = itemView.findViewById(R.id.textViewAdminRestaurante);
            imageViewTipoReporte = itemView.findViewById(R.id.imageViewTipoReporte);
        }
    }

}
