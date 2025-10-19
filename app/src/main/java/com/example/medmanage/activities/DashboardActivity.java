package com.example.medmanage.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.medmanage.R;
import com.example.medmanage.databinding.DashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    // 2. CHANGE: Declare the correct binding class
    private DashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.topBar.toolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView greetingTextView = binding.topBar.greetingText;

        Intent intent = getIntent();
        String userType = intent.getStringExtra("USER_TYPE");

        String greetingMessage = "Hi there 👋"; // Set a default greeting

        if (userType != null) {
            if (userType.equals("student")) {
                String name = intent.getStringExtra("NAME");
                if (name != null && !name.isEmpty()) {

                    greetingMessage = "Hi " + name + " 👋";
                }
            } else if (userType.equals("nurse")) {
                String surname = intent.getStringExtra("SURNAME");
                if (surname != null && !surname.isEmpty()) {
                    greetingMessage = "Hi Nurse " + surname + " 👋";
                }
            }
        }

// 4. Set the final text
        greetingTextView.setText(greetingMessage);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_schedule, R.id.navigation_profile)
                .build();


        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}

