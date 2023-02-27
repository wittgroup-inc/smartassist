package com.wittgroup.smartassist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wittgroup.smartassist.ui.components.AppBar
import com.wittgroup.smartassist.ui.components.ChatBar
import com.wittgroup.smartassist.ui.components.ConversationView
import com.wittgroup.smartassist.ui.components.TripleDotProgressIndicator
import com.wittgroup.smartassist.ui.theme.Purple200
import com.wittgroup.smartassist.ui.theme.Purple500
import com.wittgroup.smartassist.ui.theme.Purple700
import com.wittgroup.smartassist.ui.theme.SmartAssistTheme
import com.wittgroup.smartassist.util.RecognitionCallbacks
import kotlinx.coroutines.delay
import java.util.*

class HomeActivity : ComponentActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        val speechRecognizerIntent = initSpeakRecognizer()

        setContent {
            SmartAssistTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {

                    Navigation {
                        HomeScreen(viewModel = viewModel, upAction = {
                            speechRecognizer.stopListening()
                            viewModel.stopListening()
                        }, downAction = {
                            viewModel.startListening()
                            speechRecognizer.startListening(speechRecognizerIntent)
                        }, onClick = {
                            viewModel.homeModel.value?.let {
                                viewModel.ask(it.textFieldValue.value.text)
                            }
                        }

                        )
                    }


                }
            }
        }
    }

    private fun initSpeakRecognizer(): Intent {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer.setRecognitionListener(object : RecognitionCallbacks() {

            override fun onBeginningOfSpeech() {
                viewModel.beginnigSpeach()
            }

            override fun onError(error: Int) {
                Log.d(TAG, "Error $error")
            }

            override fun onResults(results: Bundle?) {
                Log.d(TAG, "Result $results")
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                data?.let {
                    viewModel.ask(data[0])
                } ?: Log.d(TAG, "")
            }
        })

        return intent
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RECORD_AUDIO_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 200
        private const val TAG = "SmartAssist:Home"
    }
}


@Composable
fun HomeScreen(
    viewModel: HomeViewModel, upAction: () -> Unit, downAction: () -> Unit, onClick: () -> Unit
) {
    val state = viewModel.homeModel.observeAsState()
    state.value?.let { model ->
        Scaffold(
            topBar = { AppBar(title = "Smart Assist") },
            content = { padding ->
                Column {
                    ConversationView(modifier = Modifier.weight(1f), model.row)
                    if (model.showLoading) {
                        Box(
                            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                        ) {
                            TripleDotProgressIndicator()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .heightIn(min = 64.dp)
                            .padding(padding),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Log.d("TAG###", "TEXT: ${model.textFieldValue.value.text}")
                        ChatBar(
                            state = model.textFieldValue,
                            hint = model.hint,
                            icon = if (model.micIcon) R.drawable.ic_mic_on else R.drawable.ic_mic_off,
                            modifier = Modifier.padding(16.dp),
                            actionUp = upAction,
                            actionDown = downAction,
                            onClick = onClick
                        )
                    }
                }
            },
        )
    }
}

@Composable
fun Navigation(homeScreen: @Composable() () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen",

        ) {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        // Main Screen
        composable("main_screen") {
            homeScreen()
        }
    }
}


@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        navController.navigate("main_screen")
    }

    // Image
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.smartassist_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.scale(scale.value)

                )
                Text(text = "SMART ASSIST", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Purple500), modifier = Modifier.padding(8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = "Powered by ChatGPT",
                    style = TextStyle(fontSize = 12.sp),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.openai_log),
                    contentDescription = "ChatGPT",
                    modifier = Modifier.scale(scale.value)
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SmartAssistTheme {
        // HomeScreen()
    }
}

