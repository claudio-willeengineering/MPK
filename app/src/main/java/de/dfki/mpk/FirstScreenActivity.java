package de.dfki.mpk;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import de.dfki.mpk.utils.CustomViewPager;
import de.dfki.mpk.utils.UtilsHelpers;


/**
 * Created by Olakunmi on 06/08/2017.
 */

public class FirstScreenActivity extends AppCompatActivity {

    public static final int PERMISSION_LOCATION_AND_BEACONS = 2323;
    Activity activity;
    private Toolbar toolbar;
    CustomViewPager mViewPager;
    LinearLayout indicator;
    Menu menu;


    //Facebook
    CallbackManager callbackManager;
    AlertDialog fbDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screens);



        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "de.dfki.mpk",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature .toByteArray());
                Log.d("Kunmi:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }




        activity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setSubtitle(R.string.first_screens_title);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<int[]> content = new ArrayList<>();

        content.add(new int[]{R.string.placeholder,R.drawable.placeholder});
        content.add(new int[]{R.string.placeholder,R.drawable.placeholder});
        content.add(new int[]{R.string.placeholder,R.drawable.placeholder});
        content.add(new int[]{R.string.placeholder,R.drawable.placeholder});
        content.add(new int[]{R.string.placeholder,R.drawable.placeholder});


        final List<View> indicatorCollection = new ArrayList<>();

        indicatorCollection.add(findViewById(R.id.indicator0));
        indicatorCollection.add(findViewById(R.id.indicator1));
        indicatorCollection.add(findViewById(R.id.indicator2));
        indicatorCollection.add(findViewById(R.id.indicator3));
        indicatorCollection.add(findViewById(R.id.indicator4));


        ScreenAdapter adapter = new ScreenAdapter(content);


        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(4);

        indicatorCollection.get(0).setBackgroundResource(R.drawable.slide_indicator_selected);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(View v : indicatorCollection)
                {
                    v.setBackgroundResource(R.drawable.slide_indicator);
                }
                indicatorCollection.get(position).setBackgroundResource(R.drawable.slide_indicator_selected);


                if(position == 1)
                    mViewPager.enableSwipe(false);
                else
                    mViewPager.enableSwipe(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(0, true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
/*
            case R.id.first_screen_menu_close:
                finish();
                break;
                */
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
/*        getMenuInflater().inflate(R.menu.first_screen,menu);

        menu.getItem(0).setVisible(false);
*/
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        mViewPager.enableSwipe(true);
        switch (requestCode) {
            case FirstScreenActivity.PERMISSION_LOCATION_AND_BEACONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mViewPager.setCurrentItem(2, true);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    UtilsHelpers.showErrorDialog(this, getString(R.string.permission_denied_dialog_title), getString(R.string.permission_denied_dialog_body));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public class ScreenAdapter extends PagerAdapter {

            List<int[]> data = new ArrayList<>();

            public ScreenAdapter(List<int[]> data) {
                this.data = data;

            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                View rootView;
                //Welcome
                if (position == 0) {

                    rootView = getLayoutInflater().inflate(R.layout.f1st_fragment_welcome, container, false);
                }
                //Permission
                else if (position == 1) {

                    rootView = getLayoutInflater().inflate(R.layout.f1st_fragment_app_permission, container, false);
                    rootView.findViewById(R.id.grantBeaconPermission).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Toast.makeText(activity,"Button Clicked",Toast.LENGTH_SHORT).show();

                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSION_LOCATION_AND_BEACONS);
                            mViewPager.enableSwipe(true);
                        }
                    });

                    rootView.findViewById(R.id.denyBeaconPermission).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mViewPager.enableSwipe(true);
                            mViewPager.setCurrentItem(position+1,true);
                        }
                    });
                }

                //Science Permission
                else if (position == 2) {

                    rootView = getLayoutInflater().inflate(R.layout.f1st_fragment_permission_science, container, false);
                    final EditText editText = rootView.findViewById(R.id.emailBox);
                    rootView.findViewById(R.id.grantBeaconPermission).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!UtilsHelpers.isValidEmail(editText.getText()))
                            {
                                UtilsHelpers.showErrorDialog(activity,"Ungültige E-Mail",
                                        "Bitte geben Sie eine gültige E-Mail-Adresse ein");
                                return;
                            }

                            ((MPKApplication)activity.getApplication()).setEmail(editText.getText().toString());
                            mViewPager.setCurrentItem(position+1);

                        }
                    });


                    rootView.findViewById(R.id.denyBeaconPermission).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mViewPager.enableSwipe(true);
                            mViewPager.setCurrentItem(3,true);
                        }
                    });
                }

                //Faebook Login
                else if (position == 3) {
                    rootView = getLayoutInflater().inflate(R.layout.f1st_fragment_facebook_login, container, false);

                    //FB
                    callbackManager = CallbackManager.Factory.create();

                    LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
                    loginButton.setReadPermissions("email");
                    // If using in a fragment
                    //loginButton.setFragment(this);

                    // Callback registration
                    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                            dialog.setTitle(R.string.dialog_facebook_title);
                            dialog.setMessage(R.string.dialog_facebook_message);



                            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    fbDialog.dismiss();
                                    mViewPager.setCurrentItem(4);
                                }
                            });

                            fbDialog = dialog.create();

                            fbDialog.show();
                        }

                        @Override
                        public void onCancel() {
                            UtilsHelpers.showErrorDialog(activity,getString(R.string.dialog_facebook_title),
                                    getString(R.string.dalog_facebook_error_cancel_message));
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            UtilsHelpers.showErrorDialog(activity,getString(R.string.dialog_facebook_title),
                                    getString(R.string.dalog_facebook_error_cancel_message));
                        }
                    });
                }

                else if(position == 4)
                {
                    rootView = getLayoutInflater().inflate(R.layout.f1st_fragment_questionnaire, container, false);


                    rootView.findViewById(R.id.finishButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((MPKApplication)getApplication()).setHasRunOnce();
                            activity.finish();
                        }
                    });

                }

                else {
                    rootView = getLayoutInflater().inflate(R.layout.fragment_more_crowd_sensing, container, false);
                }
                container.addView(rootView);

                return rootView;
            }


            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object obj) {
                return view == obj;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((LinearLayout) object);
            }
        }

}

