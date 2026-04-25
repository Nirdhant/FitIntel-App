package com.example.authentication.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.theme.BlueGray
import com.example.ui.theme.Roboto
import com.example.ui.theme.Black

@Composable
fun LoginScreen(
    onSignUpClick: () -> Unit,
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
){

    val uiState = viewModel.uiState.collectAsState()
    val navigateToHome = viewModel.navigateToHome.collectAsState()
    val context = LocalContext.current

    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val emailState=remember{mutableStateOf("")}
    val passwordState=remember{mutableStateOf("")}

    LaunchedEffect(uiState.value.error) {
        uiState.value.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(navigateToHome.value) {
        if (navigateToHome.value) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            viewModel.consumedNavigation()
        }
    }
    Surface {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(contentAlignment = Alignment.TopCenter)
            {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.55f) ,
                    painter=painterResource(id = R.drawable.shape),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                    ,modifier = Modifier.align(alignment = Alignment.Center)
                        .padding(bottom = 100.dp, start = 30.dp))
                {
                    Image(
                        painter = painterResource(id = R.drawable.fitintel),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp).align(Alignment.CenterVertically)
                    )

                }
                Text(text ="LogIn" ,modifier= Modifier
                    .padding(bottom = 16.dp)
                    .align(alignment = Alignment.BottomCenter),style = MaterialTheme.typography.headlineLarge ,color=uiColor)
            }
            //--------------------------------------------------------------------------------------
            Spacer(modifier = Modifier.height(35.dp))
            Column(modifier= Modifier
                .padding(horizontal = 25.dp)
                .fillMaxSize())
            {
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
                Button(modifier= Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                    colors =ButtonDefaults.buttonColors(
                        containerColor=if(isSystemInDarkTheme()) BlueGray else Black ,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp),
                    enabled = !uiState.value.isLoading,
                    onClick = {
                        viewModel.login(
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
                            text = "Log In",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 17.sp
                            )
                        )
                    }
                }
               //-----------------------------------------------------------------------------------
                Spacer(modifier = Modifier.height(30.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier= Modifier
                        .fillMaxWidth(),
                        contentAlignment=Alignment.Center){
                        ClickableText(text = buildAnnotatedString {
                            withStyle(style=SpanStyle(
                                color=Color(0xFF94A3B8),
                                fontFamily = Roboto,
                                fontSize=15.sp,
                                fontWeight = FontWeight.Normal
                            )){ append("Don't Have Account?")}

                            withStyle(style=SpanStyle(
                                    color=uiColor,
                                    fontFamily = Roboto,
                                    fontSize=15.sp,
                                    fontWeight = FontWeight.Medium
                                )){
                                append(" ")
                                append("SignUp")
                            }
                        },
                            onClick = {
                                val signUpStart = "Don't Have Account? ".length
                                val signUpEnd = signUpStart + "SignUp".length
                                if (it in signUpStart until signUpEnd)
                                {
                                    onSignUpClick()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

}


