# AddQuestionDialog.kt — AlertDialog for adding a multiple-choice question to a quiz

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/AddQuestionDialog.kt`

---

## 🎯 What This File Does
`AddQuestionDialog` is a `DialogFragment` that shows a standard `AlertDialog` containing input fields for a quiz question: the question text, four option texts (A–D), and a `RadioGroup` to select the correct answer. When the teacher taps "Add", it validates that all fields are non-empty, constructs a `QuizQuestion` object, and invokes the `onAdd` callback. The `CreateQuizFragment` uses this dialog to build the question list before saving the quiz to Firestore. Without it, teachers would have no UI to add questions.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.app.Dialog` | Android SDK | Base Dialog class | Return type of `onCreateDialog` |
| `android.os.Bundle` | Android SDK | State map | `onCreateDialog` and `onDestroyView` |
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate `dialog_add_question.xml` |
| `androidx.appcompat.app.AlertDialog` | AndroidX AppCompat | Material-styled alert dialog | Dialog builder |
| `androidx.fragment.app.DialogFragment` | AndroidX | Fragment-based dialog container | Lifecycle management |
| `com.syed.classconnect.data.model.QuizQuestion` | Project | Question data class | Created on "Add" tap |
| `com.syed.classconnect.databinding.DialogAddQuestionBinding` | ViewBinding | `dialog_add_question.xml` | `etQuestion`, `etOptionA–D`, `rgCorrect`, `rbA–D` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `DialogFragment` vs `Dialog`
`DialogFragment` manages the dialog's lifecycle — it is recreated on rotation, dismissed properly on back press, and handles Fragment back stack. Raw `Dialog` objects are NOT recreated on rotation and can leak.

### `onCreateDialog(savedInstanceState: Bundle?): Dialog`
`DialogFragment` provides this override instead of `onCreateView`. Returns the `Dialog` object to show.

### `AlertDialog.Builder`
Builder pattern for creating dialogs: `setTitle()`, `setView(binding.root)`, `setPositiveButton()`, `setNegativeButton()`, then `create()`.

### `binding.rgCorrect.checkedRadioButtonId`
Returns the resource ID of the currently checked `RadioButton` inside the `RadioGroup`. Compared with `binding.rbA.id`, `binding.rbB.id`, etc. to determine which option index (0–3) is correct.

### `QuizQuestion(question, options, correctIndex, marks = 1)`
`marks = 1` is the default value for each question. If a quiz has 10 questions × 1 mark each, total = 10 marks. This matches `totalMarks` computed in `CreateQuizFragment`.

### `if (question.isNotEmpty() && options.all { it.isNotEmpty() })`
Validation: only invoke `onAdd` if all 5 text fields are non-empty. Empty questions are silently ignored — no error message.

---

## 🏗️ Class Structure
`class AddQuestionDialog(private val onAdd: (QuizQuestion) -> Unit) : DialogFragment()` — takes the callback in the constructor.

---

## ⚙️ Functions

### `onCreateDialog(savedInstanceState: Bundle?): Dialog`
**Purpose:** Build and return the AlertDialog.
**Step by step:**
1. Inflates `dialog_add_question.xml` into `binding`.
2. Builds `AlertDialog` with title "Add Question", the binding root as the view.
3. Positive button "Add" lambda:
   - Reads question text and 4 option texts.
   - Reads `rgCorrect.checkedRadioButtonId` → maps to index 0–3.
   - Validates all fields non-empty.
   - Calls `onAdd(QuizQuestion(question, options, correctIndex, marks = 1))`.
4. Negative button "Cancel" → null (just dismisses).
5. Returns `create()`.

### `onDestroyView()`
Sets `_binding = null` to prevent memory leak.

---

## ⚠️ Important Notes & Gotchas
- No error message is shown when validation fails — the dialog just doesn't call `onAdd`. Consider adding a toast or error text.
- `marks = 1` is hardcoded. Future enhancement: let the teacher set marks per question.
- The constructor parameter `onAdd` works because `DialogFragment` instances are re-created by the system on rotation, BUT only when created via `newInstance()` with a `Bundle`. Since this dialog passes `onAdd` as a constructor parameter (not a Bundle), it will NOT survive process death/recreation. This is acceptable for the current usage pattern (called inline from `CreateQuizFragment`).

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.quiz

// (imports as listed above)

class AddQuestionDialog(private val onAdd: (QuizQuestion) -> Unit) : DialogFragment() {

    private var _binding: DialogAddQuestionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddQuestionBinding.inflate(LayoutInflater.from(context))
        // context is available in DialogFragment at onCreateDialog time.

        return AlertDialog.Builder(requireContext())
            .setTitle("Add Question")
            .setView(binding.root)
            // Embed our custom layout inside the AlertDialog.
            .setPositiveButton("Add") { _, _ ->
                val question = binding.etQuestion.text.toString().trim()
                val options = listOf(
                    binding.etOptionA.text.toString().trim(),
                    binding.etOptionB.text.toString().trim(),
                    binding.etOptionC.text.toString().trim(),
                    binding.etOptionD.text.toString().trim()
                )
                val correct = when (binding.rgCorrect.checkedRadioButtonId) {
                    binding.rbA.id -> 0   // Option A selected
                    binding.rbB.id -> 1   // Option B selected
                    binding.rbC.id -> 2   // Option C selected
                    else -> 3              // Option D selected (default)
                }
                if (question.isNotEmpty() && options.all { it.isNotEmpty() }) {
                    // Only create the question if all fields are filled.
                    onAdd(QuizQuestion(question = question, options = options,
                        correctIndex = correct, marks = 1))
                }
                // If validation fails: dialog closes silently (no error shown).
            }
            .setNegativeButton("Cancel", null)
            // null listener = just dismiss the dialog.
            .create()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
```

