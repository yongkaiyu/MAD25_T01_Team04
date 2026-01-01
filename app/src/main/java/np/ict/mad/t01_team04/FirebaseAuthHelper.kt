package np.ict.mad.t01_team04

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthHelper{

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun toEmail(username: String): String {
        return if(username.contains("@")){
            username
        } else{
            "$username@navigationUI.com" //fake email
        }
    }

    suspend fun signIn(username: String, password: String): Boolean {
        return try {
            val email = toEmail(username)
            val result = auth.signInWithEmailAndPassword(email,password).await()
            val user = result.user

            // Retrieve displayName after login
            val displayName = user?.displayName
            val userId = user?.uid
            Log.d("FirebaseHelper", "Logged in as: $displayName")
            Log.d("FirebaseHelper", "User ID: $userId")

            user!= null
        } catch (e: Exception){
            Log.e("FirebaseHelper", "Login Failed!", e)
            false
        }
    }
    suspend fun signUp(username: String, password: String): Boolean
    {
        return try {
            val email = toEmail(username)
            val result = auth.createUserWithEmailAndPassword(email,password).await()
            val user = result.user

            // Set displayName
            user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(username)  // store the username
                    .build()
            )?.await()

            user!= null
        } catch (e: Exception){
            Log.e("FirebaseHelper", "Sign Up Failed!", e)
            false
        }
    }
}