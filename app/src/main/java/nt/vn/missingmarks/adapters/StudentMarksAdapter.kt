package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Mark
import nt.vn.missingmarks.models.student

class StudentMarksAdapter(
    private val studentsmark: List<Mark>
) : RecyclerView.Adapter<StudentMarksAdapter.StudentMarksViewHolder>() {

    inner class StudentMarksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseName: TextView = itemView.findViewById(R.id.courseName)
        private val courseMark: TextView = itemView.findViewById(R.id.courseMark)
        fun bind(mark: Mark) {
            courseName.text = mark.course.courseName
                courseMark.text = mark.exammark.toString()
            if(mark.isexamMissing){

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentMarksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_marks, parent, false)
        return StudentMarksViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentMarksViewHolder, position: Int) {
        holder.bind(studentsmark[position])
    }

    override fun getItemCount(): Int = studentsmark.size
}
