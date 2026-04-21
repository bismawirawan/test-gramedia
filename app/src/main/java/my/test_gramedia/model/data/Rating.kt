package my.test_gramedia.model.data

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class Rating(
    @Json(name = "rate") val rate: Double,
    @Json(name = "count") val count: Int
)

