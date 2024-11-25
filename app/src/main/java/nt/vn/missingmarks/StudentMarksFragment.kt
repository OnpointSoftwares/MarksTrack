package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import nt.vn.missingmarks.lecturer.MarksAdapter
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Mark

class StudentMarksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var marksAdapter: MarksAdapter
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize empty list of marks
        val marksList = ArrayList<Mark>()

        // Fetch data from Firebase
        FirebaseDatabase.getInstance().reference.child("marks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    marksList.clear() // Clear previous data to avoid duplication

                    for (snapshotd in snapshot.children) {
                        for (dsnapshot in snapshotd.children) {
                            // Assuming you have structure: "marks/{studentId}/{courseId}/{score}"
                            val courseName =
                                dsnapshot.child("course").child("courseName").value.toString()
                            val courseId =
                                dsnapshot.child("course").child("courseId").value.toString()
                            val lecturer =
                                dsnapshot.child("course").child("lecturer").value.toString()
                            val score = dsnapshot.child("examMark")
                                .value.toString() // null if not available
                            val catscore = dsnapshot.child("catMark")
                                .value.toString()
                            val semester=dsnapshot.child("course").child("semester").value.toString()
                            val course = Course(lecturer, courseName, courseId, semester = semester)
                            val mark = Mark(course =course, exammark = score, catmark = catscore)
                            marksList.add(mark)
                        }
                    }

                    // After fetching data, update the adapter
                    marksAdapter = MarksAdapter(marksList)
                    recyclerView.adapter = marksAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database errors here
                    Log.e("FirebaseError", "Error: ${error.message}")
                }
            })

        return view
    }
}


