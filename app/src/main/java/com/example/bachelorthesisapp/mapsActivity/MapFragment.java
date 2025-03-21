package com.example.bachelorthesisapp.mapsActivity;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.bachelorthesisapp.R;
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
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static GoogleMap mMap;
    public static HashMap<Marker, LatLng> markers = new HashMap<>();
    private FusedLocationProviderClient fusedLocationClient;
    public static Marker currentPositionMarker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Inicjalizacja Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyAysSR_bO84Y4HF7NLNwkFjpGIN1CnfMSM");  // Podstaw swój klucz API
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Inicjalizacja MapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapFragment.getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        // Inicjalizacja AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {

            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
            autocompleteFragment.setHint("Wyszukaj miejsce");

            // Create a circular bounds restriction
            if (currentPositionMarker != null)
                autocompleteFragment.setLocationRestriction(CircularBounds.newInstance(currentPositionMarker.getPosition(), 20000));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Obsługa wyboru miejsca
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    if (place.getLatLng() != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(place.getLatLng())
                                .title(place.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker());
                        Marker marker = mMap.addMarker(markerOptions);
                        markers.put(marker, new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
                    }
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Sprawdzenie i żądanie uprawnień lokalizacji
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        // Pobieranie ostatniej lokalizacji
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                currentPositionMarker = mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Twoja lokalizacja")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            } else {
                Toast.makeText(requireContext(), "Brak lokalizacji", Toast.LENGTH_SHORT).show();
            }

            for (Marker marker : markers.keySet()) {
                if (marker != currentPositionMarker) {
                    mMap.addMarker(new MarkerOptions()
                            .position(markers.get(marker))
                            .title(marker.getTitle() + " - " + Math.round(markers.get(marker).latitude) + "," + Math.round(markers.get(marker).longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker()));
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting location", e);
            Toast.makeText(requireContext(), "Nie udało się pobrać lokalizacji. Pierwszy zaznaczony przez ciebie punkt będzie twoją lokalizacją początkową.", Toast.LENGTH_SHORT).show();
        });

        // Obsługa kliknięcia na mapę
        mMap.setOnMapClickListener(latLng -> {
            String placeDescription = getPlaceDescription(latLng);
            if (currentPositionMarker == null) {
                currentPositionMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Twoja lokalizacja")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(placeDescription));
                markers.put(marker, latLng);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                onMapReady(mMap); // Re-initialize map if permissions are granted
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPlaceDescription(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        String placeDescription = "Nie znaleziono opisu miejsca";

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String characters = "abcdefghijklmnopqrstuvwxyz";
                placeDescription = !address.getFeatureName().matches(".*[" + characters + "].*") ? address.getAddressLine(0) : address.getFeatureName();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving place description", e);
        }

        return placeDescription;
    }
}
