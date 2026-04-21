package my.test_gramedia.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import my.test_gramedia.R
import my.test_gramedia.databinding.ItemDashboardBinding
import my.test_gramedia.model.response.DataModel

class DashboardAdapter(
    private val onClicked: (DataModel) -> Unit,
    private val onFavoriteClicked: (DataModel) -> Unit
): RecyclerView.Adapter<DashboardAdapter.ViewHolder>() {

    var listData: MutableList<DataModel> = ArrayList()
    private var favoriteStatuses: Map<Int, Boolean> = emptyMap()

    fun clear() {
        if (listData.isNotEmpty()) {
            listData.clear()
            notifyDataSetChanged()
        }
    }

    fun insertAll(data: List<DataModel>) {
        data.forEach {
            listData.add(it)
            notifyItemInserted(listData.size - 1)
        }
    }

    fun updateFavoriteStatuses(statuses: Map<Int, Boolean>) {
        favoriteStatuses = statuses
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDashboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData[position]
        val isFavorite = favoriteStatuses[item.id] ?: false
        holder.bindTo(item, isFavorite)
    }

    override fun getItemCount()= listData.size

    inner class ViewHolder(val binding: ItemDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindTo(item : DataModel, isFavorite: Boolean) {

            binding.tvProdukName.text = item.title
            binding.tvPrice.text = "$${item.price}"
            binding.tvRating.text = "${item.rating.rate}/5.0 (${item.rating.count})"

            binding.ivProduk.load(item.image) {
                transformations(RoundedCornersTransformation(20f))
                error(R.drawable.ic_broken_image)
                crossfade(false)
            }

            // Update favorite icon based on status
            if (isFavorite) {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_border)
            }

            binding.ivFavorite.setOnClickListener {
                onFavoriteClicked(item)
            }

            binding.cardItem.setOnClickListener {
                onClicked(item)
            }
        }
    }

}