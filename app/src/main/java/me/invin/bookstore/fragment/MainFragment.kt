package me.invin.bookstore.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.invin.bookstore.R
import me.invin.bookstore.constants.Constants
import me.invin.bookstore.databinding.FragmentMainBinding
import me.invin.bookstore.databinding.ItemlistBookStoreBinding
import me.invin.bookstore.model.Resource
import me.invin.bookstore.net.res.BookInfo
import me.invin.bookstore.utils.DialogUtils
import me.invin.bookstore.utils.InputMethodUtils
import me.invin.bookstore.viewmodel.MainViewModel


class MainFragment : Fragment() {
    companion object {
        private const val TAG = "MainFragment"

        private const val SIZE_IN_PAGE = 10
        private const val DEFAULT_PAGE = "1"

        private const val LOAD_DEFAULT = "LOAD_DEFAULT"
        private const val LOAD_MORE = "LOAD_MORE"

        private const val OPERATOR_OR = "|"
        private const val OPERATOR_NOT = "-"
    }

    private lateinit var localAdapter: LocalAdapter
    private val viewModel by viewModels<MainViewModel> {
        MainViewModel.Factory()
    }

    private var currentPage = "1"
    private var total = "0"

    // view bind
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localAdapter = LocalAdapter(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.searchResponse.observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    val res = it.data
                    if (res.total?.toIntOrNull() ?: 0 == 0) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.tvEmpty.visibility = View.GONE
                        total = res.total ?: "0"
                        currentPage = res.page ?: DEFAULT_PAGE
                        if (res.books.isNullOrEmpty().not()) {
                            val keyword = binding.etSearch.text.toString().trim()
                            if (keyword.contains(OPERATOR_NOT)) {
                                val excludedString = keyword.split(OPERATOR_NOT)[1].trim()
                                if (excludedString.isNotEmpty()) {
                                    val newList = res.books?.filter { info ->
                                        info.title?.contains(excludedString, ignoreCase = true)?.not() == true
                                    }
                                    localAdapter.items = newList as MutableList<BookInfo>
                                    localAdapter.notifyDataSetChanged()
                                }
                            } else {
                                localAdapter.items = res.books as MutableList<BookInfo>
                                localAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Error occurred : ${it.getErrorMessage()}")
            }
        })
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            showProgressBar(it)
        })
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    requestServer()
                    return true
                }
                return false
            }
        })
        binding.tvSearch.setOnClickListener {
            requestServer()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = localAdapter
            setHasFixedSize(true)
            requestFocus()
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!viewModel.isRequesting) {
                        val lastVisiblePos = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        val itemCount = localAdapter.itemCount

                        val total = total.toIntOrNull() ?: 0
                        var currentPage = currentPage.toIntOrNull() ?: 0

                        if (total > (currentPage * SIZE_IN_PAGE) && lastVisiblePos >= itemCount - 1) {
                            val keyword = binding.etSearch.text.toString().trim()
                            if (keyword.isNotEmpty()) {
                                currentPage++
                                loadData(keyword = keyword, page = currentPage.toString(), type = LOAD_MORE)
                            }
                        }
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData(keyword: String, page: String = DEFAULT_PAGE, type: String) {
        if (type == LOAD_DEFAULT) {
            localAdapter.clear()
            viewModel.getSearch(keyword)
        } else {
            viewModel.getSearchPartial(keyword, page)
        }
    }

    private fun requestServer() {
        currentPage = "1"
        total = "0"

        InputMethodUtils.hideInputMethod(context, binding.etSearch)

        val keyword = binding.etSearch.text.toString().trim()
        if (keyword.isNotEmpty()) {
            if (!isValidKeyword(keyword)) {
                activity?.let {
                    DialogUtils.showAlertPopup(it)
                }
                return
            }
            loadData(keyword = keyword, type = LOAD_DEFAULT)
        } else {
            context?.let {
                Toast.makeText(it, getString(R.string.input_keyword), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidKeyword(keyword: String): Boolean {
        if (keyword.contains(OPERATOR_OR) && keyword.contains(OPERATOR_NOT)) {
            return false
        }
        if (keyword.contains(OPERATOR_OR)) {
            if (keyword.split(OPERATOR_OR).size > 2) {
                return false
            }
        } else if (keyword.contains(OPERATOR_NOT)) {
            if (keyword.split(OPERATOR_NOT).size > 2) {
                return false
            }
        }
        return true
    }

    private fun showProgressBar(isShow: Boolean) {
        binding.loadingProgressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private inner class LocalAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val viewTypeItem = 1

        var items = mutableListOf<BookInfo>()
            set(value) {
                field.addAll(value)
            }

        fun clear() {
            items.clear()
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun getItemViewType(position: Int): Int {
            return viewTypeItem
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ItemViewHolder(ItemlistBookStoreBinding.inflate(LayoutInflater.from(activity), parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                viewTypeItem -> {
                    val vh = holder as ItemViewHolder
                    val data = items[position]

                    Glide.with(activity).load(data.image).into(vh.binding.ivThumb)
                    vh.binding.tvTitle.text = data.title
                    vh.binding.tvSubTitle.text = data.subtitle
                    vh.binding.tvPrice.text = data.price

                    vh.binding.root.setOnClickListener {
                        val bundle = Bundle().apply {
                            putString(Constants.Extra.ISBN_13, data.isbn13)
                        }
                        val navOption = NavOptions.Builder().setLaunchSingleTop(true).build()
                        findNavController().navigate(R.id.fragment_detail, bundle, navOption)
                    }
                }
            }
        }

        private open inner class ItemViewHolder(_binding: ItemlistBookStoreBinding) : RecyclerView.ViewHolder(_binding.root) {
            val binding = _binding
        }
    }
}