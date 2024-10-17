package nt.vn.missingmarks.models

data class Lecturer(
    val lecturerId: String = "",
    val name: String = "",
    val department: String = "",  // New field for department
    val email: String = "",  // New field for email
    val officeHours: String = "",  // New field for office hours
    val courses: List<Course> = emptyList() // List of courses this lecturer teaches
)

