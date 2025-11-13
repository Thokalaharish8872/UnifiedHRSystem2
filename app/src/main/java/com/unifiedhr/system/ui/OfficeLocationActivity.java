package com.unifiedhr.system.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.services.CompanyService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OfficeLocationActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 401;

    private TextInputLayout layoutLatitude;
    private TextInputLayout layoutLongitude;
    private TextInputLayout layoutRadius;
    private TextInputEditText etLatitude;
    private TextInputEditText etLongitude;
    private TextInputEditText etRadius;
    private TextView tvResolvedAddress;
    private Button btnUseCurrentLocation;
    private Button btnSave;

    private CompanyService companyService;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        companyId = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("companyId", "");
        companyService = new CompanyService();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupListeners();
        loadExistingLocation();
    }

    private void initViews() {
        layoutLatitude = findViewById(R.id.layoutLatitude);
        layoutLongitude = findViewById(R.id.layoutLongitude);
        layoutRadius = findViewById(R.id.layoutRadius);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etRadius = findViewById(R.id.etRadius);
        tvResolvedAddress = findViewById(R.id.tvResolvedAddress);
        btnUseCurrentLocation = findViewById(R.id.btnUseCurrentLocation);
        btnSave = findViewById(R.id.btnSaveLocation);
    }

    private void setupListeners() {
        btnUseCurrentLocation.setOnClickListener(v -> requestCurrentLocation());
        btnSave.setOnClickListener(v -> saveOfficeLocation());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutLatitude.setError(null);
                layoutLongitude.setError(null);
                layoutRadius.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                resolveAddressPreview();
            }
        };

        etLatitude.addTextChangedListener(watcher);
        etLongitude.addTextChangedListener(watcher);
        etRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutRadius.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadExistingLocation() {
        if (TextUtils.isEmpty(companyId)) {
            Toast.makeText(this, R.string.error_company_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        companyService.getCompany(companyId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null) {
                    if (company.getOfficeLatitude() != null) {
                        etLatitude.setText(String.valueOf(company.getOfficeLatitude()));
                    }
                    if (company.getOfficeLongitude() != null) {
                        etLongitude.setText(String.valueOf(company.getOfficeLongitude()));
                    }
                    if (company.getOfficeRadiusMeters() != null) {
                        etRadius.setText(String.valueOf(company.getOfficeRadiusMeters()));
                    }
                    if (TextUtils.isEmpty(Objects.requireNonNull(etRadius.getText()).toString())) {
                        etRadius.setText(String.valueOf(200));
                    }
                    if (!TextUtils.isEmpty(company.getOfficeAddress())) {
                        tvResolvedAddress.setText(company.getOfficeAddress());
                    } else {
                        resolveAddressPreview();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(OfficeLocationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestCurrentLocation() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        CancellationTokenSource cts = new CancellationTokenSource();
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fillLocationFields(location);
                    } else {
                        fusedLocationProviderClient.getLastLocation()
                                .addOnSuccessListener(this::fillLocationFields)
                                .addOnFailureListener(e -> Toast.makeText(this, R.string.error_location_unavailable, Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, R.string.error_location_unavailable, Toast.LENGTH_SHORT).show());
    }

    private void fillLocationFields(Location location) {
        if (location == null) {
            Toast.makeText(this, R.string.error_location_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        etLatitude.setText(String.format(Locale.getDefault(), "%.6f", location.getLatitude()));
        etLongitude.setText(String.format(Locale.getDefault(), "%.6f", location.getLongitude()));
        resolveAddressPreview();
    }

    private void resolveAddressPreview() {
        String latStr = Objects.requireNonNull(etLatitude.getText()).toString().trim();
        String lngStr = Objects.requireNonNull(etLongitude.getText()).toString().trim();
        if (latStr.isEmpty() || lngStr.isEmpty()) {
            tvResolvedAddress.setText(R.string.label_address_unknown);
            return;
        }

        try {
            double lat = Double.parseDouble(latStr);
            double lng = Double.parseDouble(lngStr);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    builder.append(address.getAddressLine(i));
                    if (i != address.getMaxAddressLineIndex()) {
                        builder.append(", ");
                    }
                }
                tvResolvedAddress.setText(builder.toString());
            } else {
                tvResolvedAddress.setText(R.string.label_address_unknown);
            }
        } catch (NumberFormatException | IOException e) {
            tvResolvedAddress.setText(R.string.label_address_unknown);
        }
    }

    private void saveOfficeLocation() {
        String latStr = Objects.requireNonNull(etLatitude.getText()).toString().trim();
        String lngStr = Objects.requireNonNull(etLongitude.getText()).toString().trim();
        String radiusStr = Objects.requireNonNull(etRadius.getText()).toString().trim();

        if (latStr.isEmpty()) {
            layoutLatitude.setError(getString(R.string.error_latitude_required));
            return;
        }

        if (lngStr.isEmpty()) {
            layoutLongitude.setError(getString(R.string.error_longitude_required));
            return;
        }

        if (radiusStr.isEmpty()) {
            layoutRadius.setError(getString(R.string.error_radius_required));
            return;
        }

        try {
            double latitude = Double.parseDouble(latStr);
            double longitude = Double.parseDouble(lngStr);
            int radius = Integer.parseInt(radiusStr);
            if (radius < 50) {
                layoutRadius.setError(getString(R.string.error_radius_too_small));
                return;
            }
            if (radius > 2000) {
                layoutRadius.setError(getString(R.string.error_radius_too_large));
                return;
            }

            String address = tvResolvedAddress.getText() != null ? tvResolvedAddress.getText().toString() : null;
            companyService.updateOfficeLocation(companyId, latitude, longitude, radius,
                    TextUtils.equals(address, getString(R.string.label_address_unknown)) ? null : address,
                    (error, ref) -> {
                        if (error == null) {
                            Toast.makeText(this, R.string.toast_office_location_saved, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.error_invalid_coordinates, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCurrentLocation();
            } else {
                Toast.makeText(this, R.string.error_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

