package com.example.bachelorthesisapp.mapsActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.bachelorthesisapp.R;
import com.example.bachelorthesisapp.databinding.ActivityMapsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity {

    private ActivityMapsBinding binding;

    // Fragmenty jako singletony
    private Fragment mapFragment;
    private Fragment markersListFragment;
    private Fragment routeFragment;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicjalizacja fragmentów
        initializeFragments();

        // Ustawienie początkowego fragmentu
        if (savedInstanceState == null) {
            replaceFragment(mapFragment);
        }

        // Ustawienie słuchacza nawigacji
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // Sprawdzenie uprawnień lokalizacji
        checkLocationPermission();
    }

    private void initializeFragments() {
        mapFragment = new MapFragment();
        markersListFragment = new MarkersListFragment();
        routeFragment = new RouteFragment();
    }

    private void showFragment(Fragment selectedFragment) {
        if (selectedFragment == null || selectedFragment == getCurrentFragment()) {
            return;
        }

        executorService.execute(() -> getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commitAllowingStateLoss()); // Uruchomienie na osobnym wątku
    }

    // Sprawdzenie uprawnień lokalizacji
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // Obsługa kliknięcia elementów w dolnej nawigacji
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Fragment selectedFragment;
                switch (item.getItemId()) {
                    case R.id.nav_list:
                        selectedFragment = markersListFragment;
                        break;
                    case R.id.nav_route:
                        selectedFragment = routeFragment;
                        break;
                    case R.id.nav_map:
                    default:
                        selectedFragment = mapFragment;
                        break;
                }

                replaceFragment(selectedFragment);
                return true;
            };

    // Pomocnicza metoda, aby uniknąć powtórnych zmian na tym samym fragmencie
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Zamknięcie ExecutorService, aby uniknąć wycieków pamięci
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Pozwala wrócić do poprzedniego ekranu
                .commit();
    }
}
