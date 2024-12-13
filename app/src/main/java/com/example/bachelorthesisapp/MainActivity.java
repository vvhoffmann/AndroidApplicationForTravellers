package com.example.bachelorthesisapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button btnToApp;
    TextView title;
    TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Włączanie EdgeToEdge dla aplikacji
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ustawienie paddingu w zależności od systemowych pasków
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicjalizacja komponentów UI
        title = findViewById(R.id.tvTitle);
        description = findViewById(R.id.tvDescription);
        btnToApp = findViewById(R.id.btnToApp);

        // Obsługa kliknięcia na przycisk
        btnToApp.setOnClickListener(v -> {
            // Wyświetlenie komunikatu powitalnego
            showToast("Welcome to the application!!");

            // Uruchomienie nowej aktywności
            navigateToMapsActivity();
        });
    }

    // Funkcja do wyświetlania toastów
    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // Funkcja do przejścia do MapsActivity
    private void navigateToMapsActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
}
