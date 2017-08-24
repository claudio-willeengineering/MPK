package de.dfki.mpk;

import android.content.Intent;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import de.dfki.mpk.fragments.BaseFragment;
import de.dfki.mpk.fragments.FragmentFeedback;
import de.dfki.mpk.fragments.FragmentPass;
import de.dfki.mpk.fragments.Fragment_Home;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    SubsamplingScaleImageView imageView;
    FrameLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle(getString(R.string.home_title));


        layout = (FrameLayout) findViewById(R.id.fragmentContainer);
        // Add the fragment to the 'fragment_container' FrameLayout

        switchFragment(Fragment_Home.createInstance());

        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if(frag != null) {

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






    @Override
    protected void onResume() {
        super.onResume();

        if(((MPKApplication)getApplication()).isFirstRun())
        {
            Intent i = new Intent(this, FirstScreenActivity.class);
            startActivity(i);
        }
        else
        {

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


        if(getSupportFragmentManager().getFragments()!= null && getSupportFragmentManager().getFragments().contains(frag))
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

}
