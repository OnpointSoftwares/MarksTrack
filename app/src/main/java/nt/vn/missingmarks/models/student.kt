package nt.vn.missingmarks.models

data class student(
    val studentId: String="",
    val name: String="",
    val age: String ="",
    val gender: String="",
    val email: String="",
    val phoneNumber: String="",
    val address: String="",
    // Maps course names to marks
)
