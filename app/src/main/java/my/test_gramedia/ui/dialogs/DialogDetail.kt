package my.test_gramedia.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import coil.load
import coil.transform.RoundedCornersTransformation
import my.test_gramedia.R
import my.test_gramedia.databinding.DialogDetailBinding
import my.test_gramedia.model.response.DataModel

class DialogDetail(
    context: Context,
    val data: DataModel,
    private val isFavorite: Boolean = false,
    private val onFavoriteClick: ((DataModel) -> Unit)? = null
): Dialog(context) {

    private lateinit var onSelectedString: OnSelectedString
    private val binding: DialogDetailBinding = DialogDetailBinding.inflate(LayoutInflater.from(context))
    private var currentFavoriteState = isFavorite

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
        this.setCancelable(false)

        binding.tvProdukName.text = data.title
        binding.tvPrice.text = "$${data.price}"
        binding.tvRating.text = "${data.rating.rate}/5.0 (${data.rating.count})"
        binding.tvDescription.text = data.description

        binding.ivProduk.load(data.image) {
            transformations(RoundedCornersTransformation(20f))
            error(R.drawable.ic_broken_image)
            crossfade(false)
        }

        // Set initial favorite icon
        updateFavoriteIcon(currentFavoriteState)

        // Handle favorite click
        binding.ivFavorite.setOnClickListener {
            currentFavoriteState = !currentFavoriteState
            updateFavoriteIcon(currentFavoriteState)
            onFavoriteClick?.invoke(data)
        }

        binding.ivClose.setOnClickListener {
            onSelectedString.onItemSelected("done")
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    fun updateFavoriteStatus(isFavorite: Boolean) {
        currentFavoriteState = isFavorite
        updateFavoriteIcon(isFavorite)
    }

    interface OnSelectedString {
        fun onItemSelected(data: String)
    }

    fun setOnclick(onClickItem: OnSelectedString) {
        this.onSelectedString = onClickItem
    }
}