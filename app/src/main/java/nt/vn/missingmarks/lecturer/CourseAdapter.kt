package nt.vn.missingmarks.lecturer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Course

class CourseAdapter(
    private var courses: List<Course>,
    private val onViewStudentsClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: TextView = itemView.findViewById(R.id.tv_course_name)
        val courseCode: TextView = itemView.findViewById(R.id.tv_course_code)
        val semester:TextView=itemView.findViewById(R.id.tv_semester)
        val viewStudentsButton: Button = itemView.findViewById(R.id.btn_view_students)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course_lecturer, parent, false)
        return CourseViewHolder(view)
    }
    fun updateCourses(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = courses[position]
        holder.courseName.text = currentCourse.courseName
        holder.courseCode.text = currentCourse.courseId
        holder.semester.text=currentCourse.semester
        holder.viewStudentsButton.setOnClickListener {
            onViewStudentsClick(currentCourse)
        }
    }

    override fun getItemCount() = courses.size
}
