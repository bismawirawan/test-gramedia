package my.test_gramedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import my.test_gramedia.common.states.ActionLiveData
import my.test_gramedia.common.states.UiState
import my.test_gramedia.database.entities.toEntity
import my.test_gramedia.model.response.DataModel
import my.test_gramedia.network.services.DataServices
import my.test_gramedia.repository.ProductRepository
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val services: DataServices,
    private val repository: ProductRepository
) : ViewModel() {

    val dataState = ActionLiveData<UiState>()
    var dataResponse = MutableLiveData<List<DataModel>>()
    var favoriteStatus = MutableLiveData<Map<Int, Boolean>>()

    fun getData() {
        dataState.sendAction(UiState.Loading)
        viewModelScope.launch {
            try {
                val cachedData = repository.getCachedProducts()

                if (cachedData.isNotEmpty()) {
                    dataState.sendAction(UiState.Success)
                    dataResponse.postValue(cachedData)
                    updateFavoriteStatuses(cachedData)
                } else {
                    fetchFromApiAndCache()
                }

            } catch (error: Exception) {
                dataState.sendAction(UiState.Error(error.message ?: "Unknown error"))
            }
        }
    }

    fun checkDataWithConnection(isConnected: Boolean) {
        dataState.sendAction(UiState.Loading)
        viewModelScope.launch {
            try {
                if (isConnected) {
                    fetchFromApiAndCache()
                } else {
                    val cachedData = repository.getCachedProducts()

                    if (cachedData.isNotEmpty()) {
                        dataState.sendAction(UiState.Success)
                        dataResponse.postValue(cachedData)
                        updateFavoriteStatuses(cachedData)
                    } else {
                        // Jika database kosong, hilangkan semua data
                        dataState.sendAction(UiState.Success)
                        dataResponse.postValue(emptyList())
                        favoriteStatus.postValue(emptyMap())
                    }
                }
            } catch (error: Exception) {
                dataState.sendAction(UiState.Error(error.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun fetchFromApiAndCache() {
        try {
            val response = services.getData()

            val productsToCache = response.map { it.toEntity(isFavorite = false) }
            repository.cacheProducts(productsToCache)

            dataState.sendAction(UiState.Success)
            dataResponse.postValue(response)
            updateFavoriteStatuses(response)

        } catch (error: Exception) {
            dataState.sendAction(UiState.Error(error.message ?: "Unknown error"))
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.toggleFavorite(productId)
                result.onSuccess { isFavorite ->
                    val currentMap = favoriteStatus.value?.toMutableMap() ?: mutableMapOf()
                    currentMap[productId] = isFavorite
                    favoriteStatus.postValue(currentMap)
                }
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }

    private fun updateFavoriteStatuses(products: List<DataModel>) {
        viewModelScope.launch {
            val statusMap = mutableMapOf<Int, Boolean>()
            products.forEach { product ->
                val isFavorite = repository.isFavorite(product.id)
                statusMap[product.id] = isFavorite
            }
            favoriteStatus.postValue(statusMap)
        }
    }
}