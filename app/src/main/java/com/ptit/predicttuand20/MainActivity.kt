package com.ptit.predicttuand20

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.predicttuand20.ui.theme.PredictTuanD20Theme
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

object YoloRetrofitClient {
    private const val BASE_URL = "http://100.68.49.61:8080/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }
}

object FasterRetrofitClient {
    private const val BASE_URL = "http://100.68.49.61:8181/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .build()
    }
}


object ApiClient {
    val yolo: PredictAPI by lazy {
        YoloRetrofitClient.retrofit.create(PredictAPI::class.java)
    }

    val faster: PredictAPI by lazy {
        FasterRetrofitClient.retrofit.create(PredictAPI::class.java)
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val scope = rememberCoroutineScope()
            val bitmap = remember { mutableStateOf<Bitmap?>(null) }
            PredictTuanD20Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (bitmap.value != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "PHÁT HIỆN BỆNH RĂNG SÂU",
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    modifier = Modifier.background(
                                        color = Color.Green,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                    onClick = {
                                        scope.launch {
                                            val res = ApiClient.faster.predict()
                                            res.enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    bitmap.value = response.body()?.byteStream()?.use {
                                                        BitmapFactory.decodeStream(it)
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseBody>,
                                                    t: Throwable
                                                ) {

                                                }
                                            })
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Faster R-CNN",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Button(
                                    modifier = Modifier.background(
                                        color = Color.Green,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                    onClick = {
                                        scope.launch {
                                            val res = ApiClient.yolo.predict()
                                            res.enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    bitmap.value = response.body()?.byteStream()?.use {
                                                        BitmapFactory.decodeStream(it)
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseBody>,
                                                    t: Throwable
                                                ) {

                                                }
                                            })
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "YOLOv3",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Button(
                                modifier = Modifier.background(
                                    color = Color.Green,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                                onClick = {

                                }
                            ) {
                                Text(
                                    text = "Tải ảnh lên",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }


                            Box {

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PredictTuanD20Theme {
        Greeting("Android")
    }
}