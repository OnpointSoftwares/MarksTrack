package nt.vn.missingmarks.lecturer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.student

class StudentsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentsAdapter: studentAdapter
    private lateinit var database: DatabaseReference
    private lateinit var course: Course
    private var studentList = mutableListOf<student>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_students, container, false)

        // Retrieve course from arguments
        course = arguments?.getParcelable("course") ?: Course()
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.studentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with an empty list of students
        studentsAdapter = studentAdapter(studentList,course.courseId.toString())
        recyclerView.adapter = studentsAdapter

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().getReference("courses")

        // Fetch students registered for the course
        fetchStudents(course)

        return view
    }

    private fun fetchStudents(course: Course) {
        // Add a listener to the 'students' node in Firebase Realtime Database
        database.child(course.courseId.toString()).child("students") // Navigate to the correct node
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val students = mutableListOf<student>()
                    // Loop through the students in the database
                    for (studentSnapshot in snapshot.children) {
                        val age = studentSnapshot.child("age").value.toString()
                        val gender = studentSnapshot.child("gender").value.toString()
                        val name = studentSnapshot.child("name").value.toString()
                        val studentId = studentSnapshot.child("studentId").value.toString()
                        val email=studentSnapshot.child("email").value.toString()
                        val student = student(age = age, gender = gender, name = name, studentId = studentId, email = email)
                        students.add(student)
                    }
                    // Update the adapter with the new data
                    studentList.clear()
                    studentList.addAll(students)
                    studentsAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch students: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    companion object {
        // Create a new instance of StudentsFragment and pass the Course object
        fun newInstance(course: Course): StudentsFragment {
            val fragment = StudentsFragment()
            val args = Bundle()
            args.putParcelable("course", course)
            fragment.arguments = args
            return fragment
        }
    }
}
