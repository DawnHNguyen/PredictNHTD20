package com.ptit.predicttuand20

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.predicttuand20.ui.theme.PredictTuanD20Theme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayOutputStream

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
            val selectedImageUri = remember { mutableStateOf<Uri>(Uri.EMPTY) }

            val pickerLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                    uri?.let {
                        selectedImageUri.value = it
                    } ?: run {
                        selectedImageUri.value = Uri.EMPTY
                    }
                }

            PredictTuanD20Theme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentColor = Color.White
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(innerPadding)
                            .padding(16.dp)
                    ) {
                        if (bitmap.value == null) {
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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        scope.launch {
                                            val res = ApiClient.faster.predict(
                                                getRequestBodyFromUri(
                                                    contentResolver,
                                                    selectedImageUri.value
                                                )
                                            )
                                            res.enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    bitmap.value =
                                                        response.body()?.byteStream()?.use {
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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        scope.launch {
                                            val res = ApiClient.yolo.predict(
                                                getRequestBodyFromUri(
                                                    contentResolver,
                                                    selectedImageUri.value
                                                )
                                            )
                                            res.enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    bitmap.value =
                                                        response.body()?.byteStream()?.use {
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
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                onClick = {
                                    pickerLauncher.launch(
                                        PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                }
                            ) {
                                Text(
                                    text = "Tải ảnh lên",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color.Gray)
                            ) {
                                if (selectedImageUri.value != Uri.EMPTY) {
                                    Image(
                                        bitmap = getImageBitmap(
                                            contentResolver,
                                            selectedImageUri.value
                                        ).asImageBitmap(),
                                        contentDescription = "Image",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else
                                    Column(
                                        modifier = Modifier.align(Alignment.Center),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Warning",
                                            tint = Color.White,
                                        )
                                        Text(
                                            text = "Your image",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                        )
                                    }
                            }
                        }
                        else {
                            Image(
                                bitmap = bitmap.value!!.asImageBitmap(),
                                contentDescription = "Image",
                                modifier = Modifier.fillMaxSize()
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                onClick = {
                                   bitmap.value = null
                                }
                            ) {
                                Text(
                                    text = "Quay lại",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
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

private fun getImageBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap {
    val inputStream = contentResolver.openInputStream(uri)
    val image = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()
    return image
}

private fun getRequestBodyFromUri(contentResolver: ContentResolver, uri: Uri): MultipartBody.Part {
    val image = getImageBitmap(contentResolver, uri)
    val byteArrayOutputStream = ByteArrayOutputStream()
    return image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream).let {
        MultipartBody.Part.create(
            RequestBody.create(
                okhttp3.MediaType.parse("image/*"),
                byteArrayOutputStream.toByteArray()
            )
        )
    }
}