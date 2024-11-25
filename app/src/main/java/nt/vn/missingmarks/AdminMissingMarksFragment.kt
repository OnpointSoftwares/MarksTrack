package nt.vn.missingmarks

import android.content.Context
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
import nt.vn.missingmarks.adapters.MissingMarksAdapter
// Make sure to use the appropriate adapter for marks
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.MissingMarks

class AdminMissingMarksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var marksAdapter: MissingMarksAdapter
    private lateinit var database: DatabaseReference
    private lateinit var course: Course
    private var missingMarksList = mutableListOf<MissingMarks>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_marks, container, false) // Ensure you have a correct layout

        // Retrieve course from arguments
        course = arguments?.getParcelable("course") ?: Course() // Ensure course is retrieved correctly
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_marks) // Ensure correct ID for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with an empty list of missing marks
        marksAdapter = MissingMarksAdapter(missingMarksList)
        recyclerView.adapter = marksAdapter

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().getReference("missingMarks")

        // Fetch missing marks for the course
        fetchMissingMarks(course,requireContext())

        return view
    }

    private fun fetchMissingMarks(course: Course,context: Context) {
        // Add a listener to the 'missingMarks' node in Firebase Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val missingMarks = mutableListOf<MissingMarks>()
                // Loop through the missing marks in the database
                for (MarkSnapshot in snapshot.children) {
                    for (missingMarkSnapshot in MarkSnapshot.children) {
                        var studentId = missingMarkSnapshot.child("studentId").value.toString()
                        var subject = missingMarkSnapshot.child("subject").value.toString()
                        var dueDate = missingMarkSnapshot.child("dueDate").value.toString()
                        var markStatus = missingMarkSnapshot.child("markStatus").value.toString()
                        var semester=missingMarkSnapshot.child("semester").value.toString()
                        var exammark=missingMarkSnapshot.child("examMark").value.toString()
                        var catmark=missingMarkSnapshot.child("catMark").value.toString()
                        // Create MissingMarks object
                        val missingMark = MissingMarks(studentId=studentId, subject = subject, semester = semester, dueDate = dueDate, markStatus = markStatus, examMark = exammark, catMark = catmark)
if(missingMark.markStatus=="submitted")
{
    missingMarks.add(missingMark)
}

                    }
                }
                // Update the adapter with the new data
                missingMarksList.clear()
                missingMarksList.addAll(missingMarks)
                marksAdapter.notifyDataSetChanged() // Notify adapter of data change
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch missing marks: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        // Create a new instance of MarksFragment and pass the Course object
        fun newInstance(course: Course): AdminMissingMarksFragment {
            val fragment = AdminMissingMarksFragment()
            val args = Bundle()
            args.putParcelable("course", course)
            fragment.arguments = args
            return fragment
        }
    }
}
