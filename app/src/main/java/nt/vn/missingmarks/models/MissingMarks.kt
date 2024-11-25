package nt.vn.missingmarks.models

import android.os.Parcelable

data class MissingMarks(
    var school: String = "",
    var department: String = "",
    var studentName: String = "",
    var studentId: String = "",
    var dueDate:String="",
    var subject: String = "",
    var description: String = "",
    var unitDescription: String = "",
    var semester: String = "", // Example: "December 2019"
    var examType: String = "", // Example: "normal" or "special"
    var lecturerName: String = "",
    var catMark: String = "",
    var examMark: String = "",
    var markStatus: String = "missing", // Can be "missing" or "submitted"

)
