package com.familyquran.app.auth

import com.familyquran.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

data class FamilyAccount(
    val id: String,
    val name: String,
    val label: String,
    val photoRes: Int
)

object FamilyAccounts {
    val all = listOf(
        FamilyAccount("asep", "Asep Suryana", "Ayah", R.drawable.ayah),
        FamilyAccount("leli", "Leli Robiyanti", "Ibu", R.drawable.ibu),
        FamilyAccount("nazhifa", "Nazhifa Qalbi Zhafira", "Teteh", R.drawable.teteh),
        FamilyAccount("raihan", "Raihan Rafiful Allam", "Kakak", R.drawable.kaka),
        FamilyAccount("raffa", "Raffa", "Handsome", R.drawable.handsome),
        FamilyAccount("zaskia", "Zaskia", "Teteh", R.drawable.zaskia),
    )
}

const val FAMILY_EMAIL = "rainara@keluarga.app"
const val FAMILY_USERNAME = "rainara"
const val FAMILY_PASSWORD = "rainara123"

class AuthManager {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isFamilyLoggedIn: Boolean get() = currentUser?.email == FAMILY_EMAIL

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

    fun removeAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.removeAuthStateListener(listener)
    }
}
