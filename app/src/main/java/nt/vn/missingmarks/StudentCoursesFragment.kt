package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.adapters.CourseAdapter
import nt.vn.missingmarks.models.Course

class StudentCoursesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var courseList: MutableList<Course>
    private lateinit var database: DatabaseReference
    private lateinit var addCourseButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_courses, container, false)
        addCourseButton = view.findViewById(R.id.newCourse)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.courseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        courseList = mutableListOf()

        addCourseButton.setOnClickListener {
            showAddCourseDialog()
        }

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("courses")

        // Fetch and set the course data from Firebase
        fetchCoursesFromFirebase()

        return view
    }

    private fun showAddCourseDialog() {
        // Create a dialog to add course
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_select_course, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add New Course")


        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                courseList.clear()

                for (courseSnapshot in dataSnapshot.children) {
                    val courseId = courseSnapshot.child("courseId").getValue(String::class.java) ?: ""
                    val courseName = courseSnapshot.child("courseName").getValue(String::class.java) ?: ""

                    val course = Course(
                        courseId = courseId,
                        courseName = courseName
                    )

                    courseList.add(course)
                }

                courseAdapter = CourseAdapter(courseList)
                val recyclerView=dialogView.findViewById<RecyclerView>(R.id.coursesRv)
                recyclerView.adapter = courseAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("CoursesFragment", "loadCourse:onCancelled", databaseError.toException())
            }
        })
        val alertDialog = builder.show()
    }

    private fun fetchCoursesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                courseList.clear()

                for (courseSnapshot in dataSnapshot.children) {
                    val courseId = courseSnapshot.child("courseId").getValue(String::class.java) ?: ""
                    val courseName = courseSnapshot.child("courseName").getValue(String::class.java) ?: ""

                    val course = Course(
                        courseId = courseId,
                        courseName = courseName
                    )

                    courseList.add(course)
                }

                courseAdapter = CourseAdapter(courseList)
                recyclerView.adapter = courseAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("CoursesFragment", "loadCourse:onCancelled", databaseError.toException())
            }
        })
    }
}
