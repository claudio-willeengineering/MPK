package de.dfki.mpk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

import de.dfki.mpk.fragments.BaseFragment;
import de.dfki.mpk.fragments.FragmentFeedback;
import de.dfki.mpk.fragments.FragmentPass;
import de.dfki.mpk.fragments.Fragment_Home;
import de.dfki.mpk.model.Exhibits;
import de.dfki.mpk.model.Topic;
import de.dfki.mpk.utils.UtilsHelpers;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    SubsamplingScaleImageView imageView;
    FrameLayout layout;

    public JSONObject jsonData = new JSONObject();
    HashMap<String,Exhibits> exhibits = new HashMap<>();
    HashMap<String,Topic> topics = new HashMap<>();

    List<Fragment> fragments = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle(getString(R.string.home_title));


        layout = (FrameLayout) findViewById(R.id.fragmentContainer);


        jsonData = UtilsHelpers.fromRawToJson(this,R.raw.content);
        buildExhibitionItems();

        switchFragment(Fragment_Home.createInstance());


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
                            switchFragment(Fragment_Home.createInstance());
                        }


                    }
                });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_pass:
                        switchFragment(FragmentPass.createInstance());
                        break;
                    case R.id.feedback:
                        switchFragment(FragmentFeedback.createInstance());
                        break;
                }
                return false;
            }
        });




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
                      exhibits.put(e.getId(), e );
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

        if(((MPKApplication)getApplication()).isFirstRun())
        {
            Intent i = new Intent(this, FirstScreenActivity.class);
            startActivity(i);
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

    public void switchFragment(BaseFragment frag)
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
            frag.id = getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, frag).addToBackStack(frag.title).commit();
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




    public List<Topic> getTopics() {
        List<Topic> ts = new ArrayList<>();
        ts.addAll(this.topics.values());
        Collections.sort(ts);
        return ts;
    }

    public List<Exhibits> getExhibits() {
        List<Exhibits> es = new ArrayList<>();
        es.addAll(this.exhibits.values());
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
               e.add(exhibits.get(r));
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

    public boolean hasAudioPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }

}

