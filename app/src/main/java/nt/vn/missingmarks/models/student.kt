package nt.vn.missingmarks.models

data class student(
    val studentId: String,
    val name: String,
    val age: Int,
    val gender: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    // Maps course names to marks
)
