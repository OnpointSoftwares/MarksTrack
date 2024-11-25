package nt.vn.missingmarks

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import nt.vn.missingmarks.models.MissingMarks
import java.text.SimpleDateFormat
import java.util.*

fun applyMissingMarks(
    courseId: String,
    studentId: String,
    subject: String,
    dueDate: String
) {
    // Initialize Firebase Database reference
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("missingMarks")

    // Generate the current date as the application date
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Create a MissingMarks object
    val missingMark = MissingMarks(
        studentId = studentId,
        subject = subject,
        dueDate = dueDate,
        markStatus = "missing"
    )

    // Save to Firebase under missingMarks/courseId/studentId
    database.child(courseId).child(studentId).setValue(missingMark)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Missing mark successfully applied for $studentId in $subject")
            } else {
                println("Failed to apply missing mark: ${task.exception?.message}")
            }
        }
}
