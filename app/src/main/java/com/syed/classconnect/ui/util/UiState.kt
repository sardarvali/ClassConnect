package com.syed.classconnect.ui.util

/**
 * UiState - Sealed class for managing async UI states consistently across the app
 *
 * Follows the MVVM pattern with reactive state management. All ViewModels should expose
 * their data through LiveData<UiState<T>> instead of separate Loading/Error/Success states.
 *
 * Example usage in ViewModel:
 * ```
 * val assignments: LiveData<UiState<List<Assignment>>> = repository
 *     .getAssignments()
 *     .map { UiState.Success(it) }
 *     .onStart { emit(UiState.Loading()) }
 *     .catch { emit(UiState.Error(it.message ?: "Unknown error", ::retry)) }
 * ```
 *
 * Example usage in Fragment:
 * ```
 * viewModel.assignments.observe(viewLifecycleOwner) { state ->
 *     when (state) {
 *         is UiState.Loading -> showShimmer()
 *         is UiState.Success -> showAssignments(state.data)
 *         is UiState.Error -> showErrorState(state.message, state.action)
 *     }
 * }
 * ```
 */
sealed class UiState<out T> {
    // ────────────────────────────────────────────────────────────────────────────
    // LOADING STATE - Show shimmer/skeleton loaders
    // ────────────────────────────────────────────────────────────────────────────
    class Loading<T> : UiState<T>() {
        override fun toString(): String = "UiState.Loading"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // SUCCESS STATE - Display data with optional message
    // ────────────────────────────────────────────────────────────────────────────
    data class Success<T>(
        val data: T,
        val message: String? = null,
        val showAnimation: Boolean = false
    ) : UiState<T>() {
        override fun toString(): String = "UiState.Success(message=$message, animated=$showAnimation)"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // ERROR STATE - Show error UI with optional retry action
    // ────────────────────────────────────────────────────────────────────────────
    data class Error<T>(
        val message: String,
        val action: (() -> Unit)? = null,
        val errorCode: String? = null,
        val exception: Exception? = null,
        val showDetails: Boolean = false
    ) : UiState<T>() {
        override fun toString(): String = "UiState.Error(code=$errorCode, message=$message)"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // EMPTY STATE - No data available but not an error
    // ────────────────────────────────────────────────────────────────────────────
    data class Empty<T>(
        val message: String = "No data available",
        val action: (() -> Unit)? = null
    ) : UiState<T>() {
        override fun toString(): String = "UiState.Empty(message=$message)"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // OFFLINE STATE - Network unavailable
    // ────────────────────────────────────────────────────────────────────────────
    data class Offline<T>(
        val cachedData: T? = null,
        val message: String = "No internet connection"
    ) : UiState<T>() {
        override fun toString(): String = "UiState.Offline(hasCachedData=${cachedData != null})"
    }

    // ────────────────────────────────────────────────────────────────────────────
    // HELPER METHODS
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Safely get data if state is Success, otherwise null
     */
    fun getDataOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Check if state is Loading
     */
    fun isLoading(): Boolean = this is Loading

    /**
     * Check if state is Error
     */
    fun isError(): Boolean = this is Error

    /**
     * Check if state is Success
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Check if state is Empty
     */
    fun isEmpty(): Boolean = this is Empty

    /**
     * Transform success data to a new type
     */
    fun <R> map(transform: (T) -> R): UiState<R> = when (this) {
        is Loading -> Loading()
        is Success -> Success(transform(data), message, showAnimation)
        is Error -> Error(message, action, errorCode, exception, showDetails)
        is Empty -> Empty(message, action)
        is Offline -> Offline(cachedData?.let { transform(it) }, message)
    }

    /**
     * Transform success data to a new type (with mapping on cached data too)
     */
    inline fun <R> flatMap(crossinline transform: suspend (T) -> UiState<R>): suspend () -> UiState<R> {
        return suspend {
            when (this@UiState) {
                is Success -> transform(data)
                is Loading -> Loading()
                is Error -> Error(message, action, errorCode, exception, showDetails)
                is Empty -> Empty(message, action)
                is Offline -> Offline(null, message)
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════════
// EXTENSION FUNCTIONS
// ════════════════════════════════════════════════════════════════════════════════

/**
 * Execute block if state is Success
 */
inline fun <T> UiState<T>.onSuccess(block: (T) -> Unit) {
    if (this is UiState.Success) {
        block(data)
    }
}

/**
 * Execute block if state is Error
 */
inline fun <T> UiState<T>.onError(block: (String, (() -> Unit)?) -> Unit) {
    if (this is UiState.Error) {
        block(message, action)
    }
}

/**
 * Execute block if state is Loading
 */
inline fun <T> UiState<T>.onLoading(block: () -> Unit) {
    if (this is UiState.Loading) {
        block()
    }
}

/**
 * Execute block if state is Empty
 */
inline fun <T> UiState<T>.onEmpty(block: (String) -> Unit) {
    if (this is UiState.Empty) {
        block(message)
    }
}

/**
 * Execute block if state is Offline
 */
inline fun <T> UiState<T>.onOffline(block: (T?) -> Unit) {
    if (this is UiState.Offline) {
        block(cachedData)
    }
}

/**
 * Execute provided action for each state type
 */
inline fun <T> UiState<T>.fold(
    onLoading: () -> Unit,
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit,
    onEmpty: (String) -> Unit,
    onOffline: (T?) -> Unit
) {
    when (this) {
        is UiState.Loading -> onLoading()
        is UiState.Success -> onSuccess(data)
        is UiState.Error -> onError(message)
        is UiState.Empty -> onEmpty(message)
        is UiState.Offline -> onOffline(cachedData)
    }
}


