package com.laverne.mymoviememoir.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;
import com.laverne.mymoviememoir.R;
import com.laverne.mymoviememoir.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private String userAddress;
    private String userPostcode;
    private String userState;
    private LatLng userLocation = null;
    private List<String> cinemaAddressList;
    private List<String> cinemaNameList;
    private List<LatLng> cinemaLocations = new ArrayList<>();
    private NetworkConnection networkConnection;
    private Geocoder geocoder;
    private UiSettings mUiSettings;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        // set up loading view
        linearLayout = view.findViewById(R.id.map_linear);
        progressBar = view.findViewById(R.id.map_progressbar);

        geocoder = new Geocoder(getContext(), Locale.getDefault());
        networkConnection = new NetworkConnection();

        new GetUserInfoTask().execute(getUserIdFromSharedPref());

        // retrieve cinema and convert loaction to lanlng
        new GetAllCinemaTask().execute();

        return view;
    }


    private void displayGoogleMap() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mUiSettings = mMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            // add user location marker
            if (userLocation != null) {
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
                // add cinema markers
                for (int i = 0; i < cinemaLocations.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(cinemaLocations.get(i)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(cinemaNameList.get(i)));
                }
                float zoomLevel = (float) 12.0;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, zoomLevel));
            } else {
                Toast.makeText(getActivity(), "Your address is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private String getUserIdFromSharedPref() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        return sharedPref.getString("userId", null);
    }


    private void getUserLatLng(String location) {
        // user location
        List<Address> userAddr = null;
        try {
            userAddr = geocoder.getFromLocationName(location, 1);

            if (userAddr == null || userAddr.size() == 0) {
                return;
            }

            Address address = userAddr.get(0);
            double latitude = address.getLatitude();
            double longitude = address.getLongitude();
            userLocation = new LatLng(latitude, longitude);

        } catch (IOException e) {
            userLocation = null;
            e.printStackTrace();
        }
    }


    private void getAllCinemaLatLng(List<String> locations) {
        for (int i = 0; i < locations.size(); i++) {
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(locations.get(i), 1);

                if (addressList == null || addressList.size() == 0) {
                    continue;
                }
                Address address = addressList.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();
                cinemaLocations.add(new LatLng(latitude, longitude));

                // remove the progress bar
                progressBar.setVisibility(View.GONE);
               linearLayout.setVisibility(View.GONE);

                displayGoogleMap();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class GetAllCinemaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return networkConnection.getAll("cinema");
        }

        @Override
        protected void onPostExecute(String cinema) {
            cinemaAddressList = new ArrayList<>();
            cinemaNameList = new ArrayList<>();
            if (cinema != null) {
                try {
                    JSONArray jsonArray = new JSONArray(cinema);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String cineName = jsonObject.getString("cineName");
                        String cinePostcode = jsonObject.getString("cinePostcode");
                        cinemaAddressList.add(cineName + " " + cinePostcode + " Australia");
                        cinemaNameList.add(cineName);
                    }
                    // convert all cinema addresses to latitude longitude
                    getAllCinemaLatLng(cinemaAddressList);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                }
            } else {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private class GetUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // show the progress bar
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return networkConnection.getUserById(Integer.parseInt(params[0]));
        }

        @Override
        protected void onPostExecute(String user) {
            if (user != null) {
                try {
                    JSONObject jsonObject = new JSONObject(user);
                    String address = jsonObject.getString("userAddress");
                    String postcode = jsonObject.getString("userPostcode");
                    String state = jsonObject.getString("userState");
                    // convert address to latitude and longitudegetUserLatLng(address + ", " + postcode + " " + state);

                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(getActivity(), "Error", "Something went wrong, please try again later.");
            }
        }
    }
}
