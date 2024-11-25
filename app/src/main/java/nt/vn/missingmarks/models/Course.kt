package nt.vn.missingmarks.models

import android.os.Parcel
import android.os.Parcelable

data class Course(
    val lecturer: String? = "",
    val courseId: String? = "",
    val courseName: String? = "",
    val students: List<student> = emptyList(),
    val semester: String="Not Specified"
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lecturer)
        parcel.writeString(courseId)
        parcel.writeString(courseName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Course> {
        override fun createFromParcel(parcel: Parcel): Course {
            return Course(parcel)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }
    }
}
