package nt.vn.missingmarks.lecturer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Lecturer

class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("lecturers")

        // Initialize TextViews
        val lecturerNameTextView: TextView = view.findViewById(R.id.tv_lecturer_name)
        val lecturerSubjectTextView: TextView = view.findViewById(R.id.tv_lecturer_subject)
        val lecturerEmailTextView: TextView = view.findViewById(R.id.tv_lecturer_email)

        // Fetch the logged-in lecturer's details from Firebase
        fetchLoggedInLecturer { lecturer ->
            lecturer?.let {
                // Update UI with lecturer details
                lecturerNameTextView.text = lecturer.name
                lecturerSubjectTextView.text = lecturer.department
                lecturerEmailTextView.text = lecturer.email
            } ?: run {
                Toast.makeText(requireContext(), "Failed to fetch lecturer details", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchLoggedInLecturer(callback: (Lecturer?) -> Unit) {
        val currentUser = auth.currentUser

        // Check if a user is logged in
        if (currentUser != null) {
            val lecturerId = currentUser.email

            // Query the database using the lecturer's UID
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dsnapshot: DataSnapshot) {
                    // Get Lecturer object from snapshot
                    for(snapshot in dsnapshot.children) {
                        val name = snapshot.child("name").value.toString()
                        val department = snapshot.child("department").value.toString()
                        val email = snapshot.child("email").value.toString()
                        val lecturer = Lecturer(name = name, department = department, email = email)
                        if (email == lecturerId) {
                            callback(lecturer)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            })
        } else {
            Toast.makeText(requireContext(), "No user is currently logged in", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }
}
