/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.bspayone.internal.uicomponents

/* ktlint-disable no-wildcard-imports */
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mobilabsolutions.stash.bspayone.BsPayoneIntegration
import com.mobilabsolutions.stash.bspayone.R
import com.mobilabsolutions.stash.core.CustomizationExtensions
import com.mobilabsolutions.stash.core.CustomizationExtensions.applyEditTextCustomization
import com.mobilabsolutions.stash.core.CustomizationExtensions.applyFakeEditTextCustomization
import com.mobilabsolutions.stash.core.StashUiConfiguration
import com.mobilabsolutions.stash.core.UiCustomizationManager
import com.mobilabsolutions.stash.core.internal.uicomponents.CardNumberTextWatcher
import com.mobilabsolutions.stash.core.internal.uicomponents.Country
import com.mobilabsolutions.stash.core.internal.uicomponents.CountryChooserActivity
import com.mobilabsolutions.stash.core.internal.uicomponents.CreditCardDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.MonthYearPicker
import com.mobilabsolutions.stash.core.internal.uicomponents.PersonalDataValidator
import com.mobilabsolutions.stash.core.internal.uicomponents.SnackBarExtensions
import com.mobilabsolutions.stash.core.internal.uicomponents.UiRequestHandler
import com.mobilabsolutions.stash.core.internal.uicomponents.ValidationResult
import com.mobilabsolutions.stash.core.internal.uicomponents.getCardNumberStringUnformatted
import com.mobilabsolutions.stash.core.internal.uicomponents.getContentOnFocusChange
import com.mobilabsolutions.stash.core.internal.uicomponents.observeText
import com.mobilabsolutions.stash.core.model.BillingData
import com.mobilabsolutions.stash.core.model.CreditCardData
import com.mobilabsolutions.stash.core.util.CountryDetectorUtil
import com.mobilabsolutions.stash.core.util.showKeyboard
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.back
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.bsPayoneCreditCardEntrySwipeRefresh
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.countryText
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.countryTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.creditCardNumberEditText
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.creditCardNumberTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.creditCardScreenCellLayout
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.creditCardScreenMainLayout
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.creditCardScreenTitle
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.cvvEditText
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.cvvTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.errorCreditCardCVV
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.errorCreditCardExp
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.errorCreditCardFirstName
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.errorCreditCardLastName
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.errorCreditCardNumber
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.expirationDateTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.expirationDateTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.firstNameEditText
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.firstNameTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.lastNameEditText
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.lastNameTitleTextView
import kotlinx.android.synthetic.main.credit_card_data_entry_fragment.saveButton
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author <a href="ugi@mobilabsolutions.com">Ugi</a>
 */
class BsPayoneCreditCardDataEntryFragment : Fragment() {

    companion object {
        private const val COUNTRY_REQUEST_CODE = 1
    }

    @Inject
    lateinit var uiComponentHandler: UiComponentHandler

    @Inject
    lateinit var creditCardDataValidator: CreditCardDataValidator

    @Inject
    lateinit var personalDataValidator: PersonalDataValidator

    @Inject
    lateinit var uiCustomizationManager: UiCustomizationManager

    private lateinit var stashUIConfiguration: StashUiConfiguration

    private val disposables = CompositeDisposable()

    private val firstNameFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val firstNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val lastNameFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val lastNameTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val cardNumberFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val cardNumberTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val expirationDateSubject: BehaviorSubject<LocalDate> = BehaviorSubject.create()

    private val ccvFocusSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val ccvTextChangedSubject: BehaviorSubject<String> = BehaviorSubject.create()

    private val countrySubject: BehaviorSubject<String> = BehaviorSubject.create()

    private var viewState: CreditCardDataEntryViewState? = null

    private lateinit var suggestedCountry: Locale

    private var waitTimer: CountDownTimer? = null

    private var currentSnackbar: Snackbar? = null

    private var selectedExpiryDate: LocalDate? = null

    private var isKeyboardActionNext = false

    private lateinit var selectedCountry: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BsPayoneIntegration.integration?.bsPayoneIntegrationComponent?.inject(this)
        suggestedCountry = CountryDetectorUtil.getBestGuessAtCurrentCountry(requireContext())
        selectedCountry = Country(suggestedCountry.displayName, suggestedCountry.country, suggestedCountry.isO3Country)
        Timber.d("Created")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.credit_card_data_entry_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disposables += Observables.combineLatest(
            firstNameTextChangedSubject,
            lastNameTextChangedSubject,
            cardNumberTextChangedSubject,
            expirationDateSubject,
            ccvTextChangedSubject,
            countrySubject,
            BsPayoneCreditCardDataEntryFragment::CreditCardDataEntryViewState)
            .subscribe(this::onViewState)

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

        disposables += cardNumberFocusSubject
            .doOnNext {
                validateCardNumberAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += cardNumberTextChangedSubject
            .doOnNext {
                validateCardNumberAndUpdateUI(it, true)
            }
            .subscribe()

        disposables += expirationDateSubject
            .doOnNext {
                validateExpirationDateAndUpdateUI(it)
            }
            .subscribe()

        disposables += ccvFocusSubject
            .doOnNext {
                validateCvvAndUpdateUI(it, false)
            }
            .subscribe()

        disposables += ccvTextChangedSubject
            .doOnNext {
                validateCvvAndUpdateUI(it, true)
            }
            .subscribe()

        stashUIConfiguration = uiCustomizationManager.getCustomizationPreferences()

        CustomizationExtensions {

            creditCardScreenTitle.applyTextCustomization(stashUIConfiguration)
            firstNameTitleTextView.applyTextCustomization(stashUIConfiguration)
            lastNameTitleTextView.applyTextCustomization(stashUIConfiguration)
            creditCardNumberTitleTextView.applyTextCustomization(stashUIConfiguration)
            expirationDateTitleTextView.applyTextCustomization(stashUIConfiguration)
            countryTitleTextView.applyTextCustomization(stashUIConfiguration)
            cvvTitleTextView.applyTextCustomization(stashUIConfiguration)

            firstNameEditText.applyEditTextCustomization(stashUIConfiguration)
            lastNameEditText.applyEditTextCustomization(stashUIConfiguration)
            creditCardNumberEditText.applyEditTextCustomization(stashUIConfiguration)
            cvvEditText.applyEditTextCustomization(stashUIConfiguration)
            expirationDateTextView.applyFakeEditTextCustomization(stashUIConfiguration)
            countryText.applyFakeEditTextCustomization(stashUIConfiguration)
            creditCardScreenMainLayout.applyBackgroundCustomization(stashUIConfiguration)
            creditCardScreenCellLayout.applyCellBackgroundCustomization(stashUIConfiguration)
            saveButton.applyCustomization(stashUIConfiguration)
            firstNameEditText.showKeyboardAndFocus()
        }

        firstNameEditText.getContentOnFocusChange { isFocusGained, value ->
            if (!isFocusGained) firstNameFocusSubject.onNext(value.trim())
        }
        firstNameEditText.observeText { firstNameTextChangedSubject.onNext(it.trim()) }

        lastNameEditText.getContentOnFocusChange { isFocusGained, value ->
            if (!isFocusGained) lastNameFocusSubject.onNext(value.trim())
        }
        lastNameEditText.observeText { lastNameTextChangedSubject.onNext(it.trim()) }

        creditCardNumberEditText.getContentOnFocusChange { isFocusGained, value ->
            if (!isFocusGained) {
                cardNumberFocusSubject.onNext(value.getCardNumberStringUnformatted().trim())
            } else {
                firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
                lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
            }
        }
        creditCardNumberEditText.observeText { cardNumberTextChangedSubject.onNext(it.getCardNumberStringUnformatted().trim()) }

        creditCardNumberEditText.addTextChangedListener(CardNumberTextWatcher { resourceId ->
            creditCardNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, resourceId, 0)
        })

        creditCardNumberEditText.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_NEXT) {
                isKeyboardActionNext = true
                expirationDateTextView.performClick()
            }
            false
        }

        cvvEditText.getContentOnFocusChange { isFocusGained, value ->
            if (!isFocusGained) {
                ccvFocusSubject.onNext(value.trim())
            } else {
                firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
                lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
                cardNumberFocusSubject.onNext(creditCardNumberEditText.text.toString().getCardNumberStringUnformatted())
                if (!isKeyboardActionNext) {
                    expirationDateSubject.onNext(selectedExpiryDate ?: LocalDate.MIN)
                } else {
                    isKeyboardActionNext = false
                }
            }
        }
        cvvEditText.observeText { ccvTextChangedSubject.onNext(it.trim()) }

        countryText.onTextChanged { countrySubject.onNext(it.toString().trim()) }

        countryText.text = suggestedCountry.displayCountry

        countryText.setOnClickListener {
            startActivityForResult(Intent(context, CountryChooserActivity::class.java)
                .putExtra(CountryChooserActivity.CURRENT_LOCATION_ENABLE_EXTRA, true)
                .putExtra(CountryChooserActivity.CURRENT_LOCATION_CUSTOM_EXTRA, suggestedCountry.country), COUNTRY_REQUEST_CODE)

            // Check for the previous field's validations
            firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
            lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
            cardNumberFocusSubject.onNext(creditCardNumberEditText.text.toString().getCardNumberStringUnformatted())
            expirationDateSubject.onNext(selectedExpiryDate ?: LocalDate.MIN)
            ccvFocusSubject.onNext(cvvEditText.text.toString().trim())
        }

        saveButton.setOnClickListener {
            viewState?.let {
                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap[BillingData.ADDITIONAL_DATA_FIRST_NAME] = it.firstName
                dataMap[BillingData.ADDITIONAL_DATA_LAST_NAME] = it.lastName
                dataMap[BillingData.ADDITIONAL_DATA_COUNTRY] = selectedCountry.alpha2Code
                dataMap[CreditCardData.CREDIT_CARD_NUMBER] = it.cardNumber
                dataMap[CreditCardData.CVV] = it.cvv
                dataMap[CreditCardData.EXPIRY_MONTH] = (selectedExpiryDate?.monthValue
                    ?: throw RuntimeException("Month was null")).toString()
                dataMap[CreditCardData.EXPIRY_YEAR] = (selectedExpiryDate?.year
                    ?: throw RuntimeException("Year was null")).toString()
                uiComponentHandler.submitData(dataMap)
            }
        }

        expirationDateTextView.setOnClickListener {
            val monthYearPicker = MonthYearPicker(requireContext(),
                stashUIConfiguration = stashUIConfiguration,
                selectedDate = selectedExpiryDate,
                onCancelListener = DialogInterface.OnCancelListener {
                    expirationDateSubject.onNext(LocalDate.MIN)
                    cvvEditText.showKeyboard()
                }) {
                val selectedExpiryWithoutLastDay = LocalDate.of(it.second, it.first, 1)
                val lastDay = selectedExpiryWithoutLastDay.month.length(selectedExpiryWithoutLastDay.isLeapYear)
                val selectedExpiry = LocalDate.of(it.second, it.first, lastDay)
                selectedExpiryDate = selectedExpiry
                expirationDateSubject.onNext(selectedExpiry)
                val expDate = selectedExpiry.format(DateTimeFormatter.ofPattern("MM/yy"))
                expirationDateTextView.text = expDate
                cvvEditText.showKeyboard()
            }
            monthYearPicker.show()

            // Check for the previous field's validations
            firstNameFocusSubject.onNext(firstNameEditText.text.toString().trim())
            lastNameFocusSubject.onNext(lastNameEditText.text.toString().trim())
            cardNumberFocusSubject.onNext(creditCardNumberEditText.text.toString().getCardNumberStringUnformatted())
        }

        back.setOnClickListener {
            requireActivity().onBackPressed()
        }

        bsPayoneCreditCardEntrySwipeRefresh.isEnabled = false

        disposables += uiComponentHandler.getResultObservable().subscribe {
            when (it) {
                is UiRequestHandler.DataEntryResult.Success -> {
                    bsPayoneCreditCardEntrySwipeRefresh.isRefreshing = false
                }
                is UiRequestHandler.DataEntryResult.Processing -> {
                    bsPayoneCreditCardEntrySwipeRefresh.isRefreshing = true
                }
                is UiRequestHandler.DataEntryResult.Failure -> {
                    SnackBarExtensions {
                        bsPayoneCreditCardEntrySwipeRefresh.isRefreshing = false
                        currentSnackbar?.dismissWithoutAnimating()
                        currentSnackbar = it.throwable.getErrorSnackBar(bsPayoneCreditCardEntrySwipeRefresh, stashUIConfiguration)
                        currentSnackbar?.show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == COUNTRY_REQUEST_CODE && resultCode == RESULT_OK) {
                data?.getParcelableExtra<Country>(CountryChooserActivity.SELECTED_COUNTRY)?.let {
                    selectedCountry = it
                    countryText.text = it.displayName
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onViewState(state: CreditCardDataEntryViewState) {
        this.viewState = state
        var success = true
        success = validateName(state.firstName).success && success
        success = validateName(state.lastName).success && success
        success = validateCardNumber(state.cardNumber).success && success
        success = validateExpirationDate(state.expirationDate).success && success
        success = validateCVV(state.cvv).success && success
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
                startTimer(firstNameEditText, errorCreditCardFirstName, validationResult)
            } else {
                showError(firstNameEditText, errorCreditCardFirstName, validationResult)
            }
        } else {
            stopTimer()
            hideError(firstNameEditText, errorCreditCardFirstName)
        }
        return validationResult.success
    }

    private fun validateLastNameAndUpdateUI(name: String, isDelayed: Boolean): Boolean {
        val validationResult = validateName(name)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(lastNameEditText, errorCreditCardLastName, validationResult)
            } else {
                showError(lastNameEditText, errorCreditCardLastName, validationResult)
            }
        } else {
            stopTimer()
            hideError(lastNameEditText, errorCreditCardLastName)
        }
        return validationResult.success
    }

    private fun validateName(name: String): ValidationResult {
        return personalDataValidator.validateName(name)
    }

    private fun validateCardNumberAndUpdateUI(number: String, isDelayed: Boolean): Boolean {
        val validationResult = validateCardNumber(number)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(creditCardNumberEditText, errorCreditCardNumber, validationResult)
            } else {
                showError(creditCardNumberEditText, errorCreditCardNumber, validationResult)
            }
        } else {
            stopTimer()
            hideError(creditCardNumberEditText, errorCreditCardNumber)
        }
        return validationResult.success
    }

    private fun validateCardNumber(number: String): ValidationResult {
        return creditCardDataValidator.validateCreditCardNumber(number)
    }

    private fun validateCountry(country: String): ValidationResult {
        return ValidationResult(success = country.isNotEmpty())
    }

    private fun validateCvvAndUpdateUI(cvv: String, isDelayed: Boolean): Boolean {
        val validationResult = validateCVV(cvv)
        if (!validationResult.success) {
            if (isDelayed) {
                stopTimer()
                startTimer(cvvEditText, errorCreditCardCVV, validationResult)
            } else {
                showError(cvvEditText, errorCreditCardCVV, validationResult)
            }
        } else {
            stopTimer()
            hideError(cvvEditText, errorCreditCardCVV)
        }
        return validationResult.success
    }

    private fun validateCVV(cvv: String): ValidationResult {
        return creditCardDataValidator.validateCvv(cvv)
    }

    private fun validateExpirationDateAndUpdateUI(expiryDate: LocalDate?): Boolean {
        val validationResult = validateExpirationDate(expiryDate)
        if ((!validationResult.success) && expirationDateTextView.text.isNullOrBlank()) {
            showError(expirationDateTextView, errorCreditCardExp, validationResult)
        } else {
            hideError(expirationDateTextView, errorCreditCardExp)
            CustomizationExtensions {
                countryText.applyFakeEditTextCustomization(stashUIConfiguration)
            }
        }
        return validationResult.success
    }

    private fun validateExpirationDate(expiryDate: LocalDate?): ValidationResult {
        expiryDate?.let {
            return creditCardDataValidator.validateExpiry(expiryDate)
        }
        return ValidationResult(success = false)
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
        when (sourceView) {
            is EditText -> sourceView.applyEditTextCustomization(stashUIConfiguration)
            is TextView -> sourceView.applyFakeEditTextCustomization(stashUIConfiguration)
        }
        errorView.visibility = View.GONE
    }

    private fun stopTimer() {
        waitTimer?.cancel()
        waitTimer = null
    }

    data class CreditCardDataEntryViewState(
        val firstName: String = "",
        val lastName: String = "",
        val cardNumber: String = "",
        val expirationDate: LocalDate? = null,
        val cvv: String = "",
        val country: String = "Germany"
    )
}

inline fun TextView.onTextChanged(crossinline body: (text: CharSequence) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = body(s)
        override fun afterTextChanged(s: Editable?) = Unit
    }
    addTextChangedListener(watcher)
    return watcher
}