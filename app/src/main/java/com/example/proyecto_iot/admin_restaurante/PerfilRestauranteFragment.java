package com.example.proyecto_iot.admin_restaurante;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.LoginActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.admin_restaurante.RecyclerView.RestauranteViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilRestauranteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilRestauranteFragment extends Fragment {

    private RestauranteViewModel restauranteViewModel;
    private FirebaseFirestore db;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilRestauranteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilRestauranteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilRestauranteFragment newInstance(String param1, String param2) {
        PerfilRestauranteFragment fragment = new PerfilRestauranteFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_restaurante, container, false);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();

        // Obtén el ViewModel compartido
        restauranteViewModel = new ViewModelProvider(requireActivity()).get(RestauranteViewModel.class);


        // Enlazar los botones para editar datos personales y del restaurante
        LinearLayout personalInfoLayout = view.findViewById(R.id.edit_personal_info);
        LinearLayout restaurantInfoLayout = view.findViewById(R.id.edit_restaurant_info);
        LinearLayout scheduleLayout = view.findViewById(R.id.view_schedule);

        personalInfoLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), EditPersonalInfoActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        restaurantInfoLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), EditRestaurantInfoActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        scheduleLayout.setOnClickListener(v -> {
            restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
                if (idRestaurante != null) {
                    Intent intent = new Intent(getContext(), ViewRestaurantScheduleActivity.class);
                    intent.putExtra("idRestaurante", idRestaurante);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "No se pudo obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Listener para cerrar sesión
        LinearLayout logoutLayout = view.findViewById(R.id.logout_layout);
        logoutLayout.setOnClickListener(v -> showCustomLogoutDialog());

        return view;
    }


    private void attemptLogout() {
        restauranteViewModel.getIdRestaurante().observe(getViewLifecycleOwner(), idRestaurante -> {
            if (idRestaurante != null) {
                // Validar pedidos pendientes antes de cerrar sesión
                validatePendingOrders(idRestaurante);
            } else {
                Toast.makeText(getContext(), "Error al obtener el ID del restaurante.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validatePendingOrders(String idRestaurante) {
        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereIn("estado", Arrays.asList(1, 2, 3))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Mostrar alerta si hay pedidos en estado 1, 2 o 3
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Pedidos en proceso")
                                .setMessage("No puedes cerrar el restaurante porque aún hay pedidos en proceso.")
                                .setPositiveButton("Aceptar", null)
                                .show();
                    } else {
                        // Si no hay pedidos pendientes, manejar pedidos en estado 0
                        handleStateZeroOrders(idRestaurante);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al validar pedidos pendientes.", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleStateZeroOrders(String idRestaurante) {
        db.collection("pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .whereEqualTo("estado", 0)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        DocumentReference pedidoRef = doc.getReference();
                        batch.update(pedidoRef, "estado", 5); // Actualizar estado a 5
                    }

                    // Confirmar cierre de sesión
                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                updateRestaurantOpenStatus(idRestaurante);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al actualizar pedidos.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al manejar pedidos en estado 0.", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateRestaurantOpenStatus(String idRestaurante) {
        db.collection("restaurantes").document(idRestaurante)
                .update("open", false)
                .addOnSuccessListener(aVoid -> {
                    FirebaseAuth.getInstance().signOut(); // Cierra sesión
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish(); // Finalizar la actividad actual
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al actualizar el estado del restaurante.", Toast.LENGTH_SHORT).show();
                });
    }


    private void showCustomLogoutDialog() {
        // Crear el diálogo personalizado
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_logout_confirmation);

        // Inicializar vistas del layout personalizado
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView message = dialog.findViewById(R.id.dialog_message);
        Button positiveButton = dialog.findViewById(R.id.dialog_positive_button);
        Button negativeButton = dialog.findViewById(R.id.dialog_negative_button);

        // Configurar el botón "Sí"
        positiveButton.setOnClickListener(v -> {
            attemptLogout(); // Lógica de validación y cierre
            dialog.dismiss();
        });

        // Configurar el botón "No"
        negativeButton.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();

        // Ajustar tamaño del diálogo
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

}