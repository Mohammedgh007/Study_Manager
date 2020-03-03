/*
###############################################################################
Class name : MainActivity
purpose: This is model view class that manages showing the left side menu with
   moving from an activity to another.
Methods:
  onCreateView() -> It encapsulates/manages all the interaction.
###############################################################################
 */

package creative.developer.m.studymanager.modelview;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.View;

import creative.developer.m.studymanager.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    // Notes and Flash Cards depends on the same Fragment to determine the lesson.
    private static String coursesDistination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Initializing views
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        // setting up the left side menu
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        navView.setNavigationItemSelectedListener((item) -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    toolbar.setTitle("Home");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeActivity()).commit();
                    break;
                case R.id.nav_assignment:
                    toolbar.setTitle("Assignments");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AssignmentActivity()).commit();
                    break;
                case R.id.nav_remark:
                    toolbar.setTitle("Remarks");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new RemarksActivity()).commit();
                    break;
                case R.id.nav_notes:
                    toolbar.setTitle("Notes");
                    coursesDistination = "notes activity";
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CoursesActivity()).commit();
                    break;
                case R.id.nav_flash:
                    toolbar.setTitle("Flash Cards");
                    coursesDistination = "flash cards activity";
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new CoursesActivity()).commit();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            toolbar.setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeActivity()).commit();
            navView.setCheckedItem(R.id.nav_home);
        }

        // checking if this activity is opened by FlashCardsActivity, if so then MainActivity
        // will launch directly CourseActivity
        if (this.getIntent() != null && this.getIntent().hasExtra("finalDistanation")) {
            getSupportActionBar().setTitle("Flash Cards");
            coursesDistination = "flash cards activity";
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new CoursesActivity()).commit();
            navView.setCheckedItem(R.id.nav_flash);
        }

    }

    // It is called so that when the user click back button while the left list is opened, the
    @Override // app would not close.
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START); // with non-behaved users
        } else {
            super.onBackPressed(); // usual way of closing
        }
    }

    /*
    this is a getter for coursesDistination, so that it is to send an information to CoursesActivity.
     */
    protected static String getCoursesDistination() {return coursesDistination;}

}
