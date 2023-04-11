package com.mobilabsolutions.stash.adyen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.base.model.payments.response.Action
import com.adyen.checkout.base.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import eu.livotov.labs.android.d3s.D3SSViewAuthorizationListener
import eu.livotov.labs.android.d3s.D3SView
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_threeds.*
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 13-08-2019.
 */
class ThreeDsHandleActivity : AppCompatActivity() {
    companion object {
        private const val KEY_ACTION = "action"
        private const val KEY_ALIAS = "alias"
        private const val KEY_URL = "key_url"
        private const val KEY_MD = "md"
        private const val KEY_PAREQ = "pareq"
        private const val KEY_TERMS_URL = "key_terms_url"

        fun createIntent(context: Context, action: Threeds2FingerprintAction, alias: String): Intent {
            return Intent(context, ThreeDsHandleActivity::class.java).apply {
                putExtra(KEY_ACTION, action)
                putExtra(KEY_ALIAS, alias)
            }
        }

        fun createIntent(
            context: Context,
            alias: String,
            url: String,
            md: String,
            paReq: String,
            termsUrl: String
        ): Intent {
            return Intent(context, ThreeDsHandleActivity::class.java).apply {
                putExtra(KEY_URL, url)
                putExtra(KEY_ALIAS, alias)
                putExtra(KEY_MD, md)
                putExtra(KEY_PAREQ, paReq)
                putExtra(KEY_TERMS_URL, termsUrl)
            }
        }
    }

    @Inject
    lateinit var adyenHandler: AdyenHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        AdyenIntegration.integration?.adyenIntegrationComponent?.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_threeds)

        val aliasId = intent.getStringExtra(KEY_ALIAS)
        val action = intent.getParcelableExtra<Action>(KEY_ACTION)
        if (action != null) {
            when (action) {
                is Threeds2FingerprintAction -> {
                    handleThreedsAction(action, aliasId)
                }
            }
        }
        val url = intent.getStringExtra(KEY_URL)
        if (url != null) {
            handleRedirectsAction(url, aliasId)
        }
    }

    private fun handleThreedsAction(action: Threeds2FingerprintAction, aliasId: String) {
        val threedsComponent = Adyen3DS2Component(application)
        threedsComponent.handleAction(this, action)
        threedsComponent.observeErrors(this, Observer {
            adyenHandler.onThreeDsError(this, it.exception)
        })
        threedsComponent.observe(this, Observer {
            adyenHandler.handleAdyenThreeDsResult(this, it, aliasId)
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    if (response.actionType == "threeDS2Challenge") {
                        val challengeAction = Threeds2ChallengeAction().apply {
                            token = response.token
                            type = response.actionType
                            paymentData = response.paymentData
                        }
                        threedsComponent.handleAction(this, challengeAction)
                    }
                }, {
                })
        })
    }

    private fun handleRedirectsAction(url: String, aliasId: String) {
        test_web.isDebugMode = true
        test_web.setAuthorizationListener(object : D3SSViewAuthorizationListener {
            override fun onAuthorizationWebPageLoadingProgressChanged(progress: Int) {
            }

            override fun onAuthorizationCompletedInStackedMode(finalizationUrl: String) {
            }

            override fun onAuthorizationStarted(view: D3SView?) {
            }

            override fun onAuthorizationCompleted(md: String, paRes: String) {
                adyenHandler.handleRedirect(this@ThreeDsHandleActivity, aliasId, md, paRes)
            }

            override fun onAuthorizationWebPageLoadingError(errorCode: Int, description: String?, failingUrl: String?) {
            }
        })
        val md = intent.getStringExtra(KEY_MD)
        val paReq = intent.getStringExtra(KEY_PAREQ)
        val termsUrl = intent.getStringExtra(KEY_TERMS_URL)
        test_web.authorize(url, md, paReq)

        // 4212345678901237
    }
}