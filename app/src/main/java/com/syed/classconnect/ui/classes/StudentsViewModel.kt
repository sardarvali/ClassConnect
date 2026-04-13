package com.syed.classconnect.ui.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.data.model.User
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _students = MutableLiveData<NetworkResult<List<User>>>()
    val students: LiveData<NetworkResult<List<User>>> = _students
<<<<<<< HEAD

    fun loadStudents(classId: String) {
=======
    private var loadedClassId: String? = null

    fun loadStudents(classId: String) {
        val current = _students.value
        if (loadedClassId == classId && current is NetworkResult.Success) return

>>>>>>> final
        _students.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val classDoc = firestore.collection(Constants.COLLECTION_CLASSES)
                    .document(classId).get().await()
<<<<<<< HEAD
=======

>>>>>>> final
                @Suppress("UNCHECKED_CAST")
                val studentIds = classDoc.get("studentIds") as? List<String> ?: emptyList()

                if (studentIds.isEmpty()) {
                    _students.value = NetworkResult.Success(emptyList())
                    return@launch
                }

                // Firestore 'in' queries support max 30 items per batch
                val students = mutableListOf<User>()
                studentIds.chunked(30).forEach { chunk ->
                    val docs = firestore.collection(Constants.COLLECTION_USERS)
                        .whereIn("uid", chunk)
                        .get().await()
                    students.addAll(docs.toObjects(User::class.java))
                }

<<<<<<< HEAD
=======
                loadedClassId = classId
>>>>>>> final
                _students.value = NetworkResult.Success(students.sortedBy { it.name })
            } catch (e: Exception) {
                _students.value = NetworkResult.Error(e.message ?: "Failed to load students")
            }
        }
    }
}

