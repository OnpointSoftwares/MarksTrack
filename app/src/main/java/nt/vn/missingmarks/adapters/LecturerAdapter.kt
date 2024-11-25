package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Lecturer

class LecturerAdapter(
    private val lecturers: List<Lecturer>
) : RecyclerView.Adapter<LecturerAdapter.LecturerViewHolder>() {

    inner class LecturerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lecturerNameTextView: TextView = itemView.findViewById(R.id.lecturerName)
        private val lecturerIdTextView: TextView = itemView.findViewById(R.id.lecturerId)
        private val viewCoursesButton: Button = itemView.findViewById(R.id.view_courses)
        private val addCourses: Button = itemView.findViewById(R.id.add_courses)
        fun bind(lecturer: Lecturer) {
            // Bind lecturer details to TextViews
            lecturerNameTextView.text = lecturer.name
            lecturerIdTextView.text = "ID: ${lecturer.lecturerId}"

            // Set up click listener for the 'View Courses' button
            viewCoursesButton.setOnClickListener {
                // Create and show the BottomSheetDialog when the button is clicked
                showCoursesDialog(lecturer)
            }
            addCourses.setOnClickListener {
                // Inflate the custom layout for the dialog
                val dialogView =
                    LayoutInflater.from(itemView.context).inflate(R.layout.dialog_add_course, null)

                // Create the AlertDialog
                val dialog = AlertDialog.Builder(itemView.context)
                    .setTitle("Add Course")
                    .setView(dialogView)
                    .setPositiveButton("Add") { dialogInterface, _ ->
                        // Capture user input from the EditTexts
                        val lecturerName = lecturer.lecturerId
                        val courseId =
                            dialogView.findViewById<EditText>(R.id.editTextCourseId).text.toString()
                        val courseName =
                            dialogView.findViewById<EditText>(R.id.editTextCourseName).text.toString()
                        val semester=dialogView.findViewById<EditText>(R.id.editTextSemester).text.toString()
                        // Create a new Course object with the input values
                        val newCourse = Course(
                            lecturer = lecturerName,
                            courseId = courseId,
                            courseName = courseName,
                            semester = semester
                        )
                        val idKey=FirebaseDatabase.getInstance().reference.push().key
                        FirebaseDatabase.getInstance().reference.child("courses").child(newCourse.courseId.toString())
                            .setValue(newCourse).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    itemView.context,
                                    "Unit added successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    itemView.context,
                                    "Unit add failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        // Do something with the newCourse, such as adding it to a list or saving it
                        // For example:
                        // lecturer.addCourse(newCourse)

                        dialogInterface.dismiss() // Close the dialog
                    }
                    .setNegativeButton("Cancel") { dialogInterface, _ ->
                        dialogInterface.dismiss() // Close the dialog
                    }
                    .create()

                // Show the dialog
                dialog.show()
            }

        }

        private fun showCoursesDialog(lecturer: Lecturer) {
            // Initialize the BottomSheetDialog
            val bottomSheetDialog = BottomSheetDialog(itemView.context)

            // Inflate the layout for the dialog
            val bottomSheetView = LayoutInflater.from(itemView.context)
                .inflate(R.layout.view_courses, null)

            // Find the TextView where courses will be displayed
            val coursesTextView: TextView = bottomSheetView.findViewById(R.id.coursesTextView)

            // Show a loading message while fetching data
            coursesTextView.text = "Loading courses..."

            // Query Firebase to get the courses for the specific lecturer
            val databaseReference = FirebaseDatabase.getInstance().reference
            val coursesQuery = databaseReference.child("courses")

            // Attach a ValueEventListener to the query
            coursesQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Prepare a list of courses to display
                        val courseList = mutableListOf<String>()
                        for (courseSnapshot in dataSnapshot.children) {
                            if(courseSnapshot.child("lecturer").value==lecturer.lecturerId.toString()) {
                                val courseName = courseSnapshot.child("courseName").value.toString()
                                val courseId = courseSnapshot.child("courseId").value.toString()
                                courseList.add("${courseName} (ID: ${courseId})")
                            }
                        }

                        // Check if there are any courses for the lecturer
                        if (courseList.isNotEmpty()) {
                            // Display the list of courses in the TextView
                            coursesTextView.text = courseList.joinToString(separator = "\n")
                        } else {
                            // If no courses are found for this lecturer, show a placeholder message
                            coursesTextView.text = "No courses available"
                        }
                    } else {
                        // If the query returns no results, show a placeholder message
                        coursesTextView.text = "No courses available"
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error case (e.g., permission denied or network issue)
                    coursesTextView.text = "Failed to load courses"
                    Toast.makeText(
                        itemView.context,
                        "Error loading courses: ${databaseError.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })



            // Set the BottomSheetDialog view and show the dialog
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

        // Create ViewHolder by inflating item_lecturer layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LecturerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lecturer, parent, false)
        return LecturerViewHolder(view)
    }

    // Bind data to the ViewHolder at the given position
    override fun onBindViewHolder(holder: LecturerViewHolder, position: Int) {
        holder.bind(lecturers[position])
    }

    // Return the total number of lecturers
    override fun getItemCount(): Int = lecturers.size
}
