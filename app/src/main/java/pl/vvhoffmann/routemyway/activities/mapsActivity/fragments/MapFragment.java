package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static android.content.ContentValues.TAG;

import static pl.vvhoffmann.routemyway.repositories.MarkersRepository.getCurrentPositionMarker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.RouteMyWayActivity;
import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.constants.Constants;
import pl.vvhoffmann.routemyway.repositories.MarkersRepository;
import pl.vvhoffmann.routemyway.services.MapService;
import pl.vvhoffmann.routemyway.utils.PlacesUtils;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;

    private Button btnRemoveMarker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Inicjalizacja Places API
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(requireContext(), AppConfig.GOOGLE_MAPS_API_KEY);
            placesClient = Places.createClient(getContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initializeComponents(view);

        btnRemoveMarker.setOnClickListener(v -> {

            btnRemoveMarker.setVisibility(View.INVISIBLE); // co usuwa ????????
        });

        return view;
    }

    private void initializeComponents(View view) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapFragment.getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        btnRemoveMarker = view.findViewById(R.id.btnDeleteMarker);
        btnRemoveMarker.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapService.setMap(googleMap);
        MapService.getMap().getUiSettings().setZoomControlsEnabled(true);

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

        if (RouteMyWayActivity.locationEnabled) {
            // Sprawdzenie i żądanie uprawnień lokalizacji
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            MapService.getMap().setMyLocationEnabled(true);

            // Pobieranie ostatniej lokalizacji
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && MapService.getMap() != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    Marker currentPositionMarker = MapService.getMap().addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .title("Twoja lokalizacja - " + PlacesUtils.getPlaceDescription(currentLatLng, geocoder))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    if (MarkersRepository.getCurrentPositionMarker() == null)
                        MarkersRepository.setCurrentPositionMarker(currentPositionMarker);

                    MapService.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                } else {
                    Toast.makeText(requireContext(), "Brak lokalizacji", Toast.LENGTH_SHORT).show();
                }

                final FindAutocompletePredictionsRequest autocompletePlacesRequest =
                        FindAutocompletePredictionsRequest.builder()
                                .setLocationRestriction(getRectangularBounds())
                                .setOrigin(getCurrentPositionMarker().getPosition())
                                .build();
                placesClient.findAutocompletePredictions(autocompletePlacesRequest).addOnSuccessListener((response) -> {
                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i(TAG, "PREDI" + prediction.getPlaceId());
                        Log.i(TAG, prediction.getPrimaryText(null).toString());
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });

                if (autocompleteFragment != null) {

                    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
                    autocompleteFragment.setHint("Wyszukaj miejsce");

                    autocompleteFragment.setLocationRestriction(getRectangularBounds());

                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(@NonNull Place place) {
                            // Obsługa wyboru miejsca
                            Log.i(TAG, "MIEJSCE: " + place.getName() + ", " + place.getId());
                            MapService.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(place.getLatLng())
                                    .title(PlacesUtils.getPlaceDescription(place.getLatLng(), geocoder))
                                    .icon(BitmapDescriptorFactory.defaultMarker());
                            Marker marker = MapService.getMap().addMarker(markerOptions);
                            assert marker != null;
                            MarkersRepository.addMarker(marker);
                            Log.i("Markers after add", "Markers: " + MarkersRepository.getLatLngList());
                        }

                        @Override
                        public void onError(@NonNull Status status) {
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });
                }

                for (LatLng latLng : MarkersRepository.getLatLngList()) {
                    if (latLng.equals(getCurrentPositionMarker().getPosition()))
                        continue;

                    MapService.getMap().addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(PlacesUtils.getPlaceDescription(latLng, geocoder))
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting location", e);
                Toast.makeText(requireContext(), "Nie udało się pobrać lokalizacji.", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                Toast.makeText(requireContext(), Constants.NOT_UPLOADED_LOCATION_INFO_MESSAGE, Toast.LENGTH_LONG).show();
            });
        } else {
            Toast.makeText(requireContext(), "Nie udostępniono lokalizacji.", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(requireContext(), Constants.NOT_UPLOADED_LOCATION_INFO_MESSAGE, Toast.LENGTH_LONG).show();
        }

        // Obsługa kliknięcia na mapę
        MapService.getMap().setOnMapClickListener(latLng -> {
            if (Math.abs(latLng.latitude - getCurrentPositionMarker().getPosition().latitude) > 0.2 ||
                    Math.abs(latLng.longitude - getCurrentPositionMarker().getPosition().longitude) > 0.2) {
                Toast.makeText(getContext(), Constants.TOO_FUTHER_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                String placeDescription = PlacesUtils.getPlaceDescription(latLng, geocoder);
                if (getCurrentPositionMarker() == null) {
                    MarkersRepository.addMarker(MapService.getMap().addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Twoja lokalizacja -" + placeDescription)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
                    Log.i("Markers add after click", "Markers a click: " + MarkersRepository.getLatLngList());
                } else {
                    Marker marker = MapService.getMap().addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(placeDescription));
                    MarkersRepository.addMarker(marker);
                }
            }
            btnRemoveMarker.setVisibility(View.VISIBLE);
        });
    }

    @NonNull
    private static RectangularBounds getRectangularBounds() {
        LatLng southwestRestriction = new LatLng(getCurrentPositionMarker().getPosition().latitude - 0.2, getCurrentPositionMarker().getPosition().longitude - 0.2); // Lewy dolny róg
        LatLng northeastRestriction = new LatLng(getCurrentPositionMarker().getPosition().latitude + 0.2, getCurrentPositionMarker().getPosition().longitude + 0.2); // Prawy górny róg

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(southwestRestriction)
                .include(northeastRestriction)
                .build();

        return RectangularBounds.newInstance(bounds);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (MapService.getMap() != null) {
                onMapReady(MapService.getMap()); // Re-initialize map if permissions are granted
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}