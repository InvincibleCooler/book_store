package me.invin.bookstore.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import me.invin.bookstore.R
import me.invin.bookstore.constants.Constants
import me.invin.bookstore.databinding.FragmentDetailBinding
import me.invin.bookstore.model.Resource
import me.invin.bookstore.net.res.BookRes
import me.invin.bookstore.viewmodel.DetailViewModel


class DetailFragment : Fragment() {
    companion object {
        private const val TAG = "DetailFragment"
    }

    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModel.Factory()
    }

    private var isbn13 = ""
    private var res: BookRes? = null

    // view bind
    private var _binding: FragmentDetailBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inState = savedInstanceState ?: arguments
        isbn13 = inState?.getString(Constants.Extra.ISBN_13) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel.bookResponse.observe(viewLifecycleOwner, Observer {
            if (it.status == Resource.Status.SUCCESS) {
                if (it.data != null) {
                    val res = it.data
                    updateUi(res)
                }
            } else {
                Log.d(TAG, "Error occurred : ${it.getErrorMessage()}")
            }
        })
        viewModel.showProgress.observe(viewLifecycleOwner, Observer {
            showProgressBar(it)
        })
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (res == null) {
            viewModel.getBook(isbn13)
        }
    }

    private fun updateUi(res: BookRes) {
        activity?.let {
            Glide.with(it).load(res.image).into(binding.ivThumb)
        }
        binding.tvTitle.text = res.title
        binding.tvSubTitle.text = res.subtitle
        binding.tvDesc.text = res.desc
        binding.tvPrice.text = String.format(getString(R.string.price), res.price)
        binding.tvRating.text = String.format(getString(R.string.rating), res.rating)
        binding.tvAuthors.text = String.format(getString(R.string.authors), res.authors)
        binding.tvPublisher.text = String.format(getString(R.string.publisher), res.publisher)
        binding.tvPublished.text = String.format(getString(R.string.published), res.year)
        binding.tvPages.text = String.format(getString(R.string.pages), res.pages)
        binding.tvLanguage.text = String.format(getString(R.string.language), res.language)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgressBar(isShow: Boolean) {
        binding.loadingProgressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}