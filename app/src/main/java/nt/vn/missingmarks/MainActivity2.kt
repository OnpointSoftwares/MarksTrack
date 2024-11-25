package nt.vn.missingmarks

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import nt.vn.missingmarks.AdminMissingMarksFragment

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val logout:ImageView=findViewById(R.id.logout)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this,MainActivity::class.java))
        }
        // Load the default fragment (e.g., Dashboard)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
        }

        // Handle navigation item selection
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.navigation_lecturers -> {
                    loadFragment(LecturerFragment())
                    true
                }
                R.id.navigation_students -> {
                    loadFragment(StudentFragment())
                    true
                }
                R.id.navigation_missing_marks -> {
                    loadFragment(CoursesFragment())
                    true
                }
                R.id.missing_marks -> {
                    loadFragment(AdminMissingMarksFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_courses -> {
                // Handle the "Courses" menu item
                true
            }
            R.id.menu_logout -> {
                // Handle the "Logout" menu item
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
