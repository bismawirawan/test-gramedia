package my.test_gramedia.network.services

import my.test_gramedia.model.response.DataModel
import retrofit2.http.GET
import retrofit2.http.Headers

interface DataServices {

    @GET("products")
    suspend fun getData(): List<DataModel>
}