package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.student

class StudentAdapter(
    private val students: List<student>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentName)
        private val studentIdTextView: TextView = itemView.findViewById(R.id.studentId)
        private val studentAgeTextView: TextView = itemView.findViewById(R.id.studentAge)
        private val studentGenderTextView: TextView = itemView.findViewById(R.id.studentGender)
        private val studentEmailTextView: TextView = itemView.findViewById(R.id.studentEmail)
        private val viewMarks:Button=itemView.findViewById(R.id.view_marks)
        private val viewCourses:Button=itemView.findViewById(R.id.view_courses)
        fun bind(student: student) {
            studentNameTextView.text = student.name
            studentIdTextView.text = "ID: ${student.studentId}"
            studentAgeTextView.text = "Age: ${student.age}"
            studentGenderTextView.text = "Gender: ${student.gender}"
            studentEmailTextView.text = "Email: ${student.email}"

            // Optional: Set click listener for individual student items
            viewMarks.setOnClickListener {
                // Create a BottomSheetDialog instance
                val bottomSheetDialog = BottomSheetDialog(itemView.context)

                // Inflate the layout for the BottomSheetDialog
                val bottomSheetView = LayoutInflater.from(itemView.context).inflate(
                    R.layout.view_marks,
                    null
                )

                // Set the inflated view as the content view for the dialog
                bottomSheetDialog.setContentView(bottomSheetView)

                // Show the BottomSheetDialog
                bottomSheetDialog.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size
}
