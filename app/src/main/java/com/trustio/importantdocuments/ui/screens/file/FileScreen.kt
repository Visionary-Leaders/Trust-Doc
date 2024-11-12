package com.trustio.importantdocuments.ui.screens.file

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.trustio.importantdocuments.databinding.FileListScreenBinding
import com.trustio.importantdocuments.utils.BaseFragment
import com.trustio.importantdocuments.utils.convertBytesToMb
import com.trustio.importantdocuments.utils.gone
import com.trustio.importantdocuments.utils.invisible
import com.trustio.importantdocuments.utils.showNetworkDialog
import com.trustio.importantdocuments.utils.visible
import com.trustio.importantdocuments.viewmodel.imp.HomeScreenViewModelImp
import dagger.hilt.android.AndroidEntryPoint

@Suppress("CAST_NEVER_SUCCEEDS")
@AndroidEntryPoint
class FileScreen : BaseFragment<FileListScreenBinding>(FileListScreenBinding::inflate) {
    private val model by viewModels<HomeScreenViewModelImp>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.fileList.observe(this) {
            val sectionData = arguments?.getString("sectionTitle")

            val allFileSize = ArrayList<Long>()
            it.let {
                it.onEach {
                    allFileSize.add(it.file_size.toLong())
                }
            }
            binding.infoTxt.text =
                "${allFileSize.size} Items - %.2f MB".format(allFileSize.convertBytesToMb())
            binding.breadcrumbText.text = "$sectionData > Files"
            binding.fileListRv.adapter = FileListAdapter().apply {
                submitList(it ?: arrayListOf())

                // RefreshData
                binding.refreshLayout.isRefreshing = false
                binding.shimmerLayout.hideShimmer()
                binding.fileListRv.visible()
                binding.progressBar.invisible()
            }
        }
        model.noInternetLiveData.observe(this) {
            showNetworkDialog(requireActivity(),binding.root)
            binding.fileListRv.visible()
            binding.progressBar.gone()
        }
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        val sectionData = arguments?.getInt("sectionId")?:0
        model.loadFileBySection(sectionId = sectionData)
        binding.fileListRv.invisible()
        loadView()
        setupRefresh()
    }

    private fun setupRefresh(){
        val sectionData = arguments?.getInt("sectionId")?:0
        binding.refreshLayout.setSlingshotDistance( 128)
        binding.refreshLayout.setProgressViewEndTarget(false,  128)
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing=true
            model.loadFileBySection(sectionData)
            binding.shimmerLayout.showShimmer(true)
        }
    }
    private fun loadView(){
       binding. fileListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                    binding.addFileBtn.hide()
                else
                    binding.addFileBtn.show()

            }
        })
    }
}