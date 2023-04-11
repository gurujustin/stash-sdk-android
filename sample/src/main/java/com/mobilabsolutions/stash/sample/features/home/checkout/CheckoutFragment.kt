/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.data.resultentities.CartWithProduct
import com.mobilabsolutions.stash.sample.databinding.FragmentCheckoutBinding
import com.mobilabsolutions.stash.sample.shared.BaseFragment
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
class CheckoutFragment : BaseFragment() {
    @Inject
    lateinit var checkoutViewModelFactory: CheckoutViewModel.Factory

    private val viewModel: CheckoutViewModel by fragmentViewModel()
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var controller: CheckoutEpoxyController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = CheckoutEpoxyController(object : CheckoutEpoxyController.Callbacks {
            override fun onAddButtonClicked(cartWithProduct: CartWithProduct) {
                viewModel.onAddButtonClicked(cartWithProduct)
            }

            override fun onRemoveButtonClicked(cartWithProduct: CartWithProduct) {
                viewModel.onRemoveButtonClicked(cartWithProduct)
            }
        })
        binding.checkoutRv.setController(controller)
        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(
                R.id.activity_payment,
                bundleOf("pay_amount" to binding.state?.totalAmount)
            )
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            binding.state = it
            binding.btnCheckout.isVisible = !it.showEmptyView
            binding.labelTotalAmount.isVisible = !it.showEmptyView
            binding.totalPriceText.isVisible = !it.showEmptyView
            controller.setData(it)
            if (it.paymentCompleted) {
                Snackbar
                    .make(binding.root, R.string.payment_success, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_ok) { /* Nothing to do for dismissal */ }
                    .show()
                viewModel.closeSnackBar()
            }
        }
    }
}