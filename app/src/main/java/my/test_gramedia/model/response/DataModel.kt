package my.test_gramedia.model.response

import com.squareup.moshi.Json

data class DataModel(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "price") val price: Double,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String,
    @Json(name = "image") val image: String,
    @Json(name = "rating") val rating: RatingModel
)

data class RatingModel(
    @Json(name = "rate") val rate: Double,
    @Json(name = "count") val count: Int
)