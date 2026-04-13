# AssignmentRepository — Data access for assignments and submissions

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/AssignmentRepository.kt`

---

## 🎯 What This File Does
AssignmentRepository handles all Firestore operations for assignments and submissions: CRUD for assignments, submission creation/retrieval, grading, and batch notification sending when a new assignment is created.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `getAssignments(classId)` | `Flow<List<Assignment>>` | Real-time assignment list, ordered by dueDate |
| `createAssignment(classId, assignment)` | `Result<String>` | Creates assignment + sends notifications to all students |
| `getAssignmentById(classId, assignmentId)` | `Assignment?` | One-shot fetch |
| `submitAssignment(classId, assignmentId, submission)` | `Result<Unit>` | Student submits work |
| `getSubmission(classId, assignmentId, studentId)` | `Submission?` | Get student's submission |
| `getAllSubmissions(classId, assignmentId)` | `Flow<List<Submission>>` | Real-time all submissions (teacher) |
| `gradeSubmission(...)` | `Result<Unit>` | Updates grade, feedback, status |
| `deleteAssignment(classId, assignmentId)` | `Result<Unit>` | Deletes assignment + all submissions (batch) |

---

## ⚠️ Important Notes
- `createAssignment()` sends batch notifications to ALL students in the class
- `deleteAssignment()` first deletes all submissions (subcollection), then the assignment document
- Notification creation failures are caught and ignored — assignment creation should not fail due to notification issues
- Submissions use `studentId` as the document ID, ensuring one submission per student

