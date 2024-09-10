package org.dany.sqlcipher.sample

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dany.sqlcipher.sample.ui.theme.SampleAppTheme

class SampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleAppTheme {
                var openAddSecretDialog by remember { mutableStateOf(false) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        val uiState by viewModel<SampleViewModel>().uiState
                        if (uiState is UiState.OpenDatabase) {
                            ExtendedFloatingActionButton(
                                onClick = {
                                    openAddSecretDialog = true
                                },
                                icon = { Icon(Icons.Filled.Add, "Add") },
                                text = { Text(text = "Add Secret") },
                            )
                        }
                    },
                    content = { innerPadding ->
                        MainContent(modifier = Modifier.padding(innerPadding))
                    }
                )
                if (openAddSecretDialog) {
                    InsertDatabaseContent(onDismiss = { openAddSecretDialog = false})
                }
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val viewModel = viewModel<SampleViewModel>()
    val uiState by viewModel.uiState

    Column(modifier = modifier) {
        var passphrase by remember { mutableStateOf("") }
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = passphrase,
                singleLine = true,
                label = { Text("Database passphrase") },
                onValueChange = { passphrase = it },
                isError = passphrase.isBlank(),
                enabled = uiState !is UiState.OpenDatabase
            )
            Button(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                onClick = {
                    if (uiState is UiState.OpenDatabase) {
                        viewModel.closeDatabase()
                    } else {
                        if (passphrase.isEmpty()) {
                            showToast(context, "Passphrase is empty")
                        } else {
                            viewModel.openDatabase(context, passphrase)
                        }
                    }
                },
                content = {
                    if (uiState is UiState.OpenDatabase) {
                        Text("Close DB")
                    } else {
                        Text("Decrypt!")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        when (uiState) {
            is UiState.OpenDatabase -> {
                OpenDatabaseContent(uiState as UiState.OpenDatabase)
            }
            UiState.ClosedDatabase -> {
                Text(
                    """
                    Database is closed...
                    Enter a passphrase and tap on 'Decrypt'.
                    """.trimIndent()
                )
            }
            UiState.LockedDatabase -> {
                Text(
                    """
                    Could not open database, wrong passphrase.
                    This is a sample app through, so I'll tell you...
                    The super secret password is 'foo'
                    """.trimIndent()
                )
            }
        }
    }
}

@Composable
fun OpenDatabaseContent(state: UiState.OpenDatabase) {
    Text("Database is open! The secrets are...")
    LazyColumn {
        for (secret in state.secrets) {
            item {
                Text("(${secret.id}) - ${secret.text}")
            }
        }
    }
}

@Composable
fun InsertDatabaseContent(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            var theSecret by remember { mutableStateOf("") }
            TextField(
                value = theSecret,
                singleLine = true,
                label = { Text("Tell me a secret...") },
                onValueChange = {
                    theSecret = it
                },
            )
            val viewModel = viewModel<SampleViewModel>()
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.insertSecret(theSecret)
                    onDismiss.invoke()
                },
                content = { Text("Done") }
            )
        }
    }
}

private fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SampleAppTheme {
        MainContent()
    }
}