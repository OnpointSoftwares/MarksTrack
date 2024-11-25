package nt.vn.missingmarks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import nt.vn.missingmarks.adapters.StudentAdapter
import nt.vn.missingmarks.models.student

class StudentFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var studentList: MutableList<student>
    private lateinit var database: DatabaseReference
    private lateinit var addStudentButton: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student, container, false)
        addStudentButton = view.findViewById(R.id.newStudent)
        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.studentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        studentList = mutableListOf()
        addStudentButton.setOnClickListener {
            showAddStudentDialog()
        }
        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().getReference("students")

        // Fetch and set the student data from Firebase
        fetchStudentsFromFirebase()

        return view
    }

    private fun showAddStudentDialog() {
        // Create a dialog to add student
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_student, null)
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add New Student")

        val alertDialog = builder.show()

        // Get references to the input fields in the dialog
        val editTextStudentId = dialogView.findViewById<EditText>(R.id.editTextStudentId)
        val editTextStudentName = dialogView.findViewById<EditText>(R.id.editTextStudentName)
        val editTextStudentAge = dialogView.findViewById<EditText>(R.id.editTextStudentAge)
        val editTextStudentGender = dialogView.findViewById<EditText>(R.id.editTextStudentGender)
        val editTextStudentEmail = dialogView.findViewById<EditText>(R.id.editTextStudentEmail)
        val editTextStudentPhone = dialogView.findViewById<EditText>(R.id.editTextStudentPhone)
        val editTextStudentAddress = dialogView.findViewById<EditText>(R.id.editTextStudentAddress)
        val submitButton = dialogView.findViewById<Button>(R.id.buttonSubmitStudent)

        // Handle form submission
        submitButton.setOnClickListener {
            val studentId = editTextStudentId.text.toString()
            val name = editTextStudentName.text.toString()
            val age = editTextStudentAge.text.toString().toIntOrNull()
            val gender = editTextStudentGender.text.toString()
            val email = editTextStudentEmail.text.toString()
            val phone = editTextStudentPhone.text.toString()
            val address = editTextStudentAddress.text.toString()

            if (name.isNotEmpty() && age != null && studentId.isNotEmpty()) {
                // Create new student object
                val newStudent = student(
                    studentId = studentId,
                    name = name,
                    age = age.toString(),
                    gender = gender,
                    email = email,
                    phoneNumber = phone,
                    address = address
                )

                // Add new student to Firebase
                val key = database.push().key.toString()
                database.child(key).setValue(newStudent).addOnCompleteListener {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(newStudent.email,"student123").addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseDatabase.getInstance().reference.child("users")
                                .child(it.result.user!!.uid).setValue("student")
                                .addOnCompleteListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Student added",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                    }

                }

                // Dismiss the dialog
                alertDialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun fetchStudentsFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                studentList.clear()

                for (studentSnapshot in dataSnapshot.children) {
                    val studentId =
                        studentSnapshot.child("studentId").getValue(String::class.java) ?: ""
                    val name = studentSnapshot.child("name").getValue(String::class.java) ?: ""
                    val age = studentSnapshot.child("age").value.toString()
                    val gender = studentSnapshot.child("gender").getValue(String::class.java) ?: ""
                    val email = studentSnapshot.child("email").getValue(String::class.java) ?: ""
                    val phone =
                        studentSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                    val address =
                        studentSnapshot.child("address").getValue(String::class.java) ?: ""

                    val student = student(
                        studentId = studentId,
                        name = name,
                        age = age.toString(),
                        gender = gender,
                        email = email,
                        phoneNumber = phone,
                        address = address
                    )

                    studentList.add(student)
                }

                studentAdapter = StudentAdapter(studentList)
                recyclerView.adapter = studentAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("StudentFragment", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }
}

