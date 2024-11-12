package com.ptit.predicttuand20

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PredictAPI {
    @Multipart
    @POST("api/predict")
    suspend fun predict(
       @Part file: MultipartBody.Part
    ): Call<ResponseBody>
}
