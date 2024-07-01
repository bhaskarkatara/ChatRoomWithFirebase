package screen

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chatroom.Screen
import viewmodel.AuthViewModel
import data.Result
import data.Result.*
import data.UserRepository
//import data.userLogin
import kotlin.math.log

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit, // this is the lambda function Call this function to navigate to the sign up screen
    onSignInSuccess:()->Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember {
        mutableStateOf("")
    }

    val result by authViewModel.authResult.observeAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = email,
            singleLine = true,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = {

                authViewModel.login(email, password)

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (result) {
            is Success -> {
                navController.navigate(Screen.ChatRoomsScreen.route)
                val context = LocalContext.current
               Toast.makeText(context,"user Login Successful",Toast.LENGTH_LONG).show()
            }

            is Error -> {

                val context = LocalContext.current
                Toast.makeText(context,"user Login Failed",Toast.LENGTH_LONG).show()
            }

            else -> {
            }
        }
        Text("Don't have an account? Sign up.",
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )
    }
}