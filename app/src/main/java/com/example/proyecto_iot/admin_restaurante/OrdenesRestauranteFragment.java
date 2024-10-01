package com.example.proyecto_iot.admin_restaurante;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Order;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdenesRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdenesRestauranteFragment extends Fragment {

    private RecyclerView rvOrdersList;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View view = inflater.inflate(R.layout.fragment_ordenes_restaurante, container, false);

        // Configurar el RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crear la lista de órdenes
        orderList = new ArrayList<>();
        orderList.add(new Order("ID 0000012", "El Molle # 156 B", "106.00", "EN CAMINO"));
        orderList.add(new Order("ID 0000013", "El Molle # 156 B", "76.00", "EN PREPARACIÓN"));
        orderList.add(new Order("ID 0000014", "El Molle # 156 B", "20.00", "ENTREGADO"));
        orderList.add(new Order("ID 0000015", "El Molle # 156 B", "20.00", "EN CAMINO"));
        orderList.add(new Order("ID 0000016", "El Molle # 156 B", "20.00", "EN TIENDA"));
        orderList.add(new Order("ID 0000017", "El Molle # 156 B", "20.00", "ENTREGADO"));

        // Configurar el adaptador
        orderAdapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Manejar el clic en el pedido (mostrar detalles, por ejemplo)
                Toast.makeText(getContext(), "Detalles de " + order.getOrderId(), Toast.LENGTH_SHORT).show();
                // Aquí puedes iniciar una nueva actividad o cambiar a otro fragmento
            }
        });

        rvOrdersList.setAdapter(orderAdapter);

        return view;  // Retornar la vista del fragmento
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdenesRestauranteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrdenesRestauranteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrdenesRestauranteFragment newInstance(String param1, String param2) {
        OrdenesRestauranteFragment fragment = new OrdenesRestauranteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

}