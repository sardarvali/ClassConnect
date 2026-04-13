package com.syed.classconnect.ui.util

import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout

/**
 * Skeleton loading helpers using Shimmer effect
 * Replaces static progress bars with animated skeletons
 */
class SkeletonLoadingHelper(private val context: android.content.Context) {

    /**
     * Creates and configures a shimmer effect for skeleton loading
     */
    fun setupShimmer(shimmerLayout: ShimmerFrameLayout) {
        shimmerLayout.startShimmer()
    }

    /**
     * Stops shimmer effect and shows actual content
     */
    fun stopShimmer(shimmerLayout: ShimmerFrameLayout, contentView: View) {
        shimmerLayout.stopShimmer()
        shimmerLayout.visibility = View.GONE
        contentView.visibility = View.VISIBLE
    }

    /**
     * Shows error state with skeleton loader
     */
    fun showSkeletonError(shimmerLayout: ShimmerFrameLayout) {
        shimmerLayout.startShimmer()
        shimmerLayout.visibility = View.VISIBLE
    }
}

/**
 * Loading state representations
 */
sealed class LoadingState {
    object Loading : LoadingState()
    data class Success<T>(val data: T) : LoadingState()
    data class Error(val exception: Exception) : LoadingState()
}

