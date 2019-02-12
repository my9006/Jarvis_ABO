package com.jarvis_abo;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jarvis_abo.entities.Notification;
import com.jarvis_abo.entities.NotificationDao;
import com.jarvis_abo.miscellaneous.google.PermissionUtils;
import com.jarvis_abo.model.Type;
import com.jarvis_abo.notification.notification_list.NotificationsList;
import com.joanzapata.iconify.widget.IconButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private FloatingActionButton rightLowerButton;
    private FloatingActionMenu rightLowerMenu;
    private LinearLayout linearLayout;
    private HashMap<Notification, Marker> markerHashMap;
    private ArrayList<IconButton> iconButtons;
    private EditText et_Message;
    private IconButton ib_messageOk;
    private IconButton ib_messageCancel;
    private IconButton ib_IconPreview;

/*
Our side
 */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        et_Message = (EditText) findViewById(R.id.et_message);
        ib_messageOk = (IconButton) findViewById(R.id.ib_messageOK);
        ib_messageCancel = (IconButton) findViewById(R.id.ib_messageCancel);
        ib_IconPreview = (IconButton) findViewById(R.id.ib_iconPreview);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logOut:
                SharedPreferences sharedPreferences = getSharedPreferences("com.jarvis_abo_preferences", MODE_PRIVATE);
                sharedPreferences.edit().putString("last_successful_login", "BAD").apply();

                Intent intent = new Intent(MapViewActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.notifications:
                Intent intent1 = new Intent(MapViewActivity.this, NotificationsList.class);
                startActivity(intent1);
                break;
        }
        return true;

    }

    /*
     *Google Side
     */


    private GoogleMap mMap;

    private UiSettings mUiSettings;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;

    private boolean isZoomedOnce = false;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mLocationPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);


        linearLayout = (LinearLayout) findViewById(R.id.ll_messageLayout);

        markerHashMap = new HashMap<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Returns whether the checkbox with the given id is checked.
     */
    private boolean isChecked(int id) {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    @Override
    @SuppressWarnings({"NewApi", "ResourceType"})
    @SuppressLint("NewApi")
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        mMap.setMyLocationEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mMap.setOnMapLongClickListener(this);


        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {


                if (location != null && !isZoomedOnce) {
                    LatLng myLocation = new LatLng(location.getLatitude(),
                            location.getLongitude());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                            mMap.getMaxZoomLevel()));

                    isZoomedOnce = true;
                }
            }
        });


        NotificationDao notificationDao = JA_application.getInstance().getDaoSession().getNotificationDao();
        for (Notification notification : notificationDao.queryBuilder().where(NotificationDao.Properties.Fk_user.eq(JA_application.getInstance().loggedUser().getPk_id())).list()) {


            Marker marker = mMap.addMarker(notification.reverseEngineer());

            markerHashMap.put(notification, marker);
        }
//
        // Set up the white button on the lower right corner
        // more or less with default parameter
        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_input_add));
        rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();
        rightLowerButton.setVisibility(View.GONE);


        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(this);

        iconButtons = new ArrayList<IconButton>();

        for (String type : Type.types) {

            IconButton iconButton = new IconButton(MapViewActivity.this);

            iconButtons.add(iconButton);
            builder.addSubActionView(iconButton);
            iconButton.setText("{" + type + " 30dp} ");
            iconButton.setTag(type);
            iconButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            iconButton.setBackgroundResource(android.R.color.transparent);
            iconButton.setTextColor(getResources().getColor(Type.colors.get(type)));
        }


        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        rightLowerMenu = builder
                .attachTo(rightLowerButton)
                .build();

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
                rightLowerButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

    }


    /**
     * Checks if the map is ready (which depends on whether the Google Play services APK is
     * available. This should be called prior to calling any methods on GoogleMap.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setZoomButtonsEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the zoom controls (+/- buttons in the bottom-right of the map for LTR
        // locale or bottom-left for RTL locale).
        mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    public void setCompassEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the compass (icon in the top-left for LTR locale or top-right for RTL
        // locale that indicates the orientation of the map).
        mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
    }

    public void setMyLocationButtonEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location button (this DOES NOT enable/disable the my location
        // dot/chevron on the map). The my location button will never appear if the my location
        // layer is not enabled.
        // First verify that the location permission has been granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mUiSettings.setMyLocationButtonEnabled(true);
        } else {
            // Uncheck the box and request missing location permission.
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables the my location layer (i.e., the dot/chevron on the map). If enabled, it
        // will also cause the my location button to show (if it is enabled); if disabled, the my
        // location button will never show.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Uncheck the box and request missing location permission.

            PermissionUtils.requestPermission(this, LOCATION_LAYER_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables scroll gestures (i.e. panning the map).
        mUiSettings.setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
        mUiSettings.setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables tilt gestures.
        mUiSettings.setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) {
            return;
        }
        // Enables/disables rotate gestures.
        mUiSettings.setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            // Enable the My Location button if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                mUiSettings.setMyLocationButtonEnabled(true);

            } else {
                mLocationPermissionDenied = true;
            }

        } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
            // Enable the My Location layer if the permission has been granted.
            if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                mMap.setMyLocationEnabled(true);

            } else {
                mLocationPermissionDenied = true;
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mLocationPermissionDenied) {
            PermissionUtils.PermissionDeniedDialog
                    .newInstance(false).show(getSupportFragmentManager(), "dialog");
            mLocationPermissionDenied = false;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        linearLayout.setVisibility(View.VISIBLE);
        ib_IconPreview.setText("");
        et_Message.setText("");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("title");
        final Marker marker = mMap.addMarker(markerOptions);

        rightLowerButton.setVisibility(View.VISIBLE);

        for (IconButton ib : iconButtons) {

            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String type = (String) v.getTag();

                    ib_IconPreview.setText("{" + type + "}");
                    ib_IconPreview.setTag(type);
                    ib_IconPreview.setBackgroundResource(android.R.color.transparent);
                    ib_IconPreview.setTextColor(getResources().getColor(Type.colors.get(type)));

                }
            });

        }

        ib_messageOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (
                        et_Message.getText().toString().length() > 0 &&
                                ib_IconPreview.getTag() != null &&
                                ((String) ib_IconPreview.getTag()).length() > 0
                        ) {
                    Notification notification = new Notification(latLng, et_Message.getText().toString(), ((String) ib_IconPreview.getTag()));
                    notification.setInactiveUntil(Calendar.getInstance().getTime());
                    JA_application.getInstance().getDaoSession().getNotificationDao().insert(notification);
                    marker.setIcon(Type.getCustomMarker(notification.getType()));
                    marker.setTitle(notification.getMessage());

                    markerHashMap.put(notification, marker);

                    linearLayout.setVisibility(View.GONE);
                    rightLowerButton.setVisibility(View.GONE);
                    if (rightLowerMenu.isOpen()) {
                        rightLowerMenu.close(true);
                    }

                } else {
                    Toast.makeText(MapViewActivity.this, "Please select the type and write a message", Toast.LENGTH_LONG).show();

                }
            }
        });
        ib_messageCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                rightLowerButton.setVisibility(View.GONE);
                if (rightLowerMenu.isOpen()) {
                    rightLowerMenu.close(true);
                }
                marker.remove();

            }
        });

    }
}
