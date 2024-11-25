package nt.vn.missingmarks.adapters

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.MissingMarks

class MissingMarksAdapter(private var missingMarks: List<MissingMarks>) : RecyclerView.Adapter<MissingMarksAdapter.MarksViewHolder>() {

    class MarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentId: TextView = itemView.findViewById(R.id.tv_student_id)
        val subject: TextView = itemView.findViewById(R.id.tv_subject)
        val dueDate: TextView = itemView.findViewById(R.id.tv_due_date)
        val markStatus: TextView = itemView.findViewById(R.id.tv_mark_status)
        val btnSubmit: Button = itemView.findViewById(R.id.btn_submit_mark)
        val marksTv:TextView=itemView.findViewById(R.id.marks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_missing, parent, false)
        return MarksViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        val currentMissingMark = missingMarks[position]

        // Set the data for the views
        holder.studentId.text = currentMissingMark.studentId
        holder.subject.text = currentMissingMark.subject
        holder.dueDate.text = currentMissingMark.dueDate
        holder.markStatus.text = currentMissingMark.markStatus
        holder.marksTv.text="Marks:Cat:${currentMissingMark.catMark} exam mark:${currentMissingMark.examMark}"
        // Change color based on mark status
        if (currentMissingMark.markStatus == "missing") {
            holder.markStatus.setTextColor(Color.RED)
            holder.btnSubmit.visibility = View.VISIBLE // Show button if missing
        } else {
            holder.markStatus.setTextColor(Color.GREEN)
            holder.btnSubmit.visibility = View.VISIBLE // Hide button if not missing
        }

        holder.btnSubmit.setOnClickListener {
            // Show dialog to enter marks
            val alertDialog=ProgressDialog(holder.itemView.context)
            alertDialog.setTitle("Confirming...")
            alertDialog.show()
            confirmMissingMarkFun(holder.itemView.context, currentMissingMark)
            alertDialog.dismiss()
        }
    }

    private fun confirmMissingMarkFun(context: Context, missingMark: MissingMarks) {
        // Initialize Firebase Database reference
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("marks")
        FirebaseDatabase.getInstance().getReference("marks")
        // Update the mark status to "submitted" in Firebase
        missingMark.studentId
        // You may want to update the marks field here if your MissingMarks data class has a field for marks

        var markMap= emptyMap<String,String>()
        if(missingMark.catMark==""||missingMark.catMark=="null")
        {
            markMap=mapOf("examMark" to missingMark.examMark,"markStatus" to "allowed","description" to missingMark.description)
        }
        else if(missingMark.examMark==""||missingMark.examMark=="null"){
            markMap=mapOf("catMark" to missingMark.catMark,"markStatus" to "allowed","description" to missingMark.description)
        }
        else{
            markMap=mapOf("examMark" to missingMark.examMark,"catMark" to missingMark.catMark,"markStatus" to "allowed","description" to missingMark.description)
        }
        database.child(missingMark.studentId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dsn in snapshot.children)
                {
                    if(dsn.child("course").child("courseId").value==missingMark.subject)
                    {
                       val key=dsn.child("key").value.toString()
                        database.child(missingMark.studentId).child(key).updateChildren(markMap) // Ensure you add marks to the MissingMarks object if required
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Missing mark submitted for ${missingMark.studentId} ", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to submit mark: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun getItemCount() = missingMarks.size

    fun updateMarks(newMissingMarks: List<MissingMarks>) {
        missingMarks = newMissingMarks
        notifyDataSetChanged() // Notify the adapter that data has changed
    }
}
