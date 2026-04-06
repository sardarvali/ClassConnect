# StudentsViewModel — Loads student list for a class

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/StudentsViewModel.kt`

---

## 🎯 What This File Does
StudentsViewModel loads all students enrolled in a class by reading the `studentIds` array from the class document and then batch-querying user documents. Handles Firestore's `whereIn` limit of 30 items by chunking.

---

## ⚙️ Key Functions

### `loadStudents(classId: String)`
1. Sets Loading state
2. Reads class document from Firestore
3. Extracts `studentIds` list
4. Chunks by 30 (Firestore `whereIn` limit)
5. Queries `/users` for each chunk
6. Sorts by name, emits Success

---

## 📝 Full Annotated Source Code

```kotlin
@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
    // Injected directly rather than through repository — simple enough query.
) : ViewModel() {

    private val _students = MutableLiveData<NetworkResult<List<User>>>()
    val students: LiveData<NetworkResult<List<User>>> = _students

    fun loadStudents(classId: String) {
        _students.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val classDoc = firestore.collection(Constants.COLLECTION_CLASSES)
                    .document(classId).get().await()
                val studentIds = classDoc.get("studentIds") as? List<String> ?: emptyList()
                // Firestore 'in' queries support max 30 items per batch
                val students = mutableListOf<User>()
                studentIds.chunked(30).forEach { chunk ->
                    val docs = firestore.collection(Constants.COLLECTION_USERS)
                        .whereIn("uid", chunk).get().await()
                    students.addAll(docs.toObjects(User::class.java))
                }
                _students.value = NetworkResult.Success(students.sortedBy { it.name })
            } catch (e: Exception) {
                _students.value = NetworkResult.Error(e.message ?: "Failed to load students")
            }
        }
    }
}
```

