package com.unifiedhr.system.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.database.DatabaseError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unifiedhr.system.R;
import com.unifiedhr.system.models.Attendance;
import com.unifiedhr.system.models.Company;
import com.unifiedhr.system.services.AttendanceService;
import com.unifiedhr.system.services.CompanyService;
import com.unifiedhr.system.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class AttendanceActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_OFFICE_CHECK_IN = 101;
    private static final int REQUEST_LOCATION_OFFICE_CHECK_OUT = 102;

    private TextView tvStatus, tvLocation, tvDate;
    private Button btnCheckIn, btnCheckOut, btnWFHCheckIn, btnWFHCheckOut;
    private FusedLocationProviderClient fusedLocationClient;
    private AttendanceService attendanceService;
    private CompanyService companyService;
    private String employeeId;
    private String companyId;
    private String todayDate;
    private boolean isCheckedIn = false;
    private boolean isWFHCheckedIn = false;
    private Attendance currentAttendance;
    private boolean officeLocationConfigured = false;
    private double officeLatitude;
    private double officeLongitude;
    private int officeRadiusMeters = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        employeeId = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("employeeId", "");
        companyId = getSharedPreferences("UnifiedHR", MODE_PRIVATE).getString("companyId", "");
        attendanceService = new AttendanceService();
        companyService = new CompanyService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        todayDate = sdf.format(new Date());

        initViews();
        loadCompanyLocation();
        checkTodayAttendance();
    }

    private void initViews() {
        tvStatus = findViewById(R.id.tvStatus);
        tvLocation = findViewById(R.id.tvLocation);
        tvDate = findViewById(R.id.tvDate);
        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnWFHCheckIn = findViewById(R.id.btnWFHCheckIn);
        btnWFHCheckOut = findViewById(R.id.btnWFHCheckOut);

        tvDate.setText("Date: " + todayDate);

        btnCheckIn.setOnClickListener(v -> checkIn("WFO"));
        btnCheckOut.setOnClickListener(v -> checkOut("WFO"));
        btnWFHCheckIn.setOnClickListener(v -> checkIn("WFH"));
        btnWFHCheckOut.setOnClickListener(v -> checkOut("WFH"));
    }

    private void loadCompanyLocation() {
        if (TextUtils.isEmpty(companyId)) {
            officeLocationConfigured = false;
            return;
        }

        companyService.getCompany(companyId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Company company = snapshot.getValue(Company.class);
                if (company != null && company.getOfficeLatitude() != null && company.getOfficeLongitude() != null) {
                    officeLatitude = company.getOfficeLatitude();
                    officeLongitude = company.getOfficeLongitude();
                    if (company.getOfficeRadiusMeters() != null) {
                        officeRadiusMeters = company.getOfficeRadiusMeters();
                    }
                    officeLocationConfigured = true;
                } else {
                    officeLocationConfigured = false;
                }
                applyAttendanceState(currentAttendance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                officeLocationConfigured = false;
                applyAttendanceState(currentAttendance);
            }
        });
    }

    private void checkTodayAttendance() {
        attendanceService.getAttendanceByEmployeeAndDate(employeeId, todayDate)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                        Attendance attendance = null;
                        if (snapshot.exists()) {
                            for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                                Attendance value = child.getValue(Attendance.class);
                                if (value != null && todayDate.equals(value.getDate())) {
                                    attendance = value;
                                    break;
                                }
                            }
                        }
                        applyAttendanceState(attendance);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void checkIn(String attendanceType) {
        if ("WFH".equals(attendanceType)) {
            handleWFHCheckIn();
        } else {
            handleOfficeCheckIn();
        }
    }

    private void checkOut(String attendanceType) {
        if ("WFH".equals(attendanceType)) {
            handleWFHCheckOut();
        } else {
            handleOfficeCheckOut();
        }
    }

    private void handleOfficeCheckIn() {
        if (isCheckedIn) {
            Toast.makeText(this, getString(R.string.toast_already_checked_in), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!officeLocationConfigured) {
            Toast.makeText(this, getString(R.string.error_office_location_not_set), Toast.LENGTH_LONG).show();
            return;
        }

        if (!hasLocationPermission()) {
            requestLocationPermission(REQUEST_LOCATION_OFFICE_CHECK_IN);
            return;
        }

        captureLocation(location -> {
            if (!isWithinOfficeRadius(location)) {
                Toast.makeText(this, getString(R.string.error_outside_office_radius), Toast.LENGTH_LONG).show();
                return;
            }
            String locationLabel = formatLocationLabel(getString(R.string.label_office_location), location);
            markAttendance("WFO", locationLabel);
        }, this::showOfficeLocationUnavailable);
    }

    private void handleOfficeCheckOut() {
        if (!isCheckedIn || isWFHCheckedIn) {
            Toast.makeText(this, getString(R.string.toast_office_checkin_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!officeLocationConfigured) {
            Toast.makeText(this, getString(R.string.error_office_location_not_set), Toast.LENGTH_LONG).show();
            return;
        }

        if (!hasLocationPermission()) {
            requestLocationPermission(REQUEST_LOCATION_OFFICE_CHECK_OUT);
            return;
        }

        captureLocation(location -> {
            if (!isWithinOfficeRadius(location)) {
                Toast.makeText(this, getString(R.string.error_outside_office_radius_checkout), Toast.LENGTH_LONG).show();
                return;
            }
            String locationLabel = formatLocationLabel(getString(R.string.label_office_checkout_location), location);
            completeOfficeCheckout(locationLabel);
        }, this::showOfficeLocationUnavailable);
    }

    private void handleWFHCheckIn() {
        if (isCheckedIn) {
            Toast.makeText(this, getString(R.string.toast_already_checked_in), Toast.LENGTH_SHORT).show();
            return;
        }

        promptManualWFHCheckIn();
    }

    private void handleWFHCheckOut() {
        if (!isWFHCheckedIn || currentAttendance == null) {
            Toast.makeText(this, getString(R.string.toast_wfh_checkin_required), Toast.LENGTH_SHORT).show();
            return;
        }

        promptManualWFHCheckOut(currentAttendance != null ? currentAttendance.getCheckInLocation() : "");
    }

    private void markAttendance(String type, String location) {
        String attendanceId = employeeId + "_" + todayDate;
        String checkInTime = Utils.getCurrentTime();

        Attendance attendance = new Attendance(attendanceId, employeeId, todayDate);
        attendance.setCheckInTime(checkInTime);
        attendance.setCheckInLocation(location);
        attendance.setAttendanceType(type);
        attendance.setPresent(true);

        attendanceService.createAttendance(attendance, (error, ref) -> {
            if (error == null) {
                currentAttendance = attendance;
                applyAttendanceState(currentAttendance);
                String message = "WFH".equals(type)
                        ? getString(R.string.toast_checkin_success_wfh)
                        : getString(R.string.toast_checkin_success_office);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_attendance_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeOfficeCheckout(String location) {
        String attendanceId = employeeId + "_" + todayDate;
        String checkOutTime = Utils.getCurrentTime();

        Map<String, Object> updates = new HashMap<>();
        updates.put("checkOutTime", checkOutTime);
        updates.put("checkOutLocation", location);
        updates.put("isPresent", true);

        attendanceService.updateAttendance(attendanceId, updates, (error, ref) -> {
            if (error == null) {
                if (currentAttendance != null) {
                    currentAttendance.setCheckOutTime(checkOutTime);
                    currentAttendance.setCheckOutLocation(location);
                }
                applyAttendanceState(currentAttendance);
                Toast.makeText(this, getString(R.string.toast_checkout_success_office), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_checkout_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeWFHCheckout(String location) {
        String attendanceId = employeeId + "_" + todayDate;
        String checkOutTime = Utils.getCurrentTime();

        Map<String, Object> updates = new HashMap<>();
        updates.put("checkOutTime", checkOutTime);
        updates.put("checkOutLocation", location);
        updates.put("isPresent", true);

        attendanceService.updateAttendance(attendanceId, updates, (error, ref) -> {
            if (error == null) {
                if (currentAttendance != null) {
                    currentAttendance.setCheckOutTime(checkOutTime);
                    currentAttendance.setCheckOutLocation(location);
                }
                applyAttendanceState(currentAttendance);
                Toast.makeText(this, getString(R.string.toast_checkout_success_wfh), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.toast_checkout_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void captureLocation(Consumer<Location> onSuccess, Runnable onFallback) {
        try {
            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            onSuccess.accept(location);
                        } else {
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(this, lastLocation -> {
                                        if (lastLocation != null) {
                                            onSuccess.accept(lastLocation);
                                        } else {
                                            onFallback.run();
                                        }
                                    })
                                    .addOnFailureListener(e -> onFallback.run());
                        }
                    })
                    .addOnFailureListener(e -> onFallback.run());
        } catch (SecurityException e) {
            onFallback.run();
        }
    }

    private boolean isWithinOfficeRadius(Location location) {
        if (!officeLocationConfigured || location == null) {
            return false;
        }
        float[] results = new float[1];
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                officeLatitude, officeLongitude, results);
        return results[0] <= officeRadiusMeters;
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
    }

    private String formatLocationLabel(String label, Location location) {
        if (location == null) {
            return label;
        }
        return String.format(Locale.getDefault(), "%s - Lat: %.5f, Lng: %.5f",
                label, location.getLatitude(), location.getLongitude());
    }

    private void showOfficeLocationUnavailable() {
        Toast.makeText(this, getString(R.string.error_location_unavailable_office), Toast.LENGTH_LONG).show();
    }

    private void promptManualWFHCheckIn() {
        showManualLocationDialog(
                getString(R.string.dialog_title_wfh_check_in),
                "",
                input -> markAttendance("WFH", input));
    }

    private void promptManualWFHCheckOut(String prefill) {
        showManualLocationDialog(
                getString(R.string.dialog_title_wfh_check_out),
                prefill,
                this::completeWFHCheckout);
    }

    private void showManualLocationDialog(String title, String prefill, Consumer<String> onSubmit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_input, null);
        TextInputLayout layoutLocation = dialogView.findViewById(R.id.layoutLocationInput);
        TextInputEditText etLocation = dialogView.findViewById(R.id.etLocation);

        if (!TextUtils.isEmpty(prefill)) {
            etLocation.setText(prefill);
            etLocation.setSelection(prefill.length());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positive.setOnClickListener(v -> {
                String input = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";
                if (TextUtils.isEmpty(input)) {
                    layoutLocation.setError(getString(R.string.error_location_required));
                } else {
                    layoutLocation.setError(null);
                    dialog.dismiss();
                    onSubmit.accept(input);
                }
            });
        });

        dialog.show();
    }

    private void applyAttendanceState(Attendance attendance) {
        currentAttendance = attendance;

        if (attendance == null) {
            isCheckedIn = false;
            isWFHCheckedIn = false;
            tvStatus.setText(getString(R.string.status_not_checked_in));
            tvLocation.setText("");
            btnCheckIn.setEnabled(true);
            btnCheckOut.setEnabled(false);
            btnWFHCheckIn.setEnabled(true);
            btnWFHCheckOut.setEnabled(false);
            return;
        }

        boolean hasCheckIn = !TextUtils.isEmpty(attendance.getCheckInTime());
        boolean hasCheckOut = !TextUtils.isEmpty(attendance.getCheckOutTime());
        boolean isWFHType = "WFH".equals(attendance.getAttendanceType());

        isCheckedIn = hasCheckIn && !hasCheckOut;
        isWFHCheckedIn = isWFHType && !hasCheckOut;

        String location = !TextUtils.isEmpty(attendance.getCheckInLocation())
                ? attendance.getCheckInLocation()
                : getString(R.string.location_not_available);

        if (hasCheckOut && !TextUtils.isEmpty(attendance.getCheckOutLocation())) {
            location = attendance.getCheckOutLocation();
        }

        if (hasCheckOut) {
            tvStatus.setText(isWFHType
                    ? getString(R.string.status_checked_out_wfh)
                    : getString(R.string.status_checked_out_office));
        } else if (hasCheckIn) {
            tvStatus.setText(isWFHType
                    ? getString(R.string.status_checked_in_wfh)
                    : getString(R.string.status_checked_in_office));
        } else {
            tvStatus.setText(getString(R.string.status_not_checked_in));
        }

        tvLocation.setText(location);

        btnCheckIn.setEnabled(officeLocationConfigured && (!hasCheckIn || hasCheckOut));
        btnCheckOut.setEnabled(officeLocationConfigured && !hasCheckOut && !isWFHType && hasCheckIn);
        btnWFHCheckIn.setEnabled(!hasCheckIn || hasCheckOut);
        btnWFHCheckOut.setEnabled(!hasCheckOut && isWFHType && hasCheckIn);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (requestCode == REQUEST_LOCATION_OFFICE_CHECK_IN) {
            if (granted) {
                handleOfficeCheckIn();
            } else {
                showOfficeLocationUnavailable();
            }
        } else if (requestCode == REQUEST_LOCATION_OFFICE_CHECK_OUT) {
            if (granted) {
                handleOfficeCheckOut();
            } else {
                showOfficeLocationUnavailable();
            }
        }
    }
}
