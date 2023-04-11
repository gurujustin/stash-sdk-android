/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.uicomponents

/* ktlint-disable no-wildcard-imports */
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.stash.bspayone.BsPayoneIntegration
import com.mobilabsolutions.stash.bspayone.R
import com.mobilabsolutions.stash.core.CustomizationExtensions
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.uicomponents.Country
import com.mobilabsolutions.stash.core.internal.uicomponents.CountryChooserActivity
import com.mobilabsolutions.stash.core.internal.uicomponents.IbanTextWatcher
import com.mobilabsolutions.stash.core.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.SepaDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.SnackBarExtensions
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.internal.uicomponents.ValidationResult
import com.mobilabsolutions.stash.core.internal.uicomponents.getContentOnFocusChange
import com.mobilabsolutions.stash.core.internal.uicomponents.getIbanStringUnformatted
import com.mobilabsolutions.stash.core.internal.uicomponents.observeText
import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.SepaData
import com.mobilabsolutions.stash.core.util.CountryDetectorUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.sepa_data_entry_fragment.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneSepaDataEntryFragment : Fragment() {

    companion object {
        private const val COUNTRY_REQUEST_CODE = 1
    }

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    lateinit var stashUIConfiguration: StashUiConfiguration

    @Inject
    lateinit var sepaDataValidator: SepaDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    private val disposables = CompositeDisposable()

    private val firstNameFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val firstNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val lastNameFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val ibanFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ibanTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var viewState: SepaDataEntryViewState? = null

    private lateinit var suggestedCountry: Locale

    private var waitTimer: CountDownTimer? = null

    private var currentSnackbar: Snackbar? = null

    private lateinit var selectedCountry: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        suggestedCountry = CountryDetectorUtil.getBestGuessAtCurrentCountry(requireContext())
        selectedCountry = Country(suggestedCountry.displayName, suggestedCountry.country, suggestedCountry.isO3Country)
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sepa_data_entry_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stashUIConfiguration = uiCustomizationManager.getCustomizationPreferences()

        CustomizationExtensions {
            ibanTitleTextView.applyTextCustomization(stashUIConfiguration)
            firstNameTitleTextView.applyTextCustomization(stashUIConfiguration)
            lastNameTitleTextView.applyTextCustomization(stashUIConfiguration)
            sepaScreenTitle.applyTextCustomization(stashUIConfiguration)
            countryTitleTextView.applyTextCustomization(stashUIConfiguration)

            firstNameEditText.applyEditTextCustomization(stashUIConfiguration)
            lastNameEditText.applyEditTextCustomization(stashUIConfiguration)
            countryText.applyFakeEditTextCustomization(stashUIConfiguration)
            ibanNumberEditText.applyEditTextCustomization(stashUIConfiguration)
            saveButton.applyCustomization(stashUIConfiguration)
            sepaScreenMainLayout.applyBackgroundCustomization(stashUIConfiguration)
            sepaScreenCellLayout.applyCellBackgroundCustomization(stashUIConfiguration)

            firstNameEditText.showKeyboardAndFocus()
        }

        disposables += Observables.combineLatest(
                firstNameTextChangedSubject,
                lastNameTextChangedSubject,
                ibanTextChangedSubject,
                countrySubject,
                BsPayoneSepaDataEntryFragment::SepaDataEntryViewState)
                .subscribe(this::onViewStateNext)

        disposables += firstNameFocusSubject
                .doOnNext {
                    validateFirstNameAndUpdateUI(it, false)
                }
                .subscribe()

        disposables += firstNameTextChangedSubject
                .doOnNext {
                    validateFirstNameAndUpdateUI(it, true)
                }
                .subscribe()

        disposables += lastNameFocusSubject
                .doOnNext {
                    validateLastNameAndUpdateUI(it, false)
                }
                .subscribe()

        disposables += lastNameTextChangedSubject
                .doOnNext {
                    validateLastNameAndUpdateUI(it, true)
                }
                .subscribe()

        disposables += ibanFocusSubject
                .doOnNext {
                    validateIbanAndUpdateUI(it, false)
                }
                .subscribe()

        disposables += ibanTextChangedSubject
                .doOnNext {
                    validateIbanAndUpdateUI(it, true)
                }
                .subscribe()

        firstNameEditText.getContentOnFocusChange { isFocusGained, value -> if (!isFocusGained) firstNameFocusSubject.onNext(value.trim()) }
        firstNameEditText.observeText { firstNameTextChangedSubject.onNext(it.trim()) }

        lastNameEditText.getContentOnFocusChange { isFocusGained, value -> if (!isFocusGained) lastNameFocusSubject.onNext(value.trim()) }
        lastNameEditText.observeText { lastNameTextChangedSubject.onNext(it.trim()) }

        ibanNumberEditText.getContentOnFocusChange { isFocusGained, value ->
            if (!isFocusGained) {
                ibanFocusSubject.onNext(value.getIbanStringUnformatted().trim())
            } else {
                firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
                lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
            }
        }
        ibanNumberEditText.observeText { ibanTextChangedSubject.onNext(it.getIbanStringUnformatted().trim()) }

        ibanNumberEditText.addTextChangedListener(IbanTextWatcher())

        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.setOnClickListener {
            startActivityForResult(Intent(context, CountryChooserActivity::class.java)
                    .putExtra(CountryChooserActivity.CURRENT_LOCATION_ENABLE_EXTRA, true)
                    .putExtra(CountryChooserActivity.CURRENT_LOCATION_CUSTOM_EXTRA, suggestedCountry.country), COUNTRY_REQUEST_CODE)

            // Check for the previous field's validations
            firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
            lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
            ibanFocusSubject.onNext(ibanNumberEditText.text.toString().getIbanStringUnformatted())
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[BillingData.ADDITIONAL_DATA_FIRST_NAME] = it.firstName
                dataMap[BillingData.ADDITIONAL_DATA_LAST_NAME] = it.lastName
                dataMap[SepaData.IBAN] = it.iban
                dataMap[BillingData.ADDITIONAL_DATA_COUNTRY] = selectedCountry.alpha2Code
                uiComponentHandler.submitData(dataMap)
            }
        }

        countryText.text = suggestedCountry.displayCountry

        bsPayoneSepaEntrySwipeRefresh.isEnabled = false

        disposables += uiComponentHandler.getResultObservable().subscribe {
            when (it) {
                is UiRequestHandler.DataEntryResult.Success -> {
                    bsPayoneSepaEntrySwipeRefresh.isRefreshing = false
                }
                is UiRequestHandler.DataEntryResult.Processing -> {
                    bsPayoneSepaEntrySwipeRefresh.isRefreshing = true
                }
                is UiRequestHandler.DataEntryResult.Failure -> {
                    SnackBarExtensions {
                        bsPayoneSepaEntrySwipeRefresh.isRefreshing = false
                        currentSnackbar?.dismissWithoutAnimating()
                        currentSnackbar = it.throwable.getErrorSnackBar(sepaScreenMainLayout, stashUIConfiguration)
                        currentSnackbar?.show()
                    }
                }
            }
        }

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    private fun onViewStateNext(state: SepaDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateName(state.firstName).success && success
        success = validateName(state.lastName).success && success
        success = validateIban(state.iban).success && success
        success = validateCountry(state.country).success && success
        saveButton.isEnabled = success
        CustomizationExtensions {
            saveButton.applyCustomization(stashUIConfiguration)
        }
    }

    private fun validateFirstNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(firstNameEditText, errorSepaFirstName, validationResult)
            } else {
                showError(firstNameEditText, errorSepaFirstName, validationResult)
            }
        } else {
            stopTimer()
            hideError(firstNameEditText, errorSepaFirstName)
        }
        return validationResult.success
    }

    private fun validateLastNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(lastNameEditText, errorSepaLastName, validationResult)
            } else {
                showError(lastNameEditText, errorSepaLastName, validationResult)
            }
        } else {
            stopTimer()
            hideError(lastNameEditText, errorSepaLastName)
        }
        return validationResult.success
    }

    private fun validateName(name: String): ValidationResult {
        return personalDataValidator.validateName(name)
    }

    private fun validateIbanAndUpdateUI(iban: String, isDelayed: Boolean): Boolean {
        val validationResult = validateIban(iban)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(ibanNumberEditText, errorIban, validationResult)
            } else {
                showError(ibanNumberEditText, errorIban, validationResult)
            }
        } else {
            stopTimer()
            hideError(ibanNumberEditText, errorIban)
        }
        return validationResult.success
    }

    private fun validateIban(iban: String): ValidationResult {
        return sepaDataValidator.validateIban(iban)
    }

    private fun validateCountry(country: String): ValidationResult {
        return ValidationResult(success = country.isNotEmpty())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == COUNTRY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Country>(CountryChooserActivity.SELECTED_COUNTRY)?.let {
                    selectedCountry = it
                    countryText.text = it.displayName
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(sourceView: View, errorView: TextView, validationResult: ValidationResult) {
        waitTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Do Nothing
            }

            override fun onFinish() {
                showError(sourceView, errorView, validationResult)
            }
        }.start()
    }

    private fun showError(sourceView: View, errorView: TextView, validationResult: ValidationResult) {
        errorView.setText(validationResult.errorMessageResourceId)
        errorView.visibility = View.VISIBLE
        sourceView.setBackgroundResource(R.drawable.edit_text_frame_error)
    }

    private fun hideError(sourceView: View, errorView: TextView) {
        CustomizationExtensions {
            (sourceView as EditText).applyEditTextCustomization(stashUIConfiguration)
        }
        errorView.visibility = View.GONE
    }

    private fun stopTimer() {
        waitTimer?.cancel()
        waitTimer = null
    }

    data class SepaDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val iban: String = "",
        val country: String = ""
    )
}