package my.test_gramedia.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import my.test_gramedia.model.response.DataModel
import my.test_gramedia.model.response.RatingModel

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    @Embedded
    val rating: RatingModel,
    val isFavorite: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

fun ProductEntity.toDataModel(): DataModel {
    return DataModel(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = rating
    )
}

fun DataModel.toEntity(isFavorite: Boolean = false): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = rating,
        isFavorite = isFavorite
    )
}

