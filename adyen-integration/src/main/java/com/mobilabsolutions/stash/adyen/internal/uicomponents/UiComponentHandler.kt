/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen.internal.uicomponents

import androidx.appcompat.app.AppCompatActivity
import com.mobilabsolutions.stash.adyen.R
import com.mobilabsolutions.stash.core.internal.IntegrationScope
import com.mobilabsolutions.stash.core.internal.psphandler.AdditionalRegistrationData
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
@IntegrationScope
class UiComponentHandler @Inject constructor() {

    private var dataSubject: PublishSubject<AdditionalRegistrationData> = PublishSubject.create()

    private var resultObservable: Observable<UiRequestHandler.DataEntryResult>? = null

    fun submitData(data: Map<String, String>) {
        dataSubject.onNext(AdditionalRegistrationData(data))
    }

    fun getResultObservable(): Observable<UiRequestHandler.DataEntryResult>? {
        return resultObservable
    }

    fun handleSepaDataEntryRequest(activity: AppCompatActivity, resultObservable: Observable<UiRequestHandler.DataEntryResult>): Observable<AdditionalRegistrationData> {
        dataSubject = PublishSubject.create()
        val sepaDataEntryFragment = AdyenSepaDataEntryFragment()
        this.resultObservable = resultObservable
        activity.supportFragmentManager.beginTransaction().replace(R.id.host_activity_fragment, sepaDataEntryFragment).commitNow()
        return dataSubject
    }

    fun handleCreditCardDataEntryRequest(
        activity: AppCompatActivity,
        resultObservable: Observable<UiRequestHandler.DataEntryResult>
    ): Observable<AdditionalRegistrationData> {
        dataSubject = PublishSubject.create()
        val creditCardDataEntryFragment = AdyenCreditCardDataEntryFragment()
        this.resultObservable = resultObservable
        activity.supportFragmentManager.beginTransaction().replace(R.id.host_activity_fragment, creditCardDataEntryFragment).commitNow()
        return dataSubject
    }
}