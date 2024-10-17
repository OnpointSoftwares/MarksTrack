package nt.vn.missingmarks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class StudentPanel : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_panel)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnav)
        val logout: ImageView =findViewById(R.id.logout)
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
                R.id.marks -> {
                    loadFragment(StudentMarksFragment())
                    true
                }
                R.id.courses-> {
                    loadFragment(StudentCoursesFragment())
                    true
                }
                else -> false
            }
        }
    }



    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
