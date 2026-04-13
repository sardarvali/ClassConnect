# AssignmentsViewModel — Assignment CRUD and submission state management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/AssignmentsViewModel.kt`

---

## 🎯 What This File Does
AssignmentsViewModel manages all assignment-related state: loading assignments, loading/submitting submissions, grading, and user role detection. Used across AssignmentsFragment, AssignmentDetailFragment, SubmissionListFragment, and GradeSubmissionFragment.

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `assignments` | `LiveData<NetworkResult<List<Assignment>>>` | Real-time assignment list |
| `submission` | `LiveData<Submission?>` | Current student's submission |
| `submitResult` | `LiveData<NetworkResult<Unit>>` | Submit operation result |
| `gradeResult` | `LiveData<NetworkResult<Unit>>` | Grade operation result |
| `allSubmissions` | `LiveData<NetworkResult<List<Submission>>>` | All submissions (teacher view) |
| `userRole` | `LiveData<String>` | Current user's role |
| `assignmentDetail` | `LiveData<Assignment?>` | Single assignment details |

---

## ⚙️ Key Functions
- `loadAssignments(classId)`: Collects real-time Flow from AssignmentRepository
- `createAssignment(classId, assignment)`: Creates assignment with notifications
- `submitAssignment(classId, assignmentId, submission)`: Student submits work
- `gradeSubmission(classId, assignmentId, studentId, grade, feedback)`: Teacher grades
- `loadAllSubmissions(classId, assignmentId)`: Real-time submission list for teacher

