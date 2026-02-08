package np.ict.mad.t01_team04

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AccountSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountSettingsScreen(
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun AccountSettingsScreen(onBack: () -> Unit) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current

    var displayName by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var newPassword by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp)
    ) {

        Text(
            text = "Account Settings",
            fontSize = 24.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))


        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name", color = Color.LightGray) },
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.2f))
        )


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (user != null) {
                    val request = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user.updateProfile(request).addOnSuccessListener {
                        Toast.makeText(
                            context,
                            "Display name updated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Display Name")
        }

        Spacer(modifier = Modifier.height(24.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", color = Color.LightGray) },
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (user != null && email.isNotBlank()) {
                    user.updateEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Email updated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Re-authentication required",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Email")
        }

        Spacer(modifier = Modifier.height(24.dp))


        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password", color = Color.LightGray) },
            textStyle = TextStyle(color = Color.White),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (user != null && newPassword.length >= 6) {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Password updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            newPassword = ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Re-authentication required",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change Password")
        }

        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = { showDeleteDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }


    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        user?.delete()
                        Toast.makeText(
                            context,
                            "Account deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

