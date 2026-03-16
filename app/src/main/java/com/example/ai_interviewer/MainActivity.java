package com.example.ai_interviewer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Check if Performance Fragment should open
        String fragmentToOpen = getIntent().getStringExtra("openFragment");

        if (fragmentToOpen != null && fragmentToOpen.equals("performance")) {

            int score = getIntent().getIntExtra("score", 0);
            int correct = getIntent().getIntExtra("correct", 0);
            int wrong = getIntent().getIntExtra("wrong", 0);

            Bundle bundle = new Bundle();
            bundle.putInt("score", score);
            bundle.putInt("correct", correct);
            bundle.putInt("wrong", wrong);

            PerformanceFragment performanceFragment = new PerformanceFragment();
            performanceFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, performanceFragment)
                    .commit();

            bottomNavigationView.setSelectedItemId(R.id.nav_performance);

        } else {

            // Default Home Fragment
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // Bottom Navigation Click Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            if (item.getItemId() == R.id.nav_home) {
                fragment = new HomeFragment();
            }

            else if (item.getItemId() == R.id.nav_performance) {
                fragment = new PerformanceFragment();
            }

            else if (item.getItemId() == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    // OPEN PROFILE TAB
    public void openProfileTab() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    // OPEN PERFORMANCE TAB
    public void openPerformanceTab() {
        bottomNavigationView.setSelectedItemId(R.id.nav_performance);
    }
}