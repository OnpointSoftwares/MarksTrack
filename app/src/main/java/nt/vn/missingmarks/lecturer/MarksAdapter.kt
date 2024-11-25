package nt.vn.missingmarks.lecturer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Mark
import nt.vn.missingmarks.models.MissingMarks
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MarksAdapter(private var marks: List<Mark>) : RecyclerView.Adapter<MarksAdapter.MarkViewHolder>() {

    class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.tv_course_name)
        val mark: TextView = itemView.findViewById(R.id.tv_mark)
        val btnApply: Button = itemView.findViewById(R.id.btnApply)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mark_lecturer, parent, false)
        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        val currentMark = marks[position]

        // Set course name
        holder.courseName.text = currentMark.course.courseName

        // Set mark or display "Missing" if mark is null
        if (currentMark.exammark == "" || currentMark.exammark =="null") {
            holder.mark.text = "Exam marks Missing"
            holder.btnApply.visibility = View.VISIBLE
            holder.mark.setTextColor(Color.RED) // Display in red if missing
        }
        else if (currentMark.catmark == ""||currentMark.catmark =="null") {
            holder.mark.text = "Cat Marks Missing"
            holder.btnApply.visibility = View.VISIBLE
            holder.mark.setTextColor(Color.RED) // Display in red if missing
        }
        else if (currentMark.exammark == ""&& currentMark.catmark =="") {
            holder.mark.text = "Both exam and cat marks are missing"
            holder.btnApply.visibility = View.VISIBLE
            holder.mark.setTextColor(Color.RED) // Display in red if missing
        }
        else {
            holder.mark.text = "exam mark:${currentMark.exammark} cat marks: ${currentMark.catmark}"
            holder.mark.setTextColor(Color.BLACK) // Normal color for non-missing marks
            holder.btnApply.visibility = View.GONE // Hide button if mark is not missing
        }

        holder.btnApply.setOnClickListener {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            // Inflate the custom layout for the dialog
            val inflater = LayoutInflater.from(holder.itemView.context)
            val dialogView = inflater.inflate(R.layout.dialog_apply_missing_marks, null)

// Find the views by their IDs
            val etSchool = dialogView.findViewById<EditText>(R.id.etSchool)
            val etDepartment = dialogView.findViewById<EditText>(R.id.etDepartment)
            val etStudentName = dialogView.findViewById<EditText>(R.id.etStudentName)
            val etRegNo = dialogView.findViewById<EditText>(R.id.etRegNo)
            val etUnitCode = dialogView.findViewById<EditText>(R.id.etUnitCode)
            val etUnitDescription=dialogView.findViewById<EditText>(R.id.etUnitDescription)
            val etSemester = dialogView.findViewById<EditText>(R.id.etSemester)
            val etExamType = dialogView.findViewById<EditText>(R.id.etExamType)
            val etLecturerName = dialogView.findViewById<EditText>(R.id.etLecturerName)
            etUnitCode.setText(currentMark.course.courseName.toString())
            etUnitDescription.setText(currentMark.course.courseId
            )
            etLecturerName.setText(currentMark.course.lecturer)
// Set up the AlertDialog
            val alertDialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Missing Marks Form")
                .setView(dialogView)
                .setPositiveButton("Submit") { dialog, which ->
                    // Retrieve data from each EditText
                    val school = etSchool.text.toString()
                    val department = etDepartment.text.toString()
                    val studentName = etStudentName.text.toString()
                    val regNo = etRegNo.text.toString()
                    val unitCode = etUnitCode.text.toString()
                    val unitDescription=etUnitDescription.text.toString()
                    val semester = etSemester.text.toString()
                    val examType = etExamType.text.toString()
                    val lecturerName = etLecturerName.text.toString()
                    if (userEmail != null) {
                        fetchStudentIdAndApplyMissingMarks(
                            holder.itemView.context,
                            userEmail,
                            currentMark,
                            school,
                            department,
                            unitDescription,
                            studentName,
                            regNo,
                            unitCode,
                            semester,
                            examType,
                            lecturerName
                        )
                    } else {
                        Toast.makeText(holder.itemView.context, "User not logged in", Toast.LENGTH_SHORT).show()
                    }
                    // Handle the data here (e.g., send to database or API)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.cancel()
                }
                .create()

// Show the dialog
            alertDialog.show()


        }
    }

    private fun fetchStudentIdAndApplyMissingMarks(
        context: Context,
        userEmail: String,
        currentMark: Mark,
        school: String,
        department: String,
        unitDescription:String,
        studentName: String,
        regNo: String,
        unitCode: String,
        semester: String,
        examType: String,
        lecturerName: String
    ) {
        FirebaseDatabase.getInstance().getReference("students")
            .orderByChild("email").equalTo(userEmail)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (dsnapshot in snapshot.children) {
                            val studentId = sanitizeKey(dsnapshot.child("studentId").value.toString())
                            applyMissingMarks(
                                context,
                                courseId = currentMark.course.courseName ?: "Unknown Course",
                                semester = semester,
                                studentId = studentId,
                                subject = currentMark.course.courseName ?: "Unknown Subject",
                                dueDate = "2024-11-10",
                                school = school,
                                department = department,
                                unitDescription=unitDescription,
                                studentName = studentName,
                                regNo = regNo,
                                unitCode = unitCode,
                                examType = examType,
                                lecturerName = lecturerName
                            )
                            return
                        }
                    } else {
                        Toast.makeText(context, "Student ID not found for $userEmail", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun sanitizeKey(key: String): String {
        return key.replace("/", "_")
    }

    private fun applyMissingMarks(
        context: Context,
        courseId: String,
        studentId: String,
        subject: String,
        dueDate: String,
        semester: String,
        school: String,
        department: String,
        unitDescription: String,
        studentName: String,
        regNo: String,
        unitCode: String,
        examType: String,
        lecturerName: String
    ) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("missingMarks")
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val missingMark = MissingMarks(
            studentId = studentId,
            semester = semester,
            subject = subject,
            dueDate = dueDate,
            markStatus = "missing",
            unitDescription=unitDescription,
            school = school,
            department = department,
            studentName = studentName,
            examType = examType,
            lecturerName = lecturerName
        )

        database.child(courseId).child(studentId).setValue(missingMark)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Missing mark successfully applied for $studentId in $subject", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to apply missing mark: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun getItemCount() = marks.size

    fun updateMarks(marksList: MutableList<Mark>) {
        marks = marksList
    }
}
