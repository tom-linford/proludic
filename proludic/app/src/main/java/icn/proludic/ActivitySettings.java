package icn.proludic;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import icn.proludic.adapters.RecyclerViewHomeParkAdapter;
import icn.proludic.misc.Constants;
import icn.proludic.misc.SharedPreferencesManager;
import icn.proludic.misc.Utils;
import icn.proludic.models.HomeParkModel;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.View.GONE;
import static icn.proludic.misc.Constants.HOME_PARK_KEY;
import static icn.proludic.misc.Constants.LOCATIONS_CLASS_KEY;
import static icn.proludic.misc.Constants.LOCATION_IMAGE_KEY;
import static icn.proludic.misc.Constants.LOCATION_LATITUDE;
import static icn.proludic.misc.Constants.LOCATION_LONGITUDE;
import static icn.proludic.misc.Constants.LOCATION_NAME_KEY;
import static icn.proludic.misc.Constants.LOCATION_STARTING_POINT_KEY;
import static icn.proludic.misc.Constants.TWENTY_FIVE_MILES;

/**
 * Author:  Bradley Wilson
 * Date: 13/06/2017
 * Package: icn.proludic
 * Project Name: proludic
 */

public class ActivitySettings extends AppCompatActivity {

    private Context context = this;
    private RecyclerView selectHomeParkRecycler;
    private Utils utils = new Utils(context);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle bundle = getIntent().getExtras();
        double longitude = bundle.getDouble(LOCATION_LONGITUDE);
        double latitude = bundle.getDouble(LOCATION_LATITUDE);

        selectHomeParkRecycler = (RecyclerView) findViewById(R.id.select_home_park_recycler_view);
        selectHomeParkRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        selectHomeParkRecycler.setLayoutManager(mLayoutManager);
        populateRecyclerView(longitude, latitude);
        setCustomToolbar();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.enable_3g_4g);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesManager.setBoolean(context, Constants.SETTINGS_ENABLE_3G_4G, true);
                } else {
                    SharedPreferencesManager.setBoolean(context, Constants.SETTINGS_ENABLE_3G_4G, false);
                }
            }
        });

    }

    public void setCustomToolbar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setElevation(0);
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(R.layout.dashboard_toolbar);
        ab.getCustomView().findViewById(R.id.logo).setVisibility(GONE);
        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void populateRecyclerView(double longitude, double latitude) {
        final ArrayList<HomeParkModel> homeParksList = new ArrayList<>();
        ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATIONS_CLASS_KEY);
        query.whereWithinKilometers(LOCATION_STARTING_POINT_KEY, userLocation, TWENTY_FIVE_MILES);
        query.setLimit(999);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject j : objects) {
                            Log.e("objects", j.getObjectId());
                            homeParksList.add(new HomeParkModel(j.getObjectId(), j.getString(LOCATION_NAME_KEY), j.getParseFile(LOCATION_IMAGE_KEY).getUrl()));
                        }
                        RecyclerViewHomeParkAdapter adapter = new RecyclerViewHomeParkAdapter(context, homeParksList);
                        selectHomeParkRecycler.setAdapter(adapter);
                        adapter.setOnItemClickListener(new RecyclerViewHomeParkAdapter.onHomeParkItemClickListener() {
                            @Override
                            public void onItemClickListener(View view, int position, final String objectId, String name) {
                                utils.setHomeObjectId(objectId);
                                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.customAlertDialog).create();
                                alertDialog.setTitle(context.getResources().getString(R.string.selectHomePark));
                                alertDialog.setMessage(getString(R.string.sure_you_would_like_to_make) + " " + name + " " + getString(R.string.your_home_park));
                                alertDialog.setButton(BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ParseUser.getCurrentUser().put(HOME_PARK_KEY, objectId);
                                        ParseUser.getCurrentUser().saveInBackground();
                                    }
                                });

                                alertDialog.setButton(BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.show();
                            }
                        });
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("noParksWithin25Miles", "true");
                    }
                } else {
                    Log.e("failed", "failed " + e.getMessage());
                }
            }
        });
    }
}
