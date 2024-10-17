package nt.vn.missingmarks.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nt.vn.missingmarks.R
import nt.vn.missingmarks.models.Mark

class MarkAdapter(
    private val marks: List<Mark>,
    private val onMarkClick: (Mark) -> Unit
) : RecyclerView.Adapter<MarkAdapter.MarkViewHolder>() {

    inner class MarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val studentNameTextView: TextView = itemView.findViewById(R.id.studentName)
        private val courseNameTextView: TextView = itemView.findViewById(R.id.courseName)
        private val markTextView: TextView = itemView.findViewById(R.id.mark)

        fun bind(mark: Mark) {
            studentNameTextView.text = mark.course.courseName
            courseNameTextView.text = mark.course.courseName
            markTextView.text = mark.mark?.toString() ?: "Missing"
            itemView.setOnClickListener { onMarkClick(mark) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark, parent, false)
        return MarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkViewHolder, position: Int) {
        holder.bind(marks[position])
    }

    override fun getItemCount(): Int = marks.size
}
