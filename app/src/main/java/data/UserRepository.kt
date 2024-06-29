package data

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import data.Result

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
) {

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(firstName, lastName, email)
            saveUserToFirestore(user)

            Result.Success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Sign-up failed", e)
            Result.Error(e)
        }


    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
//            userLogin(false)
//            Result.Loading(false)
            Result.Success(true)
        } catch (e: Exception) {
//            Log.e(TAG, "Login failed", e)
//            userLogin(false)
//            Result.UserLogin(false)
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            firestore.collection("users").document(user.email).set(user).await()
            Log.d(TAG, "User saved to Firestore: ${user.email}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user to Firestore", e)
            throw e
        }
    }

    suspend fun getCurrentUser(): Result<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d("user2","$uid")
                Result.Success(user)
            } else {
               Result.Error(Exception("User data not found"))
            }
        } else {
           Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }

}
//data class userLogin(
//    val isLoading :Boolean = true
//)
