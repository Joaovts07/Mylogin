import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylogin.validators.DateMaskTransformation
import com.example.mylogin.validators.isValidBirthDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBasicScreen(navController: NavController) {
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var birthDateError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registration - Step 1") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = { date ->
                    if (date.filter { it.isDigit() }.length <= 8) { // Limita a 8 dígitos
                        birthDate = date.filter { it.isDigit() }
                    }
                },
                label = { Text("Birth Date (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateMaskTransformation(),
                isError = birthDateError,
                supportingText = {
                    if (birthDateError) {
                        Text("Invalid date or age under 18.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        birthDateError = !isValidBirthDate(birthDate)
                        if (!birthDateError) {
                            navController.navigate("choiseForm/${fullName}/${birthDate}")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Next")
                }
            }
        }
    }
}