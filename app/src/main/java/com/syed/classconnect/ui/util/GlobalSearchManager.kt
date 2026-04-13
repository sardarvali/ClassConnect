package com.syed.classconnect.ui.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Global search functionality across classes, assignments, people, notifications
 */
class GlobalSearchManager {

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<SearchResults>(SearchResults())
    val searchResults: LiveData<SearchResults> = _searchResults

    private val _searchHistory = MutableLiveData<List<String>>(emptyList())
    val searchHistory: LiveData<List<String>> = _searchHistory

    private val _recentSearches = mutableListOf<String>()

    /**
     * Performs global search
     */
    fun search(query: String) {
        _searchQuery.value = query
        addToSearchHistory(query)

        // Simulate search across different categories
        val results = SearchResults(
            classes = emptyList(),
            assignments = emptyList(),
            people = emptyList(),
            notifications = emptyList()
        )
        _searchResults.postValue(results)
    }

    /**
     * Adds query to search history
     */
    private fun addToSearchHistory(query: String) {
        if (query.isNotBlank() && !_recentSearches.contains(query)) {
            _recentSearches.add(0, query)
            if (_recentSearches.size > 10) {
                _recentSearches.removeAt(_recentSearches.size - 1)
            }
            _searchHistory.postValue(_recentSearches.toList())
        }
    }

    /**
     * Clears search history
     */
    fun clearSearchHistory() {
        _recentSearches.clear()
        _searchHistory.postValue(emptyList())
    }

    /**
     * Gets search suggestions based on query
     */
    fun getSearchSuggestions(query: String): List<String> {
        return _recentSearches.filter { it.contains(query, ignoreCase = true) }
    }

    /**
     * Gets trending searches
     */
    fun getTrendingSearches(): List<String> {
        return _recentSearches.take(5)
    }
}

/**
 * Search results data class
 */
data class SearchResults(
    val classes: List<SearchResultItem> = emptyList(),
    val assignments: List<SearchResultItem> = emptyList(),
    val people: List<SearchResultItem> = emptyList(),
    val notifications: List<SearchResultItem> = emptyList()
) {
    val allCount: Int
        get() = classes.size + assignments.size + people.size + notifications.size
}

/**
 * Individual search result item
 */
data class SearchResultItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val iconResId: Int? = null,
    val type: String // class, assignment, person, notification
)

/**
 * Advanced filters for specific searches
 */
sealed class SearchFilter {
    data class AssignmentFilter(
        val status: String? = null, // pending, submitted, graded
        val dueDate: Long? = null,
        val gradeRange: IntRange? = null
    ) : SearchFilter()

    data class QuizFilter(
        val difficulty: String? = null,
        val scoreRange: IntRange? = null
    ) : SearchFilter()

    data class ChatFilter(
        val userId: String? = null,
        val dateRange: Pair<Long, Long>? = null
    ) : SearchFilter()
}

