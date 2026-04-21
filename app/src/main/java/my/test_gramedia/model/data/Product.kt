package my.test_gramedia.model.data

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Product(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "price") val price: Double,
    @Json(name = "description") val description: String,
    @Json(name = "category") val category: String,
    @Json(name = "image") val image: String,
    @Json(name = "rating") val rating: Rating
)

@Keep
class ErrorResponse(
    @Json(name = "status") val status: String?,
    @Json(name = "message") val message: String?
)
