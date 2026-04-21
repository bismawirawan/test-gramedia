package my.test_gramedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import my.test_gramedia.database.dao.ProductDao
import my.test_gramedia.database.entities.ProductEntity
import my.test_gramedia.database.entities.toDataModel
import my.test_gramedia.database.entities.toEntity
import my.test_gramedia.model.response.DataModel
import my.test_gramedia.network.services.DataServices
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {

    suspend fun getCachedProducts(): List<DataModel> {
        return try {
            productDao.getAllProducts().first().map { it.toDataModel() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun cacheProducts(products: List<ProductEntity>) {
        try {
            productDao.insertProducts(products)
        } catch (e: Exception) {
            // Ignore cache errors
        }
    }

    suspend fun toggleFavorite(productId: Int): Result<Boolean> {
        return try {
            val product = productDao.getProductById(productId)
            if (product != null) {
                val newFavoriteStatus = !product.isFavorite
                productDao.updateFavoriteStatus(productId, newFavoriteStatus)
                Result.success(newFavoriteStatus)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isFavorite(productId: Int): Boolean {
        return try {
            productDao.getProductById(productId)?.isFavorite ?: false
        } catch (e: Exception) {
            false
        }
    }
}

