package np.ict.mad.t01_team04

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import np.ict.mad.t01_team04.ui.theme.MAD25_T01_Team04Theme

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MAD25_T01_Team04Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        onLoginSuccess = {
                            val intent = Intent(this@LoginScreen, NavigationUI::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier.padding(innerPadding)
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
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Login Page", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.padding(12.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {username = it},
                label = { Text(text = "Username")},
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Username") }
            )
            Spacer(modifier = Modifier.padding(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text(text = "Password")},
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") }
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
                }
            ){
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = {
                    if(username.isNotEmpty() && password.isNotEmpty()){
                        scope.launch {
                            val isCreated = performSignUp(context, username, password)
                            if (isCreated) {
                                Toast.makeText(context, "Successfully Created User!!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context,"Sign Up failed!", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context,"Enter some details?", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Sign Up")
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