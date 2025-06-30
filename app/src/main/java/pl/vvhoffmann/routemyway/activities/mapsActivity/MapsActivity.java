package pl.vvhoffmann.routemyway.activities.mapsActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.InformationFragment;
import pl.vvhoffmann.routemyway.databinding.ActivityMapsBinding;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MapFragment;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.RouteFragment;
import pl.vvhoffmann.routemyway.activities.mapsActivity.fragments.MarkersListFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity {

    private ActivityMapsBinding binding;

    private static Fragment mapFragment;
    private static Fragment markersListFragment;
    private static Fragment routeFragment;
    private static Fragment infoFragment;

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

    public void initializeFragments() {
        mapFragment = new MapFragment();
        markersListFragment = new MarkersListFragment();
        routeFragment = new RouteFragment();
        infoFragment = new InformationFragment();
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

    // Pomocnicza metoda, aby uniknąć powtórnych zmian na tym samym fragmencie
    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

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
                    case R.id.nav_info:
                        selectedFragment = infoFragment;
                        break;
                    case R.id.nav_map:
                    default:
                        selectedFragment = mapFragment;
                        break;
                }

                replaceFragment(selectedFragment);
                return true;
            };

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

    public static Fragment getMarkersListFragment() {
        return markersListFragment;
    }

    public static Fragment getMapFragment() {
        return mapFragment;
    }


}
