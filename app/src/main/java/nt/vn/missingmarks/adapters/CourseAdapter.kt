package nt.vn.missingmarks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course
import nt.vn.missingmarks.models.Mark
import nt.vn.missingmarks.models.student

class CourseAdapter(
    private val courses: List<Course>
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.courseName)

        fun bind(course: Course) {
            courseNameTextView.text = course.courseName
            itemView.setOnClickListener {
                val alertDialog = AlertDialog.Builder(itemView.context)
                alertDialog.setTitle("Do you want to register for this unit?")
                alertDialog.setPositiveButton("Yes") { dialog, _ ->
                    getStudentDetails(course,itemView.context)
                    val mark= Mark(course =course, catmark ="", exammark = "")
                    val ref=FirebaseDatabase.getInstance().reference
                    val key=ref.push().key.toString()
                    mark.key=key
                    ref.child("students").addValueEventListener(object:ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (dsnapshot in snapshot.children)
                            {
                                if (dsnapshot.child("email").value.toString() == FirebaseAuth.getInstance().currentUser!!.email) {
                                    val sid=sanitizeKey(dsnapshot.child("studentId").value.toString())
                                    ref.child("marks")
                                        .child(sid)
                                        .child(key).setValue(mark).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(
                                                    itemView.context,
                                                    "Registered successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dialog.dismiss()
                                            } else {
                                                Toast.makeText(
                                                    itemView.context,
                                                    "Registration Failed. Try again!!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dialog.dismiss()
                                            }
                                }
                            }
                            }

                    }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                      // Close the dialog
                }
                alertDialog.setNegativeButton("No") { dialog, _ ->
                    // Action when "No" is clicked
                    dialog.dismiss()  // Close the dialog
                }

// Display the dialog
                alertDialog.show()
            }
        }
    }

    private fun getStudentDetails(course:Course,context: Context) {
        val user=FirebaseAuth.getInstance().currentUser!!.email
        Toast.makeText(context,user,Toast.LENGTH_LONG).show()
        FirebaseDatabase.getInstance().reference.child("students").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dsnapshot in snapshot.children)
                {
                    val studentId=dsnapshot.child("studentId").value.toString()
                    val name=dsnapshot.child("name").value.toString()
                    val age=dsnapshot.child("age").value.toString()
                    val gender=dsnapshot.child("gender").value.toString()
                    val email=dsnapshot.child("email").value.toString()
                    val studentD=student(studentId =studentId, name =name, age =age, gender =gender)
                    val ref=FirebaseDatabase.getInstance().reference
                    val key=ref.push().key.toString()
                    val sid=sanitizeKey(studentId)
                    if(email==user) {
                        ref.child("courses").child(course.courseId.toString()).child("students")
                            .child(sid).setValue(studentD).addOnCompleteListener {
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }fun sanitizeKey(key: String): String {
        return key.replace("/", "_")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size
}
