package pl.vvhoffmann.routemyway;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity;
import pl.vvhoffmann.routemyway.services.ToastService;

public class RouteMyWayActivity extends AppCompatActivity {

    Button btnToApp;
    TextView title;
    TextView description;
    Switch switchLocation;

    public static boolean locationEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Włączanie EdgeToEdge dla aplikacji
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_route_my_way);

        // Ustawienie paddingu w zależności od systemowych pasków
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeUIComponents();

        switchLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            locationEnabled = isChecked;
        });

        // Obsługa kliknięcia na przycisk
        btnToApp.setOnClickListener(v -> {
            ToastService.showToast("Witaj w aplikacji!!", RouteMyWayActivity.this);
            navigateToMapsActivity();
        });
    }

    private void initializeUIComponents() {
        // Inicjalizacja komponentów UI
        title = findViewById(R.id.tvTitle);
        description = findViewById(R.id.tvDesc);
        btnToApp = findViewById(R.id.btnToApp);
        switchLocation = findViewById(R.id.switchLocation);
    }

    private void navigateToMapsActivity() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
}
