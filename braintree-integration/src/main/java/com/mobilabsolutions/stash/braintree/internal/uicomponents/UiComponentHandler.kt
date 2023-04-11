/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.braintree.internal.uicomponents

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.braintree.R
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

@IntegrationScope
class UiComponentHandler @Inject constructor() {

    private var dataSubject: PublishSubject<AdditionalRegistrationData> = PublishSubject.create()

    private var resultObservable: Observable<UiRequestHandler.DataEntryResult>? = null

    fun submitData(data: Map<String, String>) {
        dataSubject.onNext(AdditionalRegistrationData(data))
    }

    fun getResultObservable(): Observable<UiRequestHandler.DataEntryResult> {
        return resultObservable!!
    }

    fun handleCreditCardDataEntryRequest(
        activity: AppCompatActivity,
        additionalRegistrationData: AdditionalRegistrationData,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {
        dataSubject = PublishSubject.create()
        val creditCardDataEntryFragment = BraintreeCreditCardDataEntryFragment()
        this.resultObservable = resultObservable.doOnNext {
            if (it is UiRequestHandler.DataEntryResult.Success) {
                activity.supportFragmentManager.beginTransaction().remove(creditCardDataEntryFragment).commitNow()
            }
        }
        activity
            .supportFragmentManager
            .beginTransaction()
            .add(R.id.host_activity_fragment, creditCardDataEntryFragment)
            .commitNow()
        return dataSubject
    }
}