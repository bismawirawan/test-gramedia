package my.test_gramedia.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import my.test_gramedia.R
import my.test_gramedia.common.states.UiState
import my.test_gramedia.databinding.ActivityDashboardBinding
import my.test_gramedia.model.response.DataModel
import my.test_gramedia.module.BaseActivity
import my.test_gramedia.network.connection.NetworkConnectionLiveData
import my.test_gramedia.ui.adapters.DashboardAdapter
import my.test_gramedia.ui.dialogs.DialogDetail
import my.test_gramedia.viewmodel.DataViewModel
import java.util.ArrayList
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class Dashboard : BaseActivity() {

    lateinit var binding: ActivityDashboardBinding

    private val viewModel by viewModels<DataViewModel>()

    private var search = ""

    private var dataModel: MutableList<DataModel> = ArrayList()
    private var searchData = ArrayList<DataModel>()

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        DashboardAdapter(
            onClicked = ::onClicked,
            onFavoriteClicked = ::onFavoriteClicked
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        observe(viewModel.dataState) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.layoutLoading.loadingLayout.visibility = View.VISIBLE
                    binding.rvData.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.layoutLoading.loadingLayout.visibility = View.GONE
                    binding.rvData.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    binding.layoutLoading.loadingLayout.visibility = View.GONE
                    binding.rvData.visibility = View.VISIBLE
                    showToast(state.message)
                }
                else -> {}
            }
        }

        observe(viewModel.dataResponse) {
            if (it != null) {
                dataModel.clear()
                dataModel.addAll(it)
                adapter.clear()
                adapter.insertAll(dataModel)
            }
        }

        observe(viewModel.favoriteStatus) { statusMap ->
            statusMap?.let { adapter.updateFavoriteStatuses(it) }
        }

        initView()
    }

    private fun initView() {

        binding.rvData.let {
            it.adapter = adapter
            it.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }

        binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                search = s.toString()
                if (search.isNotEmpty()) {
                    binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_search_gray,
                        0,
                        R.drawable.ic_close,
                        0
                    )
                } else {
                    binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                searchingData()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.etSearch.setOnTouchListener(View.OnTouchListener { v, event ->
            binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_search_gray,
                0,
                R.drawable.ic_close,
                0
            )
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.x >= binding.etSearch.width - binding.etSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                ) {
                    binding.etSearch.text?.clear()
                    binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    // your action here
                    return@OnTouchListener true
                }
            }
            false
        })

        val networkconnection = NetworkConnectionLiveData(this)
        observeNonNull(networkconnection) { isConnected ->
            try {
                viewModel.checkDataWithConnection(isConnected)
            } catch (error: Exception) {
                error.printStackTrace()
            }

        }

    }

    private fun searchingData() {
        val text = search.lowercase(Locale.getDefault())

        try {
            searchData = dataModel.filter {
                it.title.lowercase(Locale.getDefault()).contains(text)
            } as ArrayList<DataModel>
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (search.isNotEmpty()) {
            try {
                adapter.clear()
                adapter.insertAll(searchData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (searchData.isEmpty()) {
                binding.rvData.visibility = View.GONE
            } else {
                binding.rvData.visibility = View.VISIBLE
            }
        } else {
            try {
                adapter.clear()
                adapter.insertAll(dataModel)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            checkEmptyData()
        }
    }

    private fun checkEmptyData() {
        if (dataModel.isEmpty()) {
            binding.rvData.visibility = View.GONE
        } else {
            binding.rvData.visibility = View.VISIBLE
        }
    }

    private fun onClicked(data: DataModel) {
        val isFavorite = viewModel.favoriteStatus.value?.get(data.id) ?: false

        val dialog = DialogDetail(
            context = this,
            data = data,
            isFavorite = isFavorite,
            onFavoriteClick = { product ->
                viewModel.toggleFavorite(product.id)
            }
        )
        dialog.show()
        dialog.setOnclick(object : DialogDetail.OnSelectedString {
            override fun onItemSelected(data: String) {
                dialog.dismiss()
            }
        })

        observe(viewModel.favoriteStatus) { statusMap ->
            statusMap?.get(data.id)?.let { isFav ->
                dialog.updateFavoriteStatus(isFav)
            }
        }
    }

    private fun onFavoriteClicked(data: DataModel) {
        viewModel.toggleFavorite(data.id)
    }
}