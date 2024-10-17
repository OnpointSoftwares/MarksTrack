package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import nt.vn.missingmarks.adapters.StudentAdapter
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Lecturer
import nt.vn.missingmarks.models.Mark
import nt.vn.missingmarks.models.student

class DashboardFragment : Fragment() {

    private lateinit var textViewTotalStudents: TextView
    private lateinit var textViewTotalLecturers: TextView
    private lateinit var studentList: MutableList<student>
    private lateinit var lecturerList: MutableList<Lecturer>
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Initialize the TextViews
        textViewTotalStudents = view.findViewById(R.id.textViewTotalStudents)
        textViewTotalLecturers = view.findViewById(R.id.textViewTotalLecturers)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Fetch and set the student and lecturer data from Firebase
        fetchStudentsFromFirebase()
        fetchLecturersFromFirebase()

        return view
    }

    private fun fetchStudentsFromFirebase() {
        database.child("students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val studentCount = dataSnapshot.childrenCount.toInt()

                // Display total students in TextView
                textViewTotalStudents.text = "Total Students: $studentCount"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("DashboardFragment", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun fetchLecturersFromFirebase() {
        database.child("lecturers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lecturerCount = dataSnapshot.childrenCount.toInt()

                // Display total lecturers in TextView
                textViewTotalLecturers.text = "Total Lecturers: $lecturerCount"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("DashboardFragment", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}
