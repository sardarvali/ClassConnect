package com.syed.classconnect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.data.model.Institution
import com.syed.classconnect.data.model.User
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.ValidationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed")
        val user = getUserById(uid) ?: throw Exception("User data not found")
        // Self-heal: admins should always be approved
        if (user.role == Constants.ROLE_ADMIN && !user.isApproved) {
            firestore.collection(Constants.COLLECTION_USERS).document(uid)
                .update("isApproved", true).await()
            return@runCatching user.copy(isApproved = true)
        }
        user
    }

    suspend fun register(
        name: String, email: String, password: String,
        role: String, institutionCode: String
    ): Result<User> = runCatching {
        // Validate institution code
        val instSnap = firestore.collection(Constants.COLLECTION_INSTITUTIONS)
            .whereEqualTo("joinCode", institutionCode).get().await()
        if (instSnap.isEmpty) throw Exception("invalid_institution_code")
        val institution = instSnap.documents[0].toObject(Institution::class.java)!!
            .copy(id = instSnap.documents[0].id)

        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Registration failed")

        val user = User(
            uid = uid, name = ValidationUtils.sanitize(name, 100),
            email = email, role = role,
            institutionId = institution.id,
            isApproved = role == Constants.ROLE_ADMIN  // admins are auto-approved
        )
        firestore.collection(Constants.COLLECTION_USERS).document(uid).set(user).await()
        user
    }

    suspend fun signInWithGoogle(idToken: String): Result<Pair<User, Boolean>> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val uid = result.user?.uid ?: throw Exception("Google sign-in failed")
        val isNewUser = result.additionalUserInfo?.isNewUser ?: false
        val existing = getUserById(uid)
        if (existing != null) {
            // Self-heal: admins should always be approved
            val healedUser = if (existing.role == Constants.ROLE_ADMIN && !existing.isApproved) {
                firestore.collection(Constants.COLLECTION_USERS).document(uid)
                    .update("isApproved", true).await()
                existing.copy(isApproved = true)
            } else existing
            Pair(healedUser, false)
        } else {
<<<<<<< HEAD
            Pair(User(uid = uid, name = result.user?.displayName ?: "",
                email = result.user?.email ?: "", photoUrl = result.user?.photoUrl?.toString() ?: ""), isNewUser)
=======
            Pair(
                User(
                    uid = uid,
                    name = result.user?.displayName ?: "",
                    email = result.user?.email ?: "",
                    photoUrl = result.user?.photoUrl?.toString() ?: ""
                ), isNewUser
            )
>>>>>>> final
        }
    }

    suspend fun saveNewGoogleUser(user: User): Result<Unit> = runCatching {
        firestore.collection(Constants.COLLECTION_USERS).document(user.uid).set(user).await()
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() = auth.signOut()

    suspend fun getUserById(uid: String): User? {
        val snap = firestore.collection(Constants.COLLECTION_USERS).document(uid).get().await()
        return snap.toObject(User::class.java)?.copy(uid = snap.id)
    }

    fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run { trySend(null); close(); return@callbackFlow }
        val sub = firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .addSnapshotListener { snap, err ->
<<<<<<< HEAD
                if (err != null) { close(err); return@addSnapshotListener }
=======
                if (err != null) {
                    close(err); return@addSnapshotListener
                }
>>>>>>> final
                trySend(snap?.toObject(User::class.java)?.copy(uid = snap.id))
            }
        awaitClose { sub.remove() }
    }

    suspend fun updateFcmToken(uid: String, token: String) {
        firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .update("fcmToken", token).await()
    }

    suspend fun updateUserProfile(uid: String, name: String, bio: String, photoUrl: String) {
        firestore.collection(Constants.COLLECTION_USERS).document(uid)
            .update(mapOf("name" to name, "bio" to bio, "photoUrl" to photoUrl)).await()
    }

<<<<<<< HEAD
    suspend fun createInstitution(name: String, adminUid: String): Result<Institution> = runCatching {
        val code = ValidationUtils.generateCode(6)
        val inst = Institution(name = name, adminId = adminUid, joinCode = code)
        val ref = firestore.collection(Constants.COLLECTION_INSTITUTIONS).document()
        ref.set(inst.copy(id = ref.id)).await()
        inst.copy(id = ref.id)
    }
=======
    suspend fun createInstitution(name: String, adminUid: String): Result<Institution> =
        runCatching {
            val code = ValidationUtils.generateCode(6)
            val inst = Institution(name = name, adminId = adminUid, joinCode = code)
            val ref = firestore.collection(Constants.COLLECTION_INSTITUTIONS).document()
            ref.set(inst.copy(id = ref.id)).await()
            inst.copy(id = ref.id)
        }
>>>>>>> final

    suspend fun approveUser(uid: String, approved: Boolean) {
        val userRef = firestore.collection(Constants.COLLECTION_USERS).document(uid)
        if (approved) {
            // Update both possible field names for backwards compatibility
<<<<<<< HEAD
            userRef.update(mapOf(
                "isApproved" to true,
                "approved" to true
            )).await()
        } else {
            userRef.update(mapOf(
                "isApproved" to false,
                "approved" to false,
                "isRejected" to true,
                "rejected" to true
            )).await()
=======
            userRef.update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true
                )
            ).await()
        } else {
            userRef.update(
                mapOf(
                    "isApproved" to false,
                    "approved" to false,
                    "isRejected" to true,
                    "rejected" to true
                )
            ).await()
>>>>>>> final
        }
    }

    /** One-shot fetch of all users in an institution (forces server, not cache). */
    suspend fun getUsersForInstitutionOnce(institutionId: String): List<User> {
        val snap = firestore.collection(Constants.COLLECTION_USERS)
            .whereEqualTo("institutionId", institutionId)
            .get(com.google.firebase.firestore.Source.SERVER).await()
        return snap.documents.mapNotNull { it.toObject(User::class.java)?.copy(uid = it.id) }
    }

    /** PATH A: Register with institution code → admin approval flow */
    suspend fun registerWithInstitutionCode(
        name: String, email: String, password: String, role: String, institutionCode: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val institutionQuery = firestore.collection(Constants.COLLECTION_INSTITUTIONS)
                .whereEqualTo("joinCode", institutionCode.uppercase()).get().await()
            if (institutionQuery.isEmpty)
                return@withContext Result.failure(Exception("Invalid institution code"))
            val institutionId = institutionQuery.documents.first().id
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            authResult.user?.sendEmailVerification()
            val isAdmin = role == Constants.ROLE_ADMIN
            val userDoc = mapOf(
                "uid" to uid, "name" to ValidationUtils.sanitize(name, 100),
                "email" to email, "photoUrl" to "", "role" to role,
                "institutionId" to institutionId, "isApproved" to isAdmin, // admins auto-approved
                "accountType" to "institution", "emailVerified" to false,
                "fcmToken" to "", "classIds" to emptyList<String>(),
                "createdAt" to FieldValue.serverTimestamp(), "bio" to "",
                "roleChangedAt" to null, "roleChangedBy" to ""
            )
            firestore.collection(Constants.COLLECTION_USERS).document(uid).set(userDoc).await()
            Result.success(uid)
<<<<<<< HEAD
        } catch (e: Exception) { Result.failure(e) }
=======
        } catch (e: Exception) {
            Result.failure(e)
        }
>>>>>>> final
    }

    /** PATH B: Register without institution code → email verification → auto-approve */
    suspend fun registerIndependent(
        name: String, email: String, password: String, role: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            authResult.user?.sendEmailVerification()?.await()
            val userDoc = mapOf(
                "uid" to uid, "name" to ValidationUtils.sanitize(name, 100),
                "email" to email, "photoUrl" to "", "role" to role,
                "institutionId" to "", "isApproved" to false,
                "accountType" to "independent", "emailVerified" to false,
                "fcmToken" to "", "classIds" to emptyList<String>(),
                "createdAt" to FieldValue.serverTimestamp(), "bio" to "",
                "roleChangedAt" to null, "roleChangedBy" to ""
            )
            firestore.collection(Constants.COLLECTION_USERS).document(uid).set(userDoc).await()
            Result.success(uid)
<<<<<<< HEAD
        } catch (e: Exception) { Result.failure(e) }
=======
        } catch (e: Exception) {
            Result.failure(e)
        }
>>>>>>> final
    }

    /** Called when email verification confirmed — auto-approves independent user */
    suspend fun approveIndependentUser(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection(Constants.COLLECTION_USERS).document(uid)
                .update(mapOf("isApproved" to true, "emailVerified" to true)).await()
            Result.success(Unit)
<<<<<<< HEAD
        } catch (e: Exception) { Result.failure(e) }
=======
        } catch (e: Exception) {
            Result.failure(e)
        }
>>>>>>> final
    }
}

