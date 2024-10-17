package nt.vn.missingmarks
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var registerButton: Button
    private lateinit var phoneField:EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        registerButton=findViewById(R.id.registerButton)
        emailField=findViewById(R.id.emailField)
        phoneField=findViewById(R.id.phoneField)
        passwordField=findViewById(R.id.passwordField)
        confirmPasswordField=findViewById(R.id.confirmPasswordField)
        // Set up the register button listener
        registerButton.setOnClickListener {
            val phone = phoneField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (validateInput(phone, email, password, confirmPassword)) {
                registerUser(email, password, phone)
            }
        }
    }

    // Function to validate input
    private fun validateInput(phone: String, email: String, password: String, confirmPassword: String): Boolean {
        if (phone.isEmpty()) {
            phoneField.error = "Phone number is required"
            phoneField.requestFocus()
            return false
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneField.error = "Please enter a valid phone number"
            phoneField.requestFocus()
            return false
        }

        if (email.isEmpty()) {
            emailField.error = "Email is required"
            emailField.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.error = "Please enter a valid email"
            emailField.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            passwordField.error = "Password is required"
            passwordField.requestFocus()
            return false
        }

        if (password.length < 6) {
            passwordField.error = "Password should be at least 6 characters long"
            passwordField.requestFocus()
            return false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordField.error = "Please confirm your password"
            confirmPasswordField.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordField.error = "Passwords do not match"
            confirmPasswordField.requestFocus()
            return false
        }

        return true
    }

    // Function to register a new user with Firebase
    private fun registerUser(email: String, password: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // Store additional user info like phone number in the database (optional)
                    // val user = FirebaseAuth.getInstance().currentUser
                    // val uid = user?.uid

                    // Proceed to next activity or update UI
                } else {
                    // Registration failed
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
