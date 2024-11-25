package nt.vn.missingmarks.lecturer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import nt.vn.missingmarks.MainActivity
import nt.vn.missingmarks.R

class LecturerPanelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lecturer_panel)

        // Load the default fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

        // Handle bottom navigation item clicks
        val logout: ImageView =findViewById(R.id.logout)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                    true
                }
                R.id.courses -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CoursesFragment()).commit()
                    true
                }
                R.id.marks -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MarksFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }
}
