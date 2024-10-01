package com.example.proyecto_iot.admin_restaurante.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurante_activity_item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderAddress, tvOrderPrice, tvOrderStatus;
        ImageView ivOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            ivOrderDetails = itemView.findViewById(R.id.iv_order_details);
        }

        public void bind(final Order order) {
            tvOrderId.setText(order.getOrderId());
            tvOrderAddress.setText(order.getAddress());
            tvOrderPrice.setText("S/ " + order.getPrice());
            tvOrderStatus.setText(order.getStatus());

            ivOrderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOrderClick(order);  // Llamamos al listener para manejar el clic
                }
            });
        }
    }
}