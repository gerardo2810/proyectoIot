package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.RecyclerView.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RealizarPedidoActivity extends AppCompatActivity {

    private ImageView backArrow;
    private Button payButton;
    private TextView seeMore;
    private List<Producto> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_realizar_pedido_cliente);

        // Configurar el padding para ajustar a las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recibir los datos del Intent
        Intent intent = getIntent();
        double subtotal = intent.getDoubleExtra("subtotal", 0.0);
        double precioDelivery = intent.getDoubleExtra("precio_delivery", 0.0);
        String nombreRestaurante = intent.getStringExtra("nombreRestaurante");
        String fotoLogo = intent.getStringExtra("fotoLogo");
        int cantidadProductos = intent.getIntExtra("cantidadProductos", 0);
        String restauranteId = getIntent().getStringExtra("restauranteId");
        productos = (List<Producto>) getIntent().getSerializableExtra("carrito");

        // Mostrar los valores en los TextViews
        double pagoTotal = subtotal + precioDelivery;
        String temp = "Productos - " + cantidadProductos;
        TextView costosProductosTextView = findViewById(R.id.costos_productos_value);
        TextView envioTextView = findViewById(R.id.envio_value);
        TextView pagoTotalTextView = findViewById(R.id.pago_total_value);
        TextView nameRestauranteTextView = findViewById(R.id.restaurant_name1);
        TextView subtotalTextView = findViewById(R.id.subtotal_value);
        TextView cantidadTextView = findViewById(R.id.products_count);
        ImageView fotoLogoImageView = findViewById(R.id.profile_image);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
        costosProductosTextView.setText(currencyFormat.format(subtotal));
        envioTextView.setText(currencyFormat.format(precioDelivery));
        pagoTotalTextView.setText(currencyFormat.format(pagoTotal));
        subtotalTextView.setText(currencyFormat.format(pagoTotal));
        nameRestauranteTextView.setText(nombreRestaurante);
        cantidadTextView.setText(String.valueOf(temp));

        Glide.with(this)
                .load(fotoLogo)
                .placeholder(R.drawable.placeholder)
                .into(fotoLogoImageView);

        backArrow = findViewById(R.id.back_arrow);
        payButton = findViewById(R.id.pay_button);
        seeMore = findViewById(R.id.see_more);

        backArrow.setOnClickListener(view -> {
            finish(); // Regresa automáticamente a CarritoClienteActivity sin crear una nueva instancia
        });


        seeMore.setOnClickListener(v -> {
            // Crear el Intent para navegar a VerMasProductosClienteActivity
            Intent intent1 = new Intent(RealizarPedidoActivity.this, VerMasProductosClienteActivity.class);

            // Pasar el nombre del restaurante
            intent1.putExtra("nombreRestaurante", nombreRestaurante);

            // Pasar el restauranteId
            intent1.putExtra("restauranteId", restauranteId);

            // Convertir la lista de productos en un ArrayList
            ArrayList<Producto> carrito = new ArrayList<>(productos);

            // Pasar la lista de productos
            intent1.putExtra("carrito", carrito);

            // Pasar el tamaño de la lista
            intent1.putExtra("carritoSize", carrito.size());

            // Iniciar la nueva actividad
            startActivity(intent1);
        });


        payButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db.collection("clientes").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String direccionCliente = documentSnapshot.getString("Direccion");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss a", new Locale("es", "PE"));
                            dateFormat.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                            String fechaHora = dateFormat.format(new Date());

                            // Crear el mapa de datos del pedido
                            Map<String, Object> pedidoData = new HashMap<>();
                            pedidoData.put("idCliente", userId);
                            pedidoData.put("direccion", direccionCliente);
                            pedidoData.put("estado", 5);
                            pedidoData.put("fechaHora", fechaHora);
                            pedidoData.put("productos", productos); // Lista de productos
                            pedidoData.put("idRepartidor", "");
                            pedidoData.put("idRestaurante", restauranteId);
                            pedidoData.put("nombreRestaurante", nombreRestaurante);
                            pedidoData.put("pagoTotal", pagoTotal);

                            db.collection("pedidos")
                                    .add(pedidoData)
                                    .addOnSuccessListener(documentReference -> {
                                        String pedidoId = documentReference.getId();
                                        Bitmap qrBitmap = generarQRCode(pedidoId);
                                        guardarQRCodeEnStorage(pedidoId, qrBitmap);

                                        // Enviar los datos a la siguiente vista
                                        Intent intent1 = new Intent(RealizarPedidoActivity.this, SeguimientoPedidoActivity.class);
                                        intent1.putExtra("pedidoId", pedidoId);
                                        intent1.putExtra("direccion", direccionCliente);
                                        intent1.putExtra("fechaHora", fechaHora);
                                        intent1.putExtra("subtotal",subtotal);
                                        intent1.putExtra("precioTotal", pagoTotal);
                                        intent1.putExtra("precioDelivery", precioDelivery);
                                        intent1.putExtra("productos", (ArrayList<Producto>) productos); // Enviar productos como ArrayList
                                        intent1.putExtra("nombreRestaurante", nombreRestaurante);
                                        intent1.putExtra("idRestaurante", restauranteId);

                                        startActivity(intent1);

                                    })
                                    .addOnFailureListener(e -> {
                                        System.err.println("Error al crear el pedido: " + e.getMessage());
                                    });
                        } else {
                            System.err.println("Cliente no encontrado en la base de datos.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error al obtener los datos del cliente: " + e.getMessage());
                    });
        });


    }

    private void guardarQRCodeEnStorage(String pedidoId, Bitmap qrBitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("qrCodes/" + pedidoId + ".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] qrData = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(qrData);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String qrUrl = uri.toString();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("pedidos").document(pedidoId)
                    .update("qrUrl", qrUrl)
                    .addOnSuccessListener(aVoid -> System.out.println("QR guardado en Firestore: " + qrUrl))
                    .addOnFailureListener(e -> System.err.println("Error al guardar el QR en Firestore: " + e.getMessage()));
        }).addOnFailureListener(e -> System.err.println("Error al obtener el enlace del QR: " + e.getMessage())));
    }

    private Bitmap generarQRCode(String data) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            com.google.zxing.common.BitMatrix matrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);
            return toBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Bitmap toBitmap(com.google.zxing.common.BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
            }
        }
        return bitmap;
    }
}
