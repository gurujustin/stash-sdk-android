/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.stash.core.PaymentMethodType
import com.mobilabsolutions.stash.core.R
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.StashImpl
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_payment_method_chooser.*
import kotlinx.android.synthetic.main.view_holder_payment_method.view.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class PaymentMethodChoiceFragment : Fragment() {

    @Inject
    lateinit var uiRequestHandler: UiRequestHandler

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        StashImpl.getInjector().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_method_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.deep_dark_blue)

        val stashUIConfiguration = uiCustomizationManager.getCustomizationPreferences()
        // CustomizationExtensions {
        //     rootView.applyBackgroundCustomization(stashUIConfiguration)
        //     titleTextView.applyTextCustomization(stashUIConfiguration)
        //     explanationTextView.applyTextCustomization(stashUIConfiguration)
        // }
        val paymentMethodAdapter = PaymentMethodAdapter(
            uiRequestHandler.availablePaymentMethods(),
            uiRequestHandler.paymentMethodTypeSubject,
            stashUIConfiguration
        )
        payment_method_chooser_rv.adapter = paymentMethodAdapter

        back_arrow.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!uiRequestHandler.paymentMethodTypeSubject.hasValue()) {
            uiRequestHandler.paymentMethodTypeSubject.onError(UiRequestHandler.UserCancelled())
        }
    }

    inner class PaymentMethodAdapter(
        private val availablePaymentMethods: List<PaymentMethodType>,
        private val paymentMethodSubject: ReplaySubject<PaymentMethodType>,
        private val stashUIConfiguration: StashUiConfiguration
    ) : RecyclerView.Adapter<PaymentMethodViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodViewHolder {
            return PaymentMethodViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_holder_payment_method, parent, false))
        }

        override fun getItemCount(): Int {
            return availablePaymentMethods.size
        }

        override fun onBindViewHolder(holder: PaymentMethodViewHolder, position: Int) {
            val paymentMethodType = availablePaymentMethods[position]
            holder.paymentMethodName.setText(
                when (paymentMethodType) {
                    PaymentMethodType.CC -> R.string.payment_chooser_credit_card
                    PaymentMethodType.SEPA -> R.string.payment_chooser_sepa
                    PaymentMethodType.PAYPAL -> R.string.payment_chooser_paypal
                }
            )
            holder.paymentMethodIcon.setImageResource(
                when (paymentMethodType) {
                    PaymentMethodType.CC -> R.drawable.ic_credit_card
                    PaymentMethodType.SEPA -> R.drawable.ic_sepa
                    PaymentMethodType.PAYPAL -> R.drawable.ic_paypal
                }
            )
            holder.itemView.setOnClickListener {
                paymentMethodSubject.onNext(paymentMethodType)
            }
            // CustomizationExtensions {
            //     holder.paymentMethodName.applyTextCustomization(stashUIConfiguration)
            //     holder.itemView.applyCellBackgroundCustomization(stashUIConfiguration)
            // }
        }
    }

    inner class PaymentMethodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentMethodName: TextView = view.payment_method_name
        val paymentMethodIcon: ImageView = view.payment_type_ic
    }
}
