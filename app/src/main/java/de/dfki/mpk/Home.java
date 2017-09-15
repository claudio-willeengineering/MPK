package de.dfki.mpk;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.willeengineering.sdk.WESBMSManager.SBMSBeacon;
import com.willeengineering.sdk.WESBMSManager.SBMSEndUser;
import com.willeengineering.sdk.WESBMSManager.SBMSLocation;
import com.willeengineering.sdk.WESBMSManager.SBMSManager;
import com.willeengineering.sdk.WESBMSManager.SBMSManagerCallback;
import com.willeengineering.sdk.WEScanner.WEBeaconRangingService;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import de.dfki.mpk.fragments.BaseFragment;
import de.dfki.mpk.fragments.FragmentDetails;
import de.dfki.mpk.fragments.FragmentFeedback;
import de.dfki.mpk.fragments.FragmentFloorPlan;
import de.dfki.mpk.fragments.FragmentPass;
import de.dfki.mpk.fragments.Fragment_Home;
import de.dfki.mpk.model.ExhibitTimeWrapper;
import de.dfki.mpk.model.Exhibits;
import de.dfki.mpk.model.Topic;
import de.dfki.mpk.utils.ExponatIconHelper;
import de.dfki.mpk.utils.UtilsHelpers;

public class Home extends AppCompatActivity{

    String TAG = Home.class.getSimpleName();

    private Toolbar toolbar;
    SubsamplingScaleImageView imageView;
    FrameLayout layout;

    public Button passButton;
    public Button feedbackButton;


    public JSONObject jsonData = new JSONObject();
    HashMap<String,ExhibitTimeWrapper> exhibits = new HashMap<>();
    HashMap<String,Topic> topics = new HashMap<>();

    List<Fragment> fragments = new ArrayList<>();
    HashMap<String, Exhibits> recommendedExponats = new HashMap<>();


    // MANDATORY!! Broadcast attributes //
    LocalBroadcastManager bManager; // Broadcast manager


    public static int REQUEST_ENABLE_BT = 1612;
    ExponatIconHelper iconHelper = null;


    //Location Update
    Location mostRecentLocation = null;
    private FusedLocationProviderClient mFusedLocationClient = null;
    boolean mRequestingLocationUpdates = false;
    List<Location> polygon = new ArrayList<>();
    boolean isRequestingLocationUpdates = false;


    String mostRecentBeacon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iconHelper = new ExponatIconHelper(this);

        if(((MPKApplication)getApplication()).isFirstRun())
        {
            Intent i = new Intent(this, FirstScreenActivity.class);
            startActivity(i);
        }
        else
        {
            /*
            if(((MPKApplication) getApplication()).getBeaconPermission()) {
                try {
                    initSdk();
                    //bindToAsandooService();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            */

        }


        toolbar.setSubtitle(getString(R.string.home_title));


        layout = (FrameLayout) findViewById(R.id.fragmentContainer);


        jsonData = UtilsHelpers.fromRawToJson(this,R.raw.content);
        buildExhibitionItems();
        Fragment_Home fragment_home = Fragment_Home.createInstance();
        switchFragment(fragment_home, fragment_home.title );

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                super.onFragmentAttached(fm, f, context);
                fragments.add(f);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentDestroyed(fm, f);
                fragments.remove(f);

            }
        },false);

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {

                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                        if(frag != null) {
                            frag.setUserVisibleHint(true);

                            if (frag instanceof Fragment_Home) {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                getSupportActionBar().setDisplayShowHomeEnabled(false);

                            }
                            else
                            {
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                getSupportActionBar().setDisplayShowHomeEnabled(true);
                            }
                        }

                        else
                        {
                            Fragment_Home  fragment_home1 = Fragment_Home.createInstance();
                            switchFragment(fragment_home1,fragment_home1.title);
                        }


                    }
                });


        feedbackButton = (Button) findViewById(R.id.feedbackButton);
        passButton = (Button) findViewById(R.id.passButton);

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentFeedback fragmentFeedback = FragmentFeedback.createInstance();
                switchFragment(fragmentFeedback, fragmentFeedback.title);
            }
        });

        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentPass pass = FragmentPass.createInstance();
                switchFragment(pass, pass.title);
            }
        });

        mFusedLocationClient = new FusedLocationProviderClient(this);

    }

      public void setSubtitle(String sub)
      {
          toolbar.setSubtitle(sub);
      }

      public void buildExhibitionItems(){
          try {
              JSONArray tops = jsonData.getJSONArray("topics");
              JSONArray exibs = jsonData.getJSONArray("exhibits");
              for(int i=0; i<exibs.length(); i++) {
                      Exhibits e = new Exhibits(exibs.getJSONObject(i));
                      exhibits.put(e.getId(), new ExhibitTimeWrapper(e, this) );
              }
              for (int i=0; i<tops.length(); i++)
              {
                  Topic t = new Topic(tops.getJSONObject(i));
                  topics.put(t.getId(),t);
              }


          } catch (JSONException e) {
              e.printStackTrace();
          }

      }



    @Override
    protected void onResume() {
        super.onResume();
        if((!((MPKApplication)getApplication()).isFirstRun()))
        {
            if(((MPKApplication)getApplication()).getBeaconPermission()) {
                initSdk();
                startLocationUpdates();
            }
            computeRecommendations();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(frag != null)
        {
            if(frag instanceof Fragment_Home)
            {
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        deInitSDK();
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        super.onDestroy();


    }

    public void switchFragment(BaseFragment frag, String name)
    {

        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if(currFrag!=null)
            if(currFrag.equals(frag)) {
            return;
        }

        if(fragments.contains(frag))
        {
            getSupportFragmentManager().popBackStack(frag.id,0);
           // getSupportFragmentManager().beginTransaction().remove(frag).commitNow();
        }
        else {
            // Add the fragment to the 'fragment_container' FrameLayout
            if(!(fragments instanceof FragmentDetails)) {
                frag.id = getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, frag).addToBackStack(frag.title).commit();
            }
            else
            {
                frag.id = getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, frag).addToBackStack(name).commit();
            }
        }

        if(! (frag instanceof Fragment_Home))
        {
            toolbar.setSubtitle(frag.title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        else
        {
            toolbar.setTitle("MPK");
            toolbar.setSubtitle("Hauptmenu");
            toolbar.setTitle(getString(R.string.app_name));
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

    }

    public ExponatIconHelper getIconHelper()
    {
        return iconHelper;
    }

    public List<Topic> getTopics() {
        List<Topic> ts = new ArrayList<>();
        ts.addAll(this.topics.values());
        Collections.sort(ts);
        return ts;
    }

    public HashMap<String, Exhibits> getRecommendedExponats(){
        return recommendedExponats;
    }

    public List<ExhibitTimeWrapper> getExhibitTimeWrappers(){
        List<ExhibitTimeWrapper> es = new ArrayList<>();
        for(ExhibitTimeWrapper e : exhibits.values())
        {
            es.add(e);
        }
        return es;
    }

    public ExhibitTimeWrapper getExhibitTimeWrapper(String id)
    {
        return exhibits.get(id);
    }

    public boolean containsExhibit(String id){
        return exhibits.containsKey(id);
    }


    public List<Exhibits> getExhibits() {
        List<Exhibits> es = new ArrayList<>();
        for(ExhibitTimeWrapper e : this.exhibits.values()) {
            es.add(e.getExhibits());
        }
        Collections.sort(es);
        return es;
    }

    public List<Exhibits> getExibitions(String[] reference)
    {
        List<Exhibits> e = new ArrayList<>();
        if(reference!=null)
        for(String r: reference)
        {
           if(exhibits.containsKey(r))
           {
               e.add(exhibits.get(r).getExhibits());
           }
        }
        return e;
    }

    public List<Topic> getTopic(String[] references)
    {
        List<Topic> t = new ArrayList<>();
        if(references!=null)
        for(String r : references)
        {
            if(topics.containsKey(r)) {
                t.add(topics.get(r));
            }
        }
        return t;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionToRecordAccepted = false;
        switch (requestCode){
            case FragmentFeedback.REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) {
            UtilsHelpers.showErrorDialog(this,getString(R.string.error_audio_dialog_title),
                    getString(R.string.error_audio_dialog_body));
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if ((resultCode == RESULT_OK)){
                startSdk();
            }
            else if ((resultCode == RESULT_CANCELED)){
                //close the app
                try {
                    //startSdk();
                    deInitSDK();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasAudioPermission()
    {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }


    void computeRecommendations(){
        MPKApplication application = (MPKApplication) getApplication();
        JSONObject recommendation = UtilsHelpers.fromRawToJson(this, R.raw.recommendation);

        if(application.getGender().compareTo("")!=0) {
            try {
                JSONArray subrec = recommendation.getJSONObject("gender_age").getJSONArray(application.getGender());
                JSONObject userAge = new JSONObject(application.getAgeRange());
                String min = userAge.getString("min");
                String max = userAge.getString("max");

                int age = -1;
                if (min != "") {
                    age = Integer.parseInt(min) + 3;
                } else {
                    age = Integer.parseInt(max) - 3;
                }

                JSONArray recommendationReferences = null;
                for (int i = 0; i < subrec.length(); i++) {
                    int minAge = subrec.getJSONObject(i).getInt("min_age");
                    int maxAge = subrec.getJSONObject(i).getInt("max_age");
                    if (age >= minAge && age <= maxAge) {
                        recommendationReferences = subrec.getJSONObject(i).getJSONArray("recommendations");
                        break;
                    }
                }

                for (int i = 0; i < recommendationReferences.length(); i++) {
                    String id = recommendationReferences.getString(i);
                    if (exhibits.containsKey(id)) {
                        recommendedExponats.put(id, exhibits.get(id).getExhibits());
                    }
                }
            } catch (JSONException exp) {
                exp.printStackTrace();
            }


        }

            try {
                JSONObject subrec = recommendation.getJSONObject("image_preference");

                JSONArray recommendationReferences = null;
                switch (application.getImagePreference()) {
                    case 1:
                        recommendationReferences = subrec.getJSONArray("technology");
                        break;
                    case 2:
                        recommendationReferences = subrec.getJSONArray("secret");
                        break;
                    case 3:
                        recommendationReferences = subrec.getJSONArray("body");
                        break;
                    case 4:
                        recommendationReferences = subrec.getJSONArray("privacy");
                        break;
                }

                for (int i = 0; i < recommendationReferences.length(); i++) {
                    String id = recommendationReferences.getString(i);
                    if (exhibits.containsKey(id)) {
                        recommendedExponats.put(id, exhibits.get(id).getExhibits());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        computeThreshHoldRecommendations();
    }

    public void computeThreshHoldRecommendations()
    {
        MPKApplication application = (MPKApplication) getApplication();
        JSONObject recommendation = UtilsHelpers.fromRawToJson(this, R.raw.recommendation);
        try {
            JSONObject long_stay = recommendation.getJSONObject("long_stay");
            int threshhold = long_stay.getInt("threshold");
            JSONObject items = long_stay.getJSONObject("items");

            for(int i=0; i<items.names().length(); i++){
                String key = items.names().getString(i);

                if(exhibits.containsKey(key)){
                    if((exhibits.get(key).getTimeSpent()/1000)>=threshhold)
                    {
                        JSONArray newlyRecommended = items.getJSONArray(key);
                        for(int x=0; x< newlyRecommended.length(); x++)
                        {
                            String id = newlyRecommended.getString(x);
                            if(exhibits.containsKey(id))
                            {
                                recommendedExponats.put(id, exhibits.get(id).getExhibits());
                            }
                        }
                    }
                }
            }

        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }

    }

    private void initSdk() {
        if (!UtilsHelpers.isBluetoothAvailable()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            startSdk();
        }
    }
    void startSdk(){

        //Start the used services
        startService(new Intent(this, WEBeaconRangingService.class));

        bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.willeengineering.sdk.WEScanner.SCAN_MESSAGE");
        bManager.registerReceiver(bReceiver, intentFilter);

        // Init SDK
        SBMSManager.init(getApplicationContext());
        SBMSManager.setCallback(mSBMSManagerCallback);
        SBMSManager.setToken(MPKApplication.BEACON_TOKEN);
        BackgroundPowerSaver backgroundPowerSaver = new BackgroundPowerSaver(this);

    }

    void deInitSDK(){
        if(bManager!=null)
        {
            bManager.unregisterReceiver(bReceiver);
            stopService(new Intent(this, WEBeaconRangingService.class));
        }
    }


    ////////////// CALLBACKS /////////////
    public final SBMSManagerCallback mSBMSManagerCallback = new SBMSManagerCallback() {
        @Override
        public void onEnteredSubplace(SBMSLocation sBMSLocation) {

            String subplaceId = sBMSLocation.getSubplaceId()+"";

            if(containsExhibit(subplaceId))
            {
                getExhibitTimeWrapper(subplaceId).Enter();
            }

        }

        @Override
        public void onExitedSubplace(SBMSLocation sBMSLocation) {

            String subplaceId = sBMSLocation.getSubplaceId()+"";

            if(containsExhibit(subplaceId))
            {
                getExhibitTimeWrapper(subplaceId).Leave();
            }

        }

        @Override
        public void onDebugMessage(String debugMessage) {
            final String outputString = "onDebugMessage: " + debugMessage;

            Log.i(TAG, outputString);

        }

    };

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.willeengineering.sdk.WEScanner.SCAN_MESSAGE")) {
                String action = intent.getStringExtra("action");
                String uuid = intent.getStringExtra("uuid");
                String major = intent.getStringExtra("major");
                String minor = intent.getStringExtra("minor");
                int rssi = intent.getIntExtra("rssi", SBMSBeacon.RSSI_DEFAULT_ERROR_VALUE);

                String displayText = "";
                //Do something with the string
                Log.i(TAG, "onReceive: " + action + " " + major + " " + minor + " rssi " + rssi);
                SBMSBeacon beacon = new SBMSBeacon(new Date().toString(), uuid, Integer.parseInt(major), Integer.parseInt(minor), rssi);
                SBMSManager.manageCallbackEnteredBeacon(beacon);
            }
        }
    };


    public boolean amIVisible(Fragment fragment)
    {

        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        return fragment.equals(currFrag);
    }


    //Location
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            isRequestingLocationUpdates = true;

            LocationRequest mLocationRequest = new LocationRequest();
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);

            buildPolygon();

        }


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                mostRecentLocation = location;
            }
        };
    };

    private void stopLocationUpdates() {

        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        mRequestingLocationUpdates = false;
    }


    public void buildPolygon(){

        try {
            JSONArray array = UtilsHelpers.fromRawToJsonArray(this, R.raw.geofence);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                Location l = new Location("");
                l.setLatitude(json.getDouble("la"));
                l.setLongitude(json.getDouble("lo"));
                polygon.add(l);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            e.printStackTrace();
        }
    }
    public boolean isInFence() throws JSONException
    {
        if(mostRecentLocation != null) {
            int intersectCount = 0;
            for (int j = 0; j < polygon.size() - 1; j++) {
                if (rayCastIntersect(mostRecentLocation, polygon.get(j), polygon.get(j + 1))) {
                    intersectCount++;
                }
            }
            return ((intersectCount % 2) == 1); // odd = inside, even = outside;
        }
        else
            return false;
    }

    private boolean rayCastIntersect(Location tap, Location vertA, Location vertB) {

        double aY = vertA.getLatitude();
        double bY = vertB.getLatitude();
        double aX = vertA.getLongitude();
        double bX = vertB.getLongitude();
        double pY = tap.getLatitude();
        double pX = tap.getLongitude();

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    public boolean isRecordingLocation()
    {
        return mRequestingLocationUpdates;
    }

    public Location getMostRecentLocation() {
        return mostRecentLocation;
    }

    public String getMostRecentBeacon(){
        return mostRecentBeacon;
    }

    public void updateMapView(){

        if(FragmentFloorPlan.getFloorPlanImage() != null)
        {

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentFloorPlan.getFloorPlanImage().invalidate();
                }
            }, 0);

        }

    }
}

