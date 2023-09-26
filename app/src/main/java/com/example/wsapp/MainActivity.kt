package com.example.wsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wsapp.ui.theme.WSAppTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class MainActivity : ComponentActivity() {

    var client = OkHttpClient()
    var webSocketMessage by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val wssUrl = "ws://192.168.7.79:8765"
        runWebSocket(wssUrl)

        setContent {
            WSAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HelloWorld(this@MainActivity)
                }
            }
        }
    }

    fun runWebSocket(url: String) {
        val wssRequest: Request = Request.Builder().url(url).build()
        val webSocket = OkHttpClient().newWebSocket(wssRequest, EchoWebSocketListener(this))
    }

}

private class EchoWebSocketListener(private val mainActivity: MainActivity)  : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        val messageString = "Heisann <3"

        webSocket.send(messageString)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        output("Receiving : " + text!!)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        output("Receiving bytes : " + bytes!!.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket!!.close(NORMAL_CLOSURE_STATUS, null)
        output("Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        output("Error : " + t.message)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    private fun output(txt: String) {
        println(txt)
        mainActivity.webSocketMessage = txt
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelloWorld(mainActivity: MainActivity) {
    var name by remember { mutableStateOf("World") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter IP Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "WebSocket Message: ${mainActivity.webSocketMessage}",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),

            onClick = {
                // Run the WebSocket
                val url = "ws://$name:8765"
                mainActivity.runWebSocket(url)
            }
        ) {
            Text("Click me!", fontSize = 30.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WSAppTheme {
        HelloWorld(MainActivity())
    }
}