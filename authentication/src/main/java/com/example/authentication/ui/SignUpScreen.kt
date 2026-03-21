package com.example.authentication.ui


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.authentication.R
import com.example.authentication.viewmodel.AuthViewModel
import com.example.navigation.Screen
import com.example.ui.theme.Black
import com.example.ui.theme.BlueGray
import com.example.ui.theme.Roboto


@Composable
fun SignUpScreen(
    onLoginClick: () -> Unit,
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
){
    val uiState = viewModel.uiState.collectAsState()
    val navigateToHome = viewModel.navigateToHome.collectAsState()
    val context = LocalContext.current

    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }

    LaunchedEffect(uiState.value.error) {
        uiState.value.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(navigateToHome.value) {
        if (navigateToHome.value) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.SignUp.route) { inclusive = true }
            }
            viewModel.consumedNavigation()
        }
    }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.TopCenter)
            {
                Image(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.45f),
                    painter = painterResource(id = R.drawable.shape),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 20.dp))
                {
                    Icon(modifier = Modifier.size(200.dp), painter = painterResource(id = R.drawable.fitintel), contentDescription = null,
                        tint = uiColor
                    )
                }
                Text(
                    text = "Sign Up",
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(alignment = Alignment.BottomCenter), style = MaterialTheme.typography.headlineLarge,
                    color = uiColor
                )
            }
            //--------------------------------------------------------------------------------------
            Spacer(modifier = Modifier.height(35.dp))
            Column(modifier = Modifier
                .padding(horizontal = 25.dp)
                .fillMaxSize())
            {
                LoginTextField(
                    usernameState.value,
                    usernameState,
                    modifier = Modifier.fillMaxWidth(),
                    label = "UserName",
                    trailing = ""
                )
                Spacer(modifier = Modifier.height(15.dp))
                LoginTextField(
                    emailState.value,
                    emailState,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Email",
                    trailing = ""
                )
                Spacer(modifier = Modifier.height(15.dp))
                LoginTextField(
                    passwordState.value,
                    passwordState,
                    modifier = Modifier.fillMaxWidth(),
                    label = "Password",
                    trailing = ""
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp),
                    enabled = !uiState.value.isLoading,
                    onClick = {
                        viewModel.signUp(
                            email = emailState.value,
                            password = passwordState.value
                        )
                    }
                )
                {
                    if (uiState.value.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier= Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.8f),
                        contentAlignment=Alignment.BottomCenter
                    )
                    {
                        ClickableText(text = buildAnnotatedString {
                            withStyle(style=SpanStyle(
                                color=Color(0xFF94A3B8),
                                fontFamily = Roboto,
                                fontSize=14.sp,
                                fontWeight = FontWeight.Normal
                            )){ append("Already Have Account?")}

                            withStyle(style=SpanStyle(
                                color=uiColor,
                                fontFamily = Roboto,
                                fontSize=14.sp,
                                fontWeight = FontWeight.Medium
                            )){
                                append(" ")
                                append("Sign In")
                            }
                        },
                            onClick = {
                                val signInStart = "Already Have Account? ".length
                                val signInEnd = signInStart + "SignIn".length
                                if (it in signInStart until signInEnd)
                                {
                                    onLoginClick()
                                }
                            }
                        )

                    }

                }
            }
        }
    }

}
