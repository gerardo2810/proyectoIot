package com.example.proyecto_iot.admin_restaurante;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Order;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.OrderAdapter;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.Pedido;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.util.Log;


public class OrdenesRestauranteFragment extends Fragment {

    private static final String TAG = "OrdenesRestaurante"; // Tag para logs
    private RestauranteViewModel restauranteViewModel;
    private FirebaseFirestore db;
    private RecyclerView rvOrdersList;
    private OrderAdapter orderAdapter;
    private List<Pedido> orderList;
    private List<Pedido> filteredList;
    private TextView tvFecha;
    private Calendar selectedDate; // Fecha seleccionada
    private SimpleDateFormat dateFormat;
    private String idRestaurante; // ID del restaurante

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_ordenes_restaurante, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar RecyclerView
        rvOrdersList = view.findViewById(R.id.rv_orders_list);
        rvOrdersList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar búsqueda
        EditText orderSearch = view.findViewById(R.id.order_search);

        orderSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString()); // Filtra en tiempo real
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Configurar TextView de fecha
        tvFecha = view.findViewById(R.id.tv_fecha);
        selectedDate = Calendar.getInstance(); // Fecha inicial: hoy
        dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        tvFecha.setText("Fecha: " + dateFormat.format(selectedDate.getTime())); // Fecha actual

        // Configurar lista de pedidos
        orderList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Configurar adaptador
        orderAdapter = new OrderAdapter(filteredList, getContext());
        rvOrdersList.setAdapter(orderAdapter);

        // Configurar evento clic en el TextView de fecha para mostrar un calendario
        tvFecha.setOnClickListener(v -> showDatePicker());

        // Obtener idRestaurante desde el ViewModel
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), id -> {
            if (id != null && !id.isEmpty()) {
                idRestaurante = id;
                Log.d(TAG, "ID del restaurante recibido: " + idRestaurante); // Log del ID
                fetchOrders(); // Cargar pedidos al obtener idRestaurante
            } else {
                Log.w(TAG, "ID del restaurante es nulo o vacío"); // Log de advertencia
            }
        });

        return view;
    }

    private void fetchOrders() {
        if (idRestaurante == null) {
            Log.e(TAG, "ID del restaurante no disponible. No se pueden cargar pedidos."); // Log de error
            Toast.makeText(getContext(), "ID del restaurante no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Cargando pedidos para el restaurante con ID: " + idRestaurante); // Log antes de la consulta
        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al cargar pedidos: " + e.getMessage(), e); // Log de error
                        Toast.makeText(getContext(), "Error al cargar pedidos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Pedido pedido = doc.toObject(Pedido.class);
                            pedido.setId(doc.getId()); // Asignar ID del documento
                            orderList.add(pedido);
                            Log.d(TAG, "Pedido cargado: " + pedido.getId() + " - Fecha: " + pedido.getFechaHora());
                        }
                        filterOrdersByDate(selectedDate.getTime());
                    } else {
                        Log.w(TAG, "No se encontraron pedidos para el restaurante."); // Log de advertencia
                    }
                });
    }

    private void showDatePicker() {
        // Mostrar un DatePicker para seleccionar una fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    tvFecha.setText("Fecha: " + dateFormat.format(selectedDate.getTime()));
                    filterOrdersByDate(selectedDate.getTime());
                    Log.d(TAG, "Fecha seleccionada: " + dateFormat.format(selectedDate.getTime())); // Log de fecha seleccionada
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    private void filterOrders(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            // Si no hay búsqueda, mostrar todos los pedidos filtrados por fecha
            filterOrdersByDate(selectedDate.getTime());
        } else {
            for (Pedido pedido : orderList) {
                // Filtrar por código de pedido o nombre del cliente
                if ((pedido.getCodigo() != null && pedido.getCodigo().toLowerCase().contains(query.toLowerCase())) ||
                        (pedido.getNombreCliente() != null && pedido.getNombreCliente().toLowerCase().contains(query.toLowerCase()))) {
                    filteredList.add(pedido);
                }
            }
            orderAdapter.notifyDataSetChanged();
        }
    }

    private void filterOrdersByDate(Date date) {
        String selectedDateStr = dateFormat.format(date);
        filteredList.clear();

        // Define el formato de la fecha en Firestore
        SimpleDateFormat pedidoDateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, hh:mm:ss a", Locale.getDefault());

        for (Pedido pedido : orderList) {
            try {
                // Convierte el String de fecha del pedido a un objeto Date
                Date pedidoDate = pedidoDateFormat.parse(pedido.getFechaHora());

                // Compara solo las fechas (sin la hora)
                if (selectedDateStr.equals(dateFormat.format(pedidoDate))) {
                    filteredList.add(pedido);
                    Log.d(TAG, "Pedido filtrado: " + pedido.getId() + " - Fecha: " + pedido.getFechaHora());
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error al convertir la fecha del pedido: " + pedido.getFechaHora(), e);
            }
        }
        orderAdapter.notifyDataSetChanged();
    }
}