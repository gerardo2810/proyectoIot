package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.EnPreparacionFragment;
import com.example.proyecto_iot.admin_restaurante.InicioRestauranteActivity;
import com.example.proyecto_iot.admin_restaurante.MasDetallesPedidoActivity;
import com.example.proyecto_iot.admin_restaurante.PedidoDetallesActivity;

import java.util.List;

public class PedidoPreparadoAdapter extends RecyclerView.Adapter<PedidoPreparadoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;
    private Context context;
    private OnOrderReadyListener listener;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final String CHANNEL_ID = "preparation_complete_channel";

    public PedidoPreparadoAdapter(List<Pedido> pedidoList, Context context, OnOrderReadyListener listener) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.listener = listener;

        // Crear el canal de notificaciones
        createNotificationChannel();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_item_en_preparacion, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.id_new_pedido.setText(pedido.getOrderId());
        holder.cliente.setText(pedido.getCliente());
        holder.cant_productos.setText(pedido.getCantidad());


        // Si el pedido ya está listo, no reiniciar el temporizador
        if (pedido.isReady()) {
            holder.time_remaining.setText("¡Listo!");
        } else {
            // Convertir el tiempo en milisegundos para el temporizador
            int timeInMinutes = Integer.parseInt(pedido.getTiempo());
            long timeInMillis = timeInMinutes * 60 * 1000;

            // Crear el temporizador
            new CountDownTimer(timeInMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    // Actualizar el TextView del temporizador
                    long minutes = millisUntilFinished / (60 * 1000);
                    long seconds = (millisUntilFinished / 1000) % 60;
                    String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                    holder.time_remaining.setText(timeFormatted);
                }

                public void onFinish() {
                    holder.time_remaining.setText("¡Listo!");
                    // Marcar el pedido como listo
                    pedido.setReady(true);
                    // Mostrar la notificación cuando el tiempo de preparación se cumpla
                    sendNotification(pedido);
                }
            }.start();
        }

        // Configurar el botón "Listo para entregar"
        holder.btn_ready_to_deliver.setOnClickListener(v -> {
            listener.onOrderReady(pedido);
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    // Crear el canal de notificaciones (solo para Android 8+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Preparation Complete";
            String description = "Notificaciones cuando un pedido esté listo para entregar";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para enviar la notificación
    private void sendNotification(Pedido pedido) {
        Intent intent = new Intent(context, InicioRestauranteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.listo) // Icono de notificación
                .setContentTitle("Pedido #" + pedido.getOrderId() + " listo")
                .setContentText("El tiempo de preparación para " + pedido.getCantidad() + " ha terminado")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(pedido.getOrderId().hashCode(), builder.build());
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView id_new_pedido, cliente, cant_productos, time_remaining;
        Button btn_ready_to_deliver;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            id_new_pedido = itemView.findViewById(R.id.id_new_pedido);
            cliente = itemView.findViewById(R.id.cliente);
            cant_productos = itemView.findViewById(R.id.cant_productos);
            time_remaining = itemView.findViewById(R.id.time_remaining);
            btn_ready_to_deliver = itemView.findViewById(R.id.btn_ready_to_deliver);
        }
    }

    public interface OnOrderReadyListener {
        void onOrderReady(Pedido pedido);
    }
}

