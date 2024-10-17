package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import nt.vn.missingmarks.adapters.StudentAdapter
import nt.vn.missingmarks.adapters.StudentMarksAdapter
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Lecturer
import nt.vn.missingmarks.models.Mark
import nt.vn.missingmarks.models.student

class StudentMarksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentmarksAdapter: StudentMarksAdapter
    private lateinit var studentmarksList: MutableList<Mark>
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_marks, container, false)
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.studentmarksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        studentmarksList = mutableListOf()
        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("marks").child("id")

        // Fetch and set the student data from Firebase
        fetchStudentsFromFirebase()

        return view
    }


    private fun fetchStudentsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                studentmarksList.clear()

                for (studentSnapshot in dataSnapshot.children) {
                    val studentId =
                        studentSnapshot.child("studentId").getValue(String::class.java) ?: ""
                    val marks = studentSnapshot.child("mark").getValue(String::class.java) ?: ""
                    val course=studentSnapshot.child("course").value as Course
                    val studentmarks = Mark(
                        course = course,
                        mark = marks.toDouble()
                    )

                    studentmarksList.add(studentmarks)
                }

                studentmarksAdapter = StudentMarksAdapter(studentmarksList)
                recyclerView.adapter = studentmarksAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("StudentFragment", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}

