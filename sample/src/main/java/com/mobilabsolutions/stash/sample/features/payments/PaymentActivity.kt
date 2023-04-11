/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.navArgs
import com.mobilabsolutions.stash.sample.R
import com.mobilabsolutions.stash.sample.shared.BaseActivity
import com.mobilabsolutions.stash.sample.databinding.ActivityPaymentBinding
import com.mobilabsolutions.stash.sample.features.payments.selectpayment.SelectPaymentFragment

class PaymentActivity : BaseActivity() {

    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)

        val args: PaymentActivityArgs by navArgs()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.select_payment_container, SelectPaymentFragment.create(args.payAmount))
            .commit()
    }
}
