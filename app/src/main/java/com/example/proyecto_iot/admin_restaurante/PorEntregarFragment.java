package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoEntregadoAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.PedidoPreparadoAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PorEntregarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PorEntregarFragment extends Fragment {

    private RecyclerView rv_orders_list;
    private PedidoEntregadoAdapter pedidoEntregadoAdapter;
    private List<Pedido> pedidoList;
    private List<Pedido> filteredList; // Lista filtrada
    private EditText orderSearch;

    public PorEntregarFragment() {
        // Required empty public constructor
    }

    public static PorEntregarFragment newInstance(String param1, String param2) {
        PorEntregarFragment fragment = new PorEntregarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_por_entregar, container, false);

        // Configurar el RecyclerView
        rv_orders_list = view.findViewById(R.id.rv_orders_list);
        rv_orders_list.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar el EditText para búsqueda
        orderSearch = view.findViewById(R.id.order_search);

        // Crear la lista de órdenes
        pedidoList = new ArrayList<>();
        pedidoList.add(new Pedido("María Lopez","#004","3 productos", "S/155.00", "20 min", "Repartidor Asignado"));
        pedidoList.add(new Pedido("Carlos Gomez","#005","3 productos", "S/205.00", "10 min", "Sin Repartidor"));
        pedidoList.add(new Pedido("Andrea Lomeli","#006","4 productos", "S/380.00", "18 min", "Sin Repartidor"));
        pedidoList.add(new Pedido("Carlos Rios","#007","7 productos", "S/555.00", "35 min", "Repartidor Asignado"));

        // Inicializar la lista filtrada con la lista original
        filteredList = new ArrayList<>(pedidoList);

        // Configurar el adaptador con la lista filtrada
        pedidoEntregadoAdapter = new PedidoEntregadoAdapter(filteredList, getContext());
        rv_orders_list.setAdapter(pedidoEntregadoAdapter);

        // Configurar el buscador
        orderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se necesita hacer nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString()); // Llamar al método que filtra la lista
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se necesita hacer nada aquí
            }
        });

        return view;
    }

    // Método para filtrar las órdenes
    private void filterOrders(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            // Si no hay texto en la búsqueda, mostrar todas las órdenes
            filteredList.addAll(pedidoList);
        } else {
            for (Pedido pedido : pedidoList) {
                if (pedido.getOrderId().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(pedido);
                }
            }
        }

        if (filteredList.isEmpty()) {
            // Mostrar un Toast si no se encontraron resultados
            Toast.makeText(getContext(), "No se encontraron resultados", Toast.LENGTH_SHORT).show();
        }

        pedidoEntregadoAdapter.notifyDataSetChanged(); // Notificar al adaptador sobre el cambio
    }
}
