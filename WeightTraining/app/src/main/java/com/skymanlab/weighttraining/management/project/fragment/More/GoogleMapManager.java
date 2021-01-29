package com.skymanlab.weighttraining.management.project.fragment.More;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.skymanlab.weighttraining.R;
import com.skymanlab.weighttraining.management.developer.Display;
import com.skymanlab.weighttraining.management.developer.LogManager;

import java.io.IOException;
import java.util.List;

public class GoogleMapManager {

    // constant
    private static final String CLASS_NAME = GoogleMapManager.class.getSimpleName();
    private static final Display CLASS_LOG_DISPLAY_POWER = Display.ON;

    // instance variable
    private Activity activity;
    private GoogleMap googleMap;
    private int interval;
    private int fastestInterval;
    private int priority;

    // instance variable
    private LocationUpdateManager updateManager;
    private PermissionManager permissionManager;


    // constructor
    private GoogleMapManager(Builder builder) {
        this.activity = builder.activity;
        this.googleMap = builder.googleMap;
        this.interval = builder.interval;
        this.fastestInterval = builder.fastestInterval;
        this.priority = builder.priority;
    }

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= initialize setting  =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public void init() {
        final String METHOD_NAME = "[init] ";

        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< google Map Manager > 구글 맵의 초기 설정을 시작합니다.==================================================================");
        permissionManager = new PermissionManager(activity);
        permissionManager.init(new PermissionManager.GrantedPermissionListener() {
            @Override
            public void setNextProcedure() {

                // init UI
                initUI();

                // map click listener
                googleMap.setOnMapClickListener(createOnMapClickListener());

                // my location 사용가능 한지 파악 후
                initMyLocation();

            }
        });

    }

    private void initUI() {
        final String METHOD_NAME = "[initUI] ";
        LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< UI > googleMap 의 UI 를 설정하겠습니다.");
        // map tool bar 사용 가능 / zoom 컨트롤 사용 가능
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void initMyLocation() {
        final String METHOD_NAME = "[initMyLocation] ";


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< my location > googleMap 에 현재 자신의 위치를 가져올 수 있도록 설정중입니다.");
            // Enable : true 로 변경
            googleMap.setMyLocationEnabled(true);

            // my location click listener 등록
            googleMap.setOnMyLocationClickListener(createOnMyLocationClickListener());

            // my location button click listener 등록
            googleMap.setOnMyLocationButtonClickListener(createOnMyLocationButtonClickListener());


            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< My location >  location 를 가져오기 위한 설정을 진행합니다.");

            // 위치 업데이트를 하기위한 초기 설정하기
            initLocationUpdate();

        } else {
            // 권한 거부에 대한 다음 과정 진행
        }

    }

    private void initLocationUpdate() {
        final String METHOD_NAME = "[initLocationUpdate] ";

        // [LocationUpdateManager] [updateManager] location update 를 사용하기 위한 초기 데이터를 설정한다.
        this.updateManager = new LocationUpdateManager.Builder()
                .setActivity(activity)
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setPriority(priority)
                .setLocationCallback(createLocationCallback())
                .create();

        // [LocationUpdateManager] [updateManager] 초기 데이터로 초기 설정을 한다.
        this.updateManager.init();

        // last location 으로 초기 위치 설정
        this.updateManager.getLastLocation(activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< last location > 마지막 위치가 있는지 확인합니다. = " + location);
                if (location != null) {
                    // 마지막 위치 표시
                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< lat location > 마지막 위치가 있습니다. 마지막 위치를 표시합니다. / 위도:" + location.getLatitude() + ", 경도: " + location.getLongitude());

                    // [LatLng] [lastLatLng] location 으로 LatLng 객체로 생성
                    LatLng lastLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // googleMap 에 lastLatLng 의 위치에 마커 표시
                    LocationUpdateUtil.showMarkerToMap(activity, googleMap, lastLatLng);

                } else {

                    // 기본 위치 표시

                    // [LatLng] [lastLatLng] 서울을 기본 위치로 하여 LatLng 객체로 생성
                    LatLng defaultLatLng = LocationUpdateUtil.SEOUL;
                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< last location > 위치 없음 기존 위치로 표시합니다. / 위도:" + defaultLatLng.latitude + ", 경도:" + defaultLatLng.longitude);

                    // googleMap 에 lastLatLng 의 위치에 마커 표시
                    LocationUpdateUtil.showMarkerToMap(activity, googleMap, defaultLatLng);

                }
            }
        });

    }


    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Listener =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    private GoogleMap.OnMapClickListener createOnMapClickListener() {
        final String METHOD_NAME = "[createOnMapClickListener] ";
        return new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< map click > 해당 맵을 클릭하였습니다. / 위도:" + latLng.latitude + ", 경도:" + latLng.longitude);
            }
        };
    }

    private GoogleMap.OnMyLocationClickListener createOnMyLocationClickListener() {
        final String METHOD_NAME = "[createOnMyLocationClickListener] ";
        return new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< my location click listener > 나의 위치를 클릭하였습니다. / 위도:" + location.getLatitude() + ", 경도:" + location.getLongitude());

            }
        };
    }

    private GoogleMap.OnMyLocationButtonClickListener createOnMyLocationButtonClickListener() {
        final String METHOD_NAME = "[createOnMyLocationButtonClickListener] ";
        return new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< my location button click listener > 나의 위치 버튼을 클릭하여서 현재 위치로 이동 합니다.");
                return false;
            }
        };
    }

    private LocationCallback createLocationCallback() {
        final String METHOD_NAME = "[createLocationCallback] ";
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // locationResult 를 통해 update 된 위치의 모든 데이터 가져오기
                List<Location> locationList = locationResult.getLocations();

                // [check 1] 위의 데이터가 존재할 경우 만
                if (locationResult.getLastLocation() != null) {


                    // [Location] [currentLocation] 마지막 index 의 location 를 현재 위치로 설정
                    Location currentLocation = locationResult.getLastLocation();
                    LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< location call back > 현재 위치를 업데이트 받았습니다. / 위도:" + currentLocation.getLatitude() + ", 경도:" + currentLocation.getLongitude());

                    // [LatLng] [currentLatLng] Location 의 위도, 경도로 LatLng 객체 생성
                    LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    LocationUpdateUtil.showMarkerToMap(activity, googleMap, currentLatLng);

                } // [check 1]
            }
        };
    }

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= 외부에서 사용하기 위한 method =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // location update start
    public void startLocationUpdate() {
        final String METHOD_NAME = "[startLocationUpdate] ";
        if (this.updateManager != null) {
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< update manager > 해당 location update 를 실행합니다.");
            this.updateManager.startLocationUpdate(activity);
        }

    }

    // location update stop
    public void stopLocationUpdate() {
        final String METHOD_NAME = "[stopLocationUpdate] ";

        if (this.updateManager != null) {
            LogManager.displayLog(CLASS_LOG_DISPLAY_POWER, CLASS_NAME, METHOD_NAME, "< update manager stop > 해당 location update 를 중지합니다.");
            this.updateManager.stopLocationUpdates(activity);
        }

    }



    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Builder =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    public static class Builder {

        // instance variable
        private Activity activity;
        private GoogleMap googleMap;
        private int interval;
        private int fastestInterval;
        private int priority;

        // constructor
        public Builder() {
        }

        // setter
        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setGoogleMap(GoogleMap googleMap) {
            this.googleMap = googleMap;
            return this;
        }

        public Builder setInterval(int interval) {
            this.interval = interval;
            return this;
        }

        public Builder setFastestInterval(int fastestInterval) {
            this.fastestInterval = fastestInterval;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        // create
        public GoogleMapManager create() {
            return new GoogleMapManager(this);
        }
    }
}
