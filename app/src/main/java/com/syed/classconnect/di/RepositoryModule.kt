package com.syed.classconnect.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * RepositoryModule — placeholder for future interface-to-implementation bindings.
 *
 * All concrete Repository classes are annotated with @Singleton + @Inject constructor
 * and are therefore auto-bound by Hilt without explicit @Provides methods.
 * This module exists as the conventional home for any abstract @Binds methods
 * that may be added when repositories are extracted behind interfaces for easier
 * unit testing.
 *
 * Example pattern:
 *   @Binds abstract fun bindClassRepo(impl: ClassRepository): IClassRepository
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // All current repositories use @Inject constructor — no explicit bindings needed.
    // Add @Binds methods here when repository interfaces are introduced.
}
