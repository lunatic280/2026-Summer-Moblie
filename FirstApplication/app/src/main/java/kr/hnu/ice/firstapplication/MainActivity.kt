package kr.hnu.ice.firstapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.hnu.ice.firstapplication.ui.theme.FirstApplicationTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FirstApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val showDate = remember { mutableStateOf(false) }
    
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = {}) {
            Text("테스트")
        }
        Button(
            onClick = { showDate.value = true },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("오늘 날짜 보기")
        }
        
        if (showDate.value) {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            Text(
                text = "오늘 날짜: ${today.format(formatter)}",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FirstApplicationTheme {
        Greeting("Android")
    }
}