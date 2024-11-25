package nt.vn.missingmarks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.adapters.StudentsCourseAdapter
import nt.vn.missingmarks.models.Course

class RegisterCourse : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseAdapter: StudentsCourseAdapter
    private lateinit var courseList: MutableList<Course>
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_course)
        recyclerView=findViewById(R.id.courseRecyclerView)
        database=FirebaseDatabase.getInstance().getReference("courses")
        courseList = mutableListOf()
        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchCoursesFromFirebase()
    }
    private fun fetchCoursesFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                courseList.clear()

                for (courseSnapshot in dataSnapshot.children) {
                    val courseId = courseSnapshot.child("courseId").value.toString() ?: ""
                    val courseName = courseSnapshot.child("courseName").value.toString() ?: ""
                    val semester= courseSnapshot.child("semester").value.toString() ?: ""
                    val course = Course(
                        courseId = courseId,
                        courseName = courseName,
                        semester = semester
                    )

                    courseList.add(course)
                }

                courseAdapter = StudentsCourseAdapter(courseList)
                recyclerView.adapter = courseAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("CoursesFragment", "loadCourse:onCancelled", databaseError.toException())
            }
        })
    }

}