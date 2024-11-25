package nt.vn.missingmarks

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.lecturer.LecturerPanelActivity

class MainActivity : AppCompatActivity() {

    // Declare FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
if(auth.uid != "")
{
    FirebaseDatabase.getInstance().reference.child("users").child(auth.uid.toString()).addValueEventListener(object:ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.value=="student")
            {
                startActivity(Intent(this@MainActivity, StudentPanel::class.java))
                Toast.makeText(this@MainActivity,"Student",Toast.LENGTH_LONG).show()
            }
            else if(snapshot.value=="lecturer")
            {
                Toast.makeText(this@MainActivity,"Lecturer",Toast.LENGTH_LONG).show()
                startActivity(Intent(this@MainActivity,LecturerPanelActivity::class.java))
            }
            else if(snapshot.value=="admin")
            {
                startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                finish()
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
}
        // Get references to UI elements
        val emailEditText = findViewById<EditText>(R.id.emailField)
        val passwordEditText = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val regBtn=findViewById<Button>(R.id.registerButton)
        val pb:ProgressBar=findViewById(R.id.progress)
        // Handle login button click
        regBtn.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
        loginButton.setOnClickListener {
            pb.visibility= View.VISIBLE
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    // Function to login the user with email and password
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful, navigate to main activity
                    val user = auth.currentUser
                    Toast.makeText(this, "Login successful: ${user?.email}", Toast.LENGTH_SHORT).show()
                    findViewById<ProgressBar>(R.id.progress).visibility= View.GONE
                   FirebaseDatabase.getInstance().reference.child("users").child(user!!.uid).addValueEventListener(object:ValueEventListener{
                       override fun onDataChange(snapshot: DataSnapshot) {
                           if(snapshot.value=="student")
                           {
                               startActivity(Intent(this@MainActivity, StudentPanel::class.java))
                               Toast.makeText(this@MainActivity,"Student",Toast.LENGTH_LONG).show()
                           }
                           else if(snapshot.value=="lecturer")
                           {
                               startActivity(Intent(this@MainActivity, LecturerPanelActivity::class.java))
                               Toast.makeText(this@MainActivity,"Lecturer",Toast.LENGTH_LONG).show()
                           }
                           else if(snapshot.value=="admin")
                           {
                               startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                               finish()
                           }

                       }

                       override fun onCancelled(error: DatabaseError) {

                       }

                   })
                    // Prevent going back to the login screen
                } else {
                    // Login failed, display error message
                    Log.e("LoginActivity", "Login failed", task.exception)
                    findViewById<ProgressBar>(R.id.progress).visibility= View.GONE
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
