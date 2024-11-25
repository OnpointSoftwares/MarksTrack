package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course

class StudentsCourseAdapter(
    private var courses: List<Course>,
) : RecyclerView.Adapter<StudentsCourseAdapter.StudentsCourseViewHolder>() {

    inner class StudentsCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val courseNameTextView: TextView = itemView.findViewById(R.id.courseName)

        fun bind(course: Course) {
            courseNameTextView.text = course.courseName
            itemView.setOnClickListener { }
        }
    }
    fun updateCourses(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsCourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course_student, parent, false)
        return StudentsCourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentsCourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size
}
