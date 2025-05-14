package com.example.ecotrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecotrack.fragment.EcoChallengeFragment;
import com.example.ecotrack.fragment.EcoServicesFragment;
import com.example.ecotrack.fragment.HomeFragment;
import com.example.ecotrack.fragment.ProfileFragment;
import com.example.ecotrack.fragment.RecycleGuidelineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private HomeFragment mHomeFragment;
    private EcoChallengeFragment mEcoChallengeFragment;
    private RecycleGuidelineFragment mRecycleGuidelineFragment;
    private EcoServicesFragment mEcoServicesFragment;
    private ProfileFragment mProfileFragment;

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in or not
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Redirect to intro
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        // Handle Intent flags for notification clicks
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("SHOW_USER_PROFILE", false)) {
                selectedFragment(4);  // ProfileFragment
            } else if (intent.getBooleanExtra("SHOW_HOME", false)) {
                selectedFragment(0);  // HomeFragment
            } else if (intent.getBooleanExtra("SHOW_ECO_CHALLENGE", false)) {
                selectedFragment(1);  // EcoChallengeFragment
            } else if (intent.getBooleanExtra("SHOW_RECYCLE_GUIDELINE", false)) {
                selectedFragment(2);  // RecycleGuidelineFragment
            } else {
                selectedFragment(0);  // Default to HomeFragment
            }
        } else {
            // Default page if no intent extras are passed
            selectedFragment(0);  // HomeFragment
        }

        // Get the Intent and check if it has the SHOW_USER_PROFILE extra
        boolean showUserProfile = getIntent().getBooleanExtra("SHOW_USER_PROFILE", false);

        // If SHOW_USER_PROFILE is true, load ProfileFragment, else load HomeFragment
        if (showUserProfile) {
            selectedFragment(4); // Index for ProfileFragment
        } else {


            // Setup bottom navigation
            mBottomNavigationView = findViewById(R.id.BottomNavigation);
            mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (item.getItemId() == R.id.HomeNavig) {
                        selectedFragment(0);
                    } else if (item.getItemId() == R.id.EcoChallengeNavig) {
                        selectedFragment(1);
                    } else if (item.getItemId() == R.id.RecycleGuidelineNavig) {
                        selectedFragment(2);
                    } else if (item.getItemId() == R.id.EcoServicesNavig) {
                        selectedFragment(3);
                    } else {
                        selectedFragment(4);
                    }
                    return true;
                }
            });

            // Load default page (HomeFragment)
            selectedFragment(0);
        }
    }


    private  void selectedFragment(int position){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);

        if (position == 0){
            if (mHomeFragment == null){
                mHomeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.Content,mHomeFragment);
            }else{
                fragmentTransaction.show(mHomeFragment);
            }
        }else if(position == 1){
            if (mEcoChallengeFragment == null){
                mEcoChallengeFragment = new EcoChallengeFragment();
                fragmentTransaction.add(R.id.Content, mEcoChallengeFragment);
            }else{
                fragmentTransaction.show(mEcoChallengeFragment);
            }
        }else if(position == 2){
            if (mRecycleGuidelineFragment == null){
                mRecycleGuidelineFragment = new RecycleGuidelineFragment();
                fragmentTransaction.add(R.id.Content, mRecycleGuidelineFragment);
            }else{
                fragmentTransaction.show(mRecycleGuidelineFragment);
            }
        }else if(position == 3){
            if (mEcoServicesFragment == null){
                mEcoServicesFragment = new EcoServicesFragment();
                fragmentTransaction.add(R.id.Content, mEcoServicesFragment);
            }else{
                fragmentTransaction.show(mEcoServicesFragment);
            }
        }else{
            if (mProfileFragment == null){
                mProfileFragment = new ProfileFragment();
                fragmentTransaction.add(R.id.Content, mProfileFragment);
            }else{
                fragmentTransaction.show(mProfileFragment);
            }
        }

        //MUST submit
        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction){

        if (mHomeFragment != null){
            fragmentTransaction.hide(mHomeFragment);
        }

        if (mEcoChallengeFragment != null){
            fragmentTransaction.hide(mEcoChallengeFragment);
        }

        if (mRecycleGuidelineFragment != null){
            fragmentTransaction.hide(mRecycleGuidelineFragment);
        }

        if (mEcoServicesFragment != null){
            fragmentTransaction.hide(mEcoServicesFragment);
        }

        if (mProfileFragment != null){
            fragmentTransaction.hide(mProfileFragment);
        }
    }
}