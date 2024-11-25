package nt.vn.missingmarks.lecturer

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.student

class studentAdapter(
    private val students: List<student>,private val course:String
) : RecyclerView.Adapter<studentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentName)
        private val studentIdTextView: TextView = itemView.findViewById(R.id.studentId)
        private val studentAgeTextView: TextView = itemView.findViewById(R.id.studentAge)
        private val studentGenderTextView: TextView = itemView.findViewById(R.id.studentGender)
        private val studentEmailTextView: TextView = itemView.findViewById(R.id.studentEmail)
        private val marksTv:TextView=itemView.findViewById(R.id.tv_marks)
        private val awardMarks: Button = itemView.findViewById(R.id.award_marks)

        fun bind(student: student) {
            var marks:String=""
            var catmarks:String=""
            val sanitizedId=sanitizekey(student.studentId)
            FirebaseDatabase.getInstance().reference.child("marks").child(sanitizedId).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(dsnapshot in snapshot.children) {

                        if(dsnapshot.child("course").child("courseId").value.toString()==course)
                        marks = dsnapshot.child("examMark").value.toString()
                        catmarks = dsnapshot.child("catMark").value.toString()
                        marksTv.text = "Marks:Exam:${marks} Cat marks: ${catmarks}"
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            studentNameTextView.text = student.name
            studentIdTextView.text = "ID: ${student.studentId}"
            studentAgeTextView.text = "Age: ${student.age}"
            studentGenderTextView.text = "Gender: ${student.gender}"
            studentEmailTextView.text = "Email: ${student.email}"
            // Set click listener for awarding marks
            awardMarks.setOnClickListener {
                showAwardMarksDialog(itemView.context, student)
            }
        }
    }
        fun sanitizekey(key: String): String {
            return key.replace("/", "_")
        }

    private fun showAwardMarksDialog(context: Context, student: student) {
        // Create an AlertDialog to input marks
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Award Marks for ${student.name}")

        // Set up the input field
        val input = EditText(context)
        input.hint = "Enter marks"
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Award") { dialog, _ ->
            val marksInput = input.text.toString().trim()
            if (marksInput.isNotEmpty()) {
                // Handle the awarding of marks (you might want to update the database or your data model here)
                awardMarksToStudent(student, marksInput,context)
            } else {
                Toast.makeText(context, "Please enter valid marks", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun awardMarksToStudent(student: student, marks: String,context:Context) {
        // Here you can implement the logic to award marks to the student
        // For example, you might want to save the awarded marks to your database

        // Assuming you have a method or a reference to store the marks
        saveMarksToDatabase(student.studentId, marks)

        Toast.makeText(context, "Marks $marks awarded to ${student.name}", Toast.LENGTH_SHORT).show()
        // Implement your logic to update the database as needed
    }

    private fun saveMarksToDatabase(studentId: String, marks: String) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_lecturer, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size
}
