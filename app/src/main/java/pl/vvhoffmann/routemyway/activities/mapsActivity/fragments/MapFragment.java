package pl.vvhoffmann.routemyway.activities.mapsActivity.fragments;

import static android.content.ContentValues.TAG;
import static pl.vvhoffmann.routemyway.activities.mapsActivity.MapsActivity.getMarkersRepository;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.net.Uri;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pl.vvhoffmann.routemyway.R;
import pl.vvhoffmann.routemyway.RouteMyWayActivity;
import pl.vvhoffmann.routemyway.config.AppConfig;
import pl.vvhoffmann.routemyway.constants.Messages;
import pl.vvhoffmann.routemyway.repositories.RouteRepository;
import pl.vvhoffmann.routemyway.services.MapService;
import pl.vvhoffmann.routemyway.utils.PlacesUtils;
import pl.vvhoffmann.routemyway.utils.WalkingRouteHttpClient;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private AutocompleteSupportFragment autocompleteFragment;
    private Button btnRemoveMarker;
    private Button btnRedirectWithGoogleMaps;

    private Geocoder geocoder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicjalizacja klienta lokalizacji
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Inicjalizacja Places API
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(requireContext(), AppConfig.GOOGLE_MAPS_API_KEY);
            Places.createClient(requireContext());
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
        btnRedirectWithGoogleMaps = view.findViewById(R.id.btnRedirectToGoogleMaps);
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        geocoder = new Geocoder(requireContext(), Locale.getDefault());

        MapService.setMap(googleMap);
        refreshMapMarkers(geocoder);
        getMap().getUiSettings().setZoomControlsEnabled(true);

        if (RouteMyWayActivity.locationEnabled) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            getMap().setMyLocationEnabled(true);

            // Pobieranie ostatniej lokalizacji
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null && getMap() != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    Marker currentPositionMarker = addMarkerToMap(new MarkerOptions()
                            .position(currentLatLng)
                            .title("Twoja lokalizacja - " + PlacesUtils.getPlaceDescription(currentLatLng, geocoder))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    if (getMarkersRepository().getCurrentPositionMarker() == null)
                        getMarkersRepository().setCurrentPositionMarker(currentPositionMarker);

                    getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                } else {
                    Toast.makeText(requireContext(), Messages.NOT_UPLOADED_LOCATION_INFO_MESSAGE, Toast.LENGTH_SHORT).show();
                }

                if (autocompleteFragment != null) {
                    autoCompleteSetup();
                    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(@NonNull Place place) {
                            btnRemoveMarker.setVisibility(View.INVISIBLE);
                            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Objects.requireNonNull(place.getLatLng()), 15));
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(place.getLatLng())
                                    .title(PlacesUtils.getPlaceDescription(place.getLatLng(), geocoder))
                                    .icon(BitmapDescriptorFactory.defaultMarker());
                            Marker marker = addMarkerToMap(markerOptions);
                            assert marker != null;
                            getMarkersRepository().addMarker(marker);
                        }
                        @Override
                        public void onError(@NonNull Status status) {
                            Log.i(TAG, "An error occurred: " + status);
                        }
                    });
                }

            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error getting location", e);
                Toast.makeText(requireContext(), Messages.NOT_UPLOADED_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exc) {
                    throw new RuntimeException(exc);
                }
                Toast.makeText(requireContext(), Messages.NOT_UPLOADED_LOCATION_INFO_MESSAGE, Toast.LENGTH_LONG).show();
            });
        } else {
            Toast.makeText(requireContext(), Messages.NOT_SHARED_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(requireContext(), Messages.NOT_UPLOADED_LOCATION_INFO_MESSAGE, Toast.LENGTH_LONG).show();
        }

        // Obsługa kliknięcia na mapę
        getMap().setOnMapClickListener(latLng -> {
            if (Math.abs(latLng.latitude - getMarkersRepository().getCurrentPositionMarker().getPosition().latitude) > 0.2 ||
                    Math.abs(latLng.longitude - getMarkersRepository().getCurrentPositionMarker().getPosition().longitude) > 0.2) {
                Toast.makeText(getContext(), Messages.TOO_FUTHER_LOCATION_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                String placeDescription = PlacesUtils.getPlaceDescription(latLng, geocoder);
                if (getMarkersRepository().getCurrentPositionMarker() == null) {
                    getMarkersRepository().addMarker(addMarkerToMap(new MarkerOptions()
                            .position(latLng)
                            .title("Twoja lokalizacja -" + placeDescription)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
                } else {
                    Marker marker = addMarkerToMap(new MarkerOptions()
                            .position(latLng)
                            .title(placeDescription));

                    getMarkersRepository().addMarker(marker);
                }
            }
            btnRemoveMarker.setVisibility(View.INVISIBLE);
        });


        getMap().setOnMarkerClickListener(marker -> {
            Log.i(TAG, "Size: " + getMarkersRepository().getLatLngList().size() + " " + getMarkersRepository().getSize());
            marker.showInfoWindow();
            btnRemoveMarker.setVisibility(View.VISIBLE);
            Log.i("Marker clicked", "Marker clicked: " + marker.getTitle() + " " + marker.getPosition());

            btnRemoveMarker.setOnClickListener(v -> {
                Log.i("Marker to remove", "Marker to remove: " + marker.getTitle() + "  "+ marker.getPosition());
                Marker selectedMarker = getMarkersRepository().getMarkerByLatLng(marker.getPosition());
                getMarkersRepository().removeMarker(selectedMarker);

                marker.remove();

                Log.i("Markers after remove", "Markers: " + getMarkersRepository().getDescription());
                btnRemoveMarker.setVisibility(View.INVISIBLE);
            });

            return true;
        });
    }

    @Nullable
    private static Marker addMarkerToMap(MarkerOptions markerOptions) {
        return getMap().addMarker(markerOptions);
    }

    private void autoCompleteSetup() {
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setHint(Messages.AUTOCOMPLETE_HINT_MESSAGE);
        autocompleteFragment.setLocationRestriction(getRectangularBounds());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if(RouteRepository.getInstance().isRouteCalculated()){
            displayRoute();
        }
        else{
            refreshMapMarkers(geocoder);
            Log.i(TAG, "onResume: Markers refreshed");
        }
    }

    private void displayRoute() {
        drawRouteOnMapByRoutesAPIResponse();
        btnRemoveMarker.setVisibility(View.INVISIBLE);
        btnRedirectWithGoogleMaps.setVisibility(View.VISIBLE);

        btnRedirectWithGoogleMaps.setOnClickListener(v -> {
            String url = MapService.getGoogleMapsRedirectUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        });
        Toast.makeText(requireContext(), "Twoja trasa wynikowa", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onResume: Route drawn");
    }

    private void drawRouteOnMapByRoutesAPIResponse() {
        String routeUrl = MapService.getRouteUrl();

        new Thread(() -> {
            try {
                String httpResponse = WalkingRouteHttpClient.getHttpResponse(routeUrl);
                List<LatLng> polylinePoints = WalkingRouteHttpClient.getPolylinePoints(httpResponse);
                if(!polylinePoints.isEmpty())
                    requireActivity().runOnUiThread(() -> drawRoute(polylinePoints));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawRoute(List<LatLng> polylinePoints) {
        if (!polylinePoints.isEmpty())
        {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(polylinePoints)
                    .clickable(true)
                    .color(Color.GREEN);

            getMap().addPolyline(polylineOptions);

            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(polylinePoints.get(0), 15));
        }
    }

    public static void refreshMapMarkers(Geocoder geocoder) {
        if(getMap() != null)
            getMap().clear();

        for (LatLng latLng : getMarkersRepository().getLatLngList()) {
            if (latLng.equals(getMarkersRepository().getCurrentPositionMarker().getPosition()))
                continue;

            addMarkerToMap(new MarkerOptions()
                    .position(latLng)
                    .title(PlacesUtils.getPlaceDescription(latLng, geocoder))
                    .icon(BitmapDescriptorFactory.defaultMarker()));
        }
    }

    private static GoogleMap getMap() {
        return MapService.getMap();
    }

    @NonNull
    private static RectangularBounds getRectangularBounds() {
        Marker currentPositionMarker = getMarkersRepository().getCurrentPositionMarker();
        LatLng southwestRestriction = new LatLng(currentPositionMarker.getPosition().latitude - 0.2, currentPositionMarker.getPosition().longitude - 0.2);
        LatLng northeastRestriction = new LatLng(currentPositionMarker.getPosition().latitude + 0.2, currentPositionMarker.getPosition().longitude + 0.2);
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
            if (getMap() != null) {
                onMapReady(getMap());
            }
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}