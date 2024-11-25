package nt.vn.missingmarks.lecturer

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.MissingMarks

class MissingMarksAdapter(private var missingMarks: List<MissingMarks>) : RecyclerView.Adapter<MissingMarksAdapter.MarksViewHolder>() {

    class MarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentId: TextView = itemView.findViewById(R.id.tv_student_id)
        val subject: TextView = itemView.findViewById(R.id.tv_subject)
        val dueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        val markStatus: TextView = itemView.findViewById(R.id.tv_mark_status)
        val btnSubmit: Button = itemView.findViewById(R.id.btn_submit_mark)
        val semester:TextView=itemView.findViewById(R.id.TvSemester)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_missing, parent, false)
        return MarksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        val currentMissingMark = missingMarks[position]

        // Set the data for the views
        holder.studentId.text = currentMissingMark.studentId
        holder.subject.text = "Exam mark:${currentMissingMark.examMark} Cat Mark:${currentMissingMark.catMark} Unit:${currentMissingMark.subject}"
        holder.dueDate.text = currentMissingMark.dueDate
        holder.markStatus.text = currentMissingMark.markStatus
        holder.semester.text=currentMissingMark.semester
        // Change color based on mark status
        if (currentMissingMark.markStatus == "missing") {
            holder.markStatus.setTextColor(Color.RED)
            holder.btnSubmit.visibility = View.VISIBLE // Show button if missing
        } else {
            holder.markStatus.setTextColor(Color.GREEN)
            holder.btnSubmit.visibility = View.GONE // Hide button if not missing
        }

        holder.btnSubmit.setOnClickListener {
            // Show dialog to enter marks
            showMarkInputDialog(holder.itemView.context, currentMissingMark)
        }
    }

    private fun showMarkInputDialog(context: Context, missingMark: MissingMarks) {
        // Create an AlertDialog to input marks
        val alertDialog = AlertDialog.Builder(context)
        val dialogView=LayoutInflater.from(context).inflate(R.layout.dialog_fill_marks,null)
        alertDialog.setTitle("Missing Marks Form")
        alertDialog.setMessage("Please enter the details below")

// Set up the EditText for description
        alertDialog.setView(dialogView)

// Set up the buttons
        alertDialog.setPositiveButton("Submit") { dialog, which ->
            val description = dialogView.findViewById<EditText>(R.id.edtDescription).text.toString()
            val mark=dialogView.findViewById<EditText>(R.id.edtMarks).text.toString()
            val examtype=dialogView.findViewById<EditText>(R.id.spinner_mark_type).text.toString()
            missingMark.description=description
            if(examtype=="CAT"||examtype=="cat"){
                missingMark.catMark=mark
            }
            else{
                missingMark.examMark=mark
            }
            submitMissingMark(context,missingMark)
        }

        alertDialog.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

// Show the dialog
        alertDialog.show()

    }

    private fun submitMissingMark(context: Context, missingMark: MissingMarks) {
        // Initialize Firebase Database reference
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("missingMarks")

        // Update the mark status to "submitted" in Firebase
        missingMark.markStatus = "submitted"
        // You may want to update the marks field here if your MissingMarks data class has a field for marks

        database.child(missingMark.subject).child(missingMark.studentId)
            .setValue(missingMark) // Ensure you add marks to the MissingMarks object if required
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Missing mark submitted for ${missingMark.studentId}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to submit mark: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun getItemCount() = missingMarks.size

    fun updateMarks(newMissingMarks: List<MissingMarks>) {
        missingMarks = newMissingMarks
        notifyDataSetChanged() // Notify the adapter that data has changed
    }
}
