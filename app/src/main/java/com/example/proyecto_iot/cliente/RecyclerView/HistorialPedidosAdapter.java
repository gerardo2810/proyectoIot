package com.example.proyecto_iot.cliente.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.DetallesPedidoActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HistorialPedidosAdapter extends RecyclerView.Adapter<HistorialPedidosAdapter.PedidoViewHolder> {

    private List<Pedido> pedidos;
    private Context context;  // Para iniciar la nueva actividad

    public HistorialPedidosAdapter(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;  // Guardar el contexto
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido_historial, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);

        // Asignar datos al ViewHolder
        holder.textRestaurantName.setText(pedido.getNombreRestaurante());
        // Formatear la fecha correctamente
        String fechaFormateada = formatearFecha(pedido.getFechaHora());

        // Obtener el estado como texto y su color
        String estadoTexto = obtenerEstadoPedido(pedido.getEstado());
        int colorEstado = obtenerColorEstado(pedido.getEstado());

        // Crear un SpannableString para aplicar estilos de texto
        String textoCompleto = estadoTexto + " - " + fechaFormateada;
        SpannableString spannable = new SpannableString(textoCompleto);

        // Aplicar color solo a la palabra del estado
        spannable.setSpan(
                new ForegroundColorSpan(colorEstado),
                0, // Inicio del estado
                estadoTexto.length(), // Fin del estado
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Asignar el texto estilizado al TextView
        holder.textOrderStatus.setText(spannable);

        // Obtener el campo `fotoLogo` desde Firebase usando el `idRestaurante`
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").document(pedido.getIdRestaurante())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fotoLogoUrl = documentSnapshot.getString("fotoLogo");
                        if (fotoLogoUrl != null && !fotoLogoUrl.isEmpty()) {
                            // Cargar la imagen con Glide
                            Glide.with(context)
                                    .load(fotoLogoUrl)
                                    .placeholder(R.drawable.placeholder) // Imagen mientras se carga
                                    .error(R.drawable.placeholder) // Imagen si hay un error
                                    .into(holder.productImage);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // En caso de error, mostrar imagen por defecto
                    holder.productImage.setImageResource(R.drawable.placeholder);
                });
        // Configurar click listener
        holder.itemView.setOnClickListener(v -> {
            // Crear Intent y pasar todos los datos
            Intent intent = new Intent(context, DetallesPedidoActivity.class);
            intent.putExtra("idPedido", pedido.getIdPedido());
            intent.putExtra("nombreRestaurante", pedido.getNombreRestaurante());
            intent.putExtra("estado", pedido.getEstado());
            intent.putExtra("fechaHora", pedido.getFechaHora());
            intent.putExtra("direccion", pedido.getDireccion());
            intent.putExtra("pagoTotal", pedido.getPagoTotal());
            intent.putExtra("idRestaurante", pedido.getIdRestaurante());

            // Convertir lista de productos a ArrayList serializable
            ArrayList<HashMap<String, Object>> productosData = new ArrayList<>();
            for (Producto producto : pedido.getProductos()) {
                HashMap<String, Object> productoMap = new HashMap<>();
                productoMap.put("nombre", producto.getNombre());
                productoMap.put("cantidad", producto.getCantidad());
                productoMap.put("imageUrl", producto.getImageUrl());
                productoMap.put("precio",producto.getPrecio());
                productosData.add(productoMap);
            }
            intent.putExtra("productos", productosData);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView textRestaurantName, textOrderStatus;
        ImageView forwardArrow,productImage;  // Referencia a la flecha

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            textRestaurantName = itemView.findViewById(R.id.text_restaurant_name);
            textOrderStatus = itemView.findViewById(R.id.text_order_status);
            forwardArrow = itemView.findViewById(R.id.forward_arrow);  // Inicializar la flecha
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
    private String obtenerEstadoPedido(int estado) {
        switch (estado) {
            case 1: return "Recibido";
            case 2: return "En preparación";
            case 3: return "En camino";
            case 4: return "Entregado";
            case 5: return "Cancelado";
            case 6: return "Rechazado";
            case 8: return  "Entregado";
            case 7:
                return "En camino";
            default: return "Desconocido";
        }
    }
    private int obtenerColorEstado(int estado) {
        switch (estado) {
            case 4: // Entregado
                return context.getResources().getColor(R.color.verde_estado);
            case 5: // Cancelado
                return context.getResources().getColor(R.color.rojo_estado);
            case 6: // Rechazado
                return context.getResources().getColor(R.color.rojo_estado);
            case 3: // En camino
                return context.getResources().getColor(R.color.amarillo_estado);
            case 7:
                return context.getResources().getColor(R.color.amarillo_estado);
            case 8:
                return context.getResources().getColor(R.color.verde_estado);
            default: // Otros estados
                return context.getResources().getColor(R.color.gris_estado);
        }
    }
    private String formatearFecha(String fechaHoraOriginal) {
        try {
            // Ajustar el formato de la fecha que llega desde Firebase
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());

            // Crear el formato deseado
            SimpleDateFormat formatoDeseado = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss a", Locale.getDefault());

            // Convertir la fecha al formato deseado
            Date fecha = formatoOriginal.parse(fechaHoraOriginal);
            return formatoDeseado.format(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay un error, retornar la fecha original
            return fechaHoraOriginal;
        }
    }



}
