package nt.vn.missingmarks.models

data class Mark(
    val course: Course,       // Associated course
    val mark: Double?,        // Nullable mark (can be missing)
) {
    // Property to check if the mark is missing
    val isMissing: Boolean
        get() = mark == null
}
