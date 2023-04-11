/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.items

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.data.entities.Product
import com.mobilabsolutions.stash.sample.databinding.FragmentItemsBinding
import com.mobilabsolutions.stash.sample.shared.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */

class ItemsFragment : BaseFragment() {
    @Inject
    lateinit var itemsViewModelFactory: ItemsViewModel.Factory

    private val viewModel: ItemsViewModel by fragmentViewModel()
    private lateinit var binding: FragmentItemsBinding
    private lateinit var controller: ItemsEpoxyController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = ItemsEpoxyController(object : ItemsEpoxyController.Callbacks {
            override fun onProductClicked(product: Product) {
                viewModel.onProductClicked(product)
                view.let { Snackbar.make(it, getString(R.string.message, product.name), Snackbar.LENGTH_SHORT).show() }
            }
        })
        binding.itemsRecyclerView.setController(controller)
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            controller.setData(it)
        }
    }
}