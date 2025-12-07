package np.ict.mad.t01_team04

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import np.ict.mad.t01_team04.ui.theme.MAD25_T01_Team04Theme

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowCompat.getInsetsController(window,window.decorView)

        controller.isAppearanceLightNavigationBars = false;
        controller.isAppearanceLightStatusBars = false;

        setContent {
            MAD25_T01_Team04Theme {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    LoginScreen(
                        onLoginSuccess = {
                            val intent = Intent(this@LoginScreen, NavigationUI::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
){
    var username by rememberSaveable { mutableStateOf("")}
    var password by rememberSaveable {mutableStateOf("")}

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(24.dp)
    ){

        // Glowing background gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-200).dp)
                .size(350.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF9B4DFF), Color.Transparent),
                        radius = 500f
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Text(
                text = "CineXplorer",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = {username = it},
                label = { Text(text = "Username", color = Color.White)},
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username", tint = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B4DFF),
                    focusedLabelColor = Color(0xFF9B4DFF),
                    cursorColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = Color.White,
                    unfocusedLeadingIconColor = Color.White
                )
            )
            Spacer(modifier = Modifier.padding(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "Password", color = Color.White)},
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color.White) },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF9B4DFF),
                    focusedLabelColor = Color(0xFF9B4DFF),
                    cursorColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = Color.White,
                    unfocusedLeadingIconColor = Color.White
                )
            )

            Spacer(modifier = Modifier.padding(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        val isValid = validateLogin(context,username,password)
                        if (isValid)
                        {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context,"Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9B4DFF)
                ),
                shape = RoundedCornerShape(12.dp)
            ){
                Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.padding(16.dp))

            TextButton(
                onClick = {
                    if(username.isEmpty() || password.isEmpty()){
                        Toast.makeText(context, "Enter username/password", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        scope.launch {
                            val isCreated = performSignUp(context, username, password)
                            if (isCreated) {
                                Toast.makeText(
                                    context,
                                    "Successfully Created User!!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(context, "Sign Up failed!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }

                }
            ) {
                Text("Create an Account", color = Color(0xFF9B4DFF))
            }
        }

    }

}

suspend fun performSignUp(context: Context, username: String, password: String): Boolean{
    // Firebase
    return FirebaseHelper().signUp(username,password)
    // Room

    /*val db = AppDatabase.getDatabase(context)
    if (db.userDao().getUser(username) == null){
        db.userDao().insertUser(UserEntity(username, password))
        return true
    }
    return false */

    // DataStore
    //return DataStoreHelper(context).saveUser(username, password)
    //SharedPreference
    //val prefsHelper = SharedPreferencesHelper(context)
    //return prefsHelper.saveUser(username, password)
    //return false
}

suspend fun validateLogin(context: Context, username: String, password: String): Boolean{
    // Firebase
    return FirebaseHelper().signIn(username,password)
    // Room
    /*val user = AppDatabase.getDatabase(context).userDao().getUser(username)
    return user!=null && user.password == password*/

    // DataStore
    //return DataStoreHelper(context).isValidUser(username,password)

    //Shared Preferences
    //val prefsHelper = SharedPreferencesHelper(context)
    //return prefsHelper.isValidUser(username,password)
    //return false

    /*
    val myUsername = "admin"
    val myPassword = "password"
    return username == myUsername && password == myPassword */
}

/* fun validateLogin(username: String, password: String): Boolean{
    val myUsername = "admin"
    val myPassword = "password"
    return username == myUsername && password == myPassword
} */