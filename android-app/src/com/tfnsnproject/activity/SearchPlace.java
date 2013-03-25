package com.tfnsnproject.activity;

import android.R;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tfnsnproject.to.PlaceInfo;
import com.tfnsnproject.util.PlacesClient;

import java.util.Collections;
import java.util.List;

public class SearchPlace extends ListActivity {

    public static final String PLACE = "PLACE";

    EditText searchPlace;

    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.searchPlace = (EditText) findViewById(com.tfnsnproject.R.id.search_place);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(criteria, true);

        lastLocation = locationManager.getLastKnownLocation(provider);

        setContentView(com.tfnsnproject.R.layout.search_place);

        if (lastLocation == null) {
            new SearchNearbyTask().execute(51.488999, -0.15044);
        } else {
            new SearchNearbyTask().execute(lastLocation.getLatitude(), lastLocation.getLongitude());
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(PLACE, (PlaceInfo)getListAdapter().getItem(position));
        setResult(RESULT_OK, intent);
        finish();
    }

    class SearchNearbyTask extends AsyncTask<Double, Void, List<PlaceInfo>> {

        ProgressDialog mDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(SearchPlace.this);
            mDialog.setMessage("Loading places nearby...");
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected List<PlaceInfo> doInBackground(Double... params) {
            return PlacesClient.getInstance().searchNearby(params[0], params[1], 100);
        }

        @Override
        protected void onPostExecute(List<PlaceInfo> placeInfos) {
            super.onPostExecute(placeInfos);
            mDialog.dismiss();
            if (placeInfos == null) {
                Toast.makeText(SearchPlace.this, "Error loading places...", Toast.LENGTH_SHORT).show();
                setListAdapter(new PlaceListAdapter(Collections.EMPTY_LIST));
            } else {
                setListAdapter(new PlaceListAdapter(placeInfos));
            }
        }
    }

    class PlaceListAdapter extends BaseAdapter implements ListAdapter {

        private List<PlaceInfo> places;

        PlaceListAdapter(List<PlaceInfo> places) {
            this.places = places;
        }

        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return places.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.simple_list_item_1, null);
            }
            ((TextView)convertView).setText(places.get(position).getName());
            return convertView;
        }
    }
}