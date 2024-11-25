package nt.vn.missingmarks.models

data class Mark(
    var key:String="",
    val course: Course,       // Associated course
    val exammark: String,
    val catmark: String,// Nullable mark (can be missing)
) {
    // Property to check if the mark is missing
    val isexamMissing: Boolean
        get() = exammark == ""
    val iscatMissing: Boolean
        get() = catmark == ""
}
