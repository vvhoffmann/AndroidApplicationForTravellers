package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static android.content.ContentValues.TAG;

import static pl.vvhoffmann.routemyway.repositories.MarkersRepository.getCurrentPositionMarker;
import static pl.vvhoffmann.routemyway.repositories.MarkersRepository.getMarkerByLatLng;

import android.Manifest;
import android.annotation.SuppressLint;
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
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;

    private Button btnRemoveMarker;
    private Button btnRemoveMarker2;
    private Button btnShowRoute;

    private Geocoder geocoder;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view) {

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        btnRemoveMarker = view.findViewById(R.id.btnDeleteMarker);
        btnRemoveMarker.setVisibility(View.INVISIBLE);

        btnRemoveMarker2 = view.findViewById(R.id.btnDeleteMarker2);
        btnRemoveMarker2.setVisibility(View.INVISIBLE);

        btnShowRoute = view.findViewById(R.id.btnShowRoute);
        btnShowRoute.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        MapService.setMap(googleMap);

        refreshMapMarkers(geocoder);

        MapService.getMap().getUiSettings().setZoomControlsEnabled(true);


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
                            btnRemoveMarker.setVisibility(View.INVISIBLE);
                            btnRemoveMarker2.setVisibility(View.INVISIBLE);
                            // Obsługa wyboru miejsca
                            MapService.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Objects.requireNonNull(place.getLatLng()), 15));
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(place.getLatLng())
                                    .title(PlacesUtils.getPlaceDescription(place.getLatLng(), geocoder))
                                    .icon(BitmapDescriptorFactory.defaultMarker());
                            Marker marker = MapService.getMap().addMarker(markerOptions);
                            assert marker != null;
                            MarkersRepository.addMarker(marker);
                        }
                        @Override
                        public void onError(@NonNull Status status) {
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });
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
            btnRemoveMarker.setVisibility(View.INVISIBLE);
            btnRemoveMarker2.setVisibility(View.VISIBLE);

            btnRemoveMarker2.setOnClickListener(v -> {
                Marker markerToRemove = getMarkerByLatLng(latLng);
                Log.i("Marker to remove", "Marker to remove: " + markerToRemove.getPosition());
                if (markerToRemove != null) {
                    MarkersRepository.removeMarker(markerToRemove);
                    markerToRemove.remove();
                    Log.i("Markers after remove", "Markers: " + MarkersRepository.getDescription());
                    btnRemoveMarker2.setVisibility(View.INVISIBLE);
                }
            });
        });


        MapService.getMap().setOnMarkerClickListener(marker -> {
            Log.i(TAG, "Size: " + MarkersRepository.getLatLngList().size() + " " + MarkersRepository.getSize());
            marker.showInfoWindow();
            btnRemoveMarker2.setVisibility(View.INVISIBLE);
            btnRemoveMarker.setVisibility(View.VISIBLE);
            Log.i("Marker clicked", "Marker clicked: " + marker.getTitle() + " " + marker.getPosition());

            btnRemoveMarker.setOnClickListener(v -> {
                Log.i("Marker to remove", "Marker to remove: " + marker.getTitle() + "  "+ marker.getPosition());
                MarkersRepository.removeMarker(marker);
                marker.remove();

                Log.i("Markers after remove", "Markers: " + MarkersRepository.getDescription());
                btnRemoveMarker.setVisibility(View.INVISIBLE);
            });

            return true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if(RouteRepository.isRouteCalculated()){
            getRouteFromDirectionsAPI();
            Log.i(TAG, "onResume: Route drawn");
        }
        else{
            refreshMapMarkers(geocoder);
            Log.i(TAG, "onResume: Markers refreshed");
        }

    }

    private void getRouteFromDirectionsAPI() {
        String urlString = MapService.getRouteUrl();

        // Wykonanie zapytania w osobnym wątku
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parsowanie odpowiedzi JSON
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray routes = jsonResponse.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String encodedPolyline = overviewPolyline.getString("points");

                    // Dekodowanie Polyline i rysowanie trasy
                    List<LatLng> polylinePoints = decodePolyline(encodedPolyline);
                    requireActivity().runOnUiThread(() -> drawRoute(polylinePoints));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawRoute(List<LatLng> polylinePoints) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(polylinePoints)
                .clickable(true)
                .color(com.google.android.libraries.places.R.color.quantum_googblue);

        Polyline polyline = MapService.getMap().addPolyline(polylineOptions);

        // Ustawienie widoku kamery
        if (!polylinePoints.isEmpty())
            MapService.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(polylinePoints.get(0), 6));
    }

    // Metoda do dekodowania Polyline z Directions API
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            polyline.add(p);
        }

        return polyline;
    }


    private static void refreshMapMarkers(Geocoder geocoder) {
        if(MapService.getMap() != null)
            MapService.getMap().clear();

        for (LatLng latLng : MarkersRepository.getLatLngList()) {
            if (latLng.equals(getCurrentPositionMarker().getPosition()))
                continue;

            MapService.getMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(PlacesUtils.getPlaceDescription(latLng, geocoder))
                    .icon(BitmapDescriptorFactory.defaultMarker()));
        }
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