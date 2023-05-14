import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme(colors = darkColors()) {
        Scaffold(
            topBar = {
                TopAppBar(contentPadding = PaddingValues(16.dp, 0.dp)) {
                    Image(
                        painter = painterResource("compose-multiplatform.xml"),
                        contentDescription = "Compose Multiplatform",
                        modifier = Modifier.padding(8.dp).size(32.dp)
                    )
                    Text("Chat: ${getPlatformName()}")
                }
            }
        ) { padding ->
            var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
            Column(
                modifier = Modifier.padding(padding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Messages(messages, Modifier.weight(1f))
                MessageInput(onSend = { message -> messages = messages + Message(message) })
            }
        }
    }
}

@Composable
fun MessageInput(onSend: (String) -> Unit = {}) {
    var input by remember { mutableStateOf("") }
    val sendMessage = {
        onSend(input)
        input = ""
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            modifier = Modifier.weight(1f),
            value = input,
            singleLine = true,
            onValueChange = { value -> input = value },
            trailingIcon = {
                IconButton(onClick = sendMessage) {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                }
            },
        )
    }
}

@Composable
fun Messages(messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier, userScrollEnabled = true) {
        itemsIndexed(messages) { index, message ->
            val localDateTime = message.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
            val dayOfWeek = localDateTime.dayOfWeek.name.take(3).toLowerCase(Locale.current).capitalize(Locale.current)
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            val time = "$hour:$minute"

            val background =
                if (index % 2 == 0) MaterialTheme.colors.primary.copy(alpha = 0.1f) else MaterialTheme.colors.surface
            Text(
                text = "$dayOfWeek @ $time  > ${message.content}",
                modifier.background(background)
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

data class Message(val content: String, val timestamp: Instant = Clock.System.now())

expect fun getPlatformName(): String