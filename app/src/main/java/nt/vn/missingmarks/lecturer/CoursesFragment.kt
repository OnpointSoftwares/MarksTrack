package nt.vn.missingmarks.lecturer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course

class CoursesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var database: DatabaseReference
    private var lecid:String=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_courses_lecturer, container, false)

        // Initialize RecyclerView with LinearLayoutManager
        recyclerView = view.findViewById(R.id.recycler_view_courses)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the adapter with an empty course list and listener for course selection
        courseAdapter = CourseAdapter(emptyList()) { course ->
            viewStudents(course)
        }
        recyclerView.adapter = courseAdapter

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("courses")

        // Fetch and update courses from Firebase
        fetchCourses()

        return view
    }

    private fun fetchCourses() {
        FirebaseDatabase.getInstance().reference.child("lecturers").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dsnapshot in snapshot.children)
                {
                    if(dsnapshot.child("email").value.toString()==FirebaseAuth.getInstance().currentUser!!.email)
                    {

                        lecid=dsnapshot.child("lecturerId").value.toString()
                        Toast.makeText(requireContext(),lecid,Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        // Attach listener to Firebase database to get courses
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = mutableListOf<Course>()
                for (courseSnapshot in snapshot.children) {
                    val lecturer=courseSnapshot.child("lecturer").value.toString()
                    val courseId=courseSnapshot.child("courseId").value.toString()
                    val courseName=courseSnapshot.child("courseName").value.toString()
                    val students=courseSnapshot.child("students").value.toString()
                    val semester=courseSnapshot.child("semester").value.toString()
                    if(lecturer==lecid) {
                        courses.add(Course(lecturer, courseId, courseName, semester = semester))
                    }
                }
                // Update the RecyclerView adapter with fetched courses
                courseAdapter.updateCourses(courses)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch courses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun viewStudents(course: Course) {
        Toast.makeText(requireContext(), "Viewing students for ${course.courseName}", Toast.LENGTH_SHORT).show()

        // Navigate to the StudentsFragment to view students in the selected course
        val studentsFragment = StudentsFragment.newInstance(course)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, studentsFragment)
            .addToBackStack(null)
            .commit()
    }
}
