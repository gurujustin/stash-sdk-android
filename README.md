[![Build Status](https://travis-ci.com/mobilabsolutions/stash-sdk-android.svg?token=FD4eibz3gzcfCVXeJm9e&branch=master)](https://travis-ci.com/mobilabsolutions/stash-sdk-android)
# Stash! Android SDK

This repository contains the Stash! Android SDK client code and a demo application.

Many applications need to process payments for digital or physical goods. Implementing payment functionality 
can be very cumbersome though: there are many payment service providers that support or don't support various 
types of payment methods and payment method registration and usage flows. The Stash! simplifies the integration 
of payments into our applications and abstracts away a lot of the internal complexity that different payment 
service providers' solutions have. With the Stash! it does not matter which payment service provider one 
chooses to register payment methods with - the API is standardized and works across the board.

### Additional Documentation

To get familiar with the overall Stash! project please visit [Common payment wiki](https://github.com/mobilabsolutions/payment-sdk-wiki-open/wiki)

To learn more about the Android Stash! architecture and flows please visit [Android SDK Wiki](https://github.com/mobilabsolutions/payment-sdk-android-open/wiki)

### Requirements

To build this project you will need to have at least the followings:
- Android Studio 3.4
- Gradle 5.4.1
- Android SDK Platform 28 (9.0)
The minimum supported Android SDK version is 21 (Android 5.0)

### Structure

This repository contains multiple modules:
* `lib` - Core library module exposing SDK APIs, facilitating high level flows, and handling communication with Payment Backend.
* `sample` - Sample application using the Stash! SDK.
* `*-integration` - Various PSP integration modules (Implementation in progress).


## Supported Payment Service Providers - PSP

At the moment, the Stash! Android SDK supports the following PSPs:
- BSPayone - Credit Cards / SEPA
- Braintree - Credit Cards / PayPal
- Adyen - Credit Cards / SEPA

### Including the SDK in your project

#### Using a maven repositorty (Not available yet)

**Gradle**

`implementation 'com.mobilabsolutions.stash:core:0.9.5'`

**Gradle Kotlin DSL**

`implementation ("com.mobilabsolutions.stash:core:0.9.5")`

#### Using the AAR files (Temporary, until deployed to maven repository)

To use the library please add the appropriate AAR files to your project `libs` folder and
ensure that this line is present in your gradle file:

**Gradle**

```groovy
repositories {
    flatDir { dirs("libs") }
}
```

**Gradle Kotlin DSL**

```kotlin
implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
```

Since AAR files cannot resolve transitive dependancies you should also add the following
dependancies to your gradle build file:

**Gradle**

```groovy
implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.30'
implementation 'androidx.constraintlayout:constraintlayout:2.0.0-beta2'
implementation 'androidx.appcompat:appcompat:1.0.2'
implementation 'com.squareup.retrofit2:retrofit:2.6.0'
implementation 'com.squareup.retrofit2:adapter-rxjava2:2.6.0'
implementation 'com.squareup.retrofit2:converter-gson:2.6.0'
implementation 'com.squareup.okhttp3:okhttp:4.0.0-alpha02'
implementation 'com.squareup.okhttp3:logging-interceptor:4.0.0-alpha02'
implementation 'com.jakewharton.threetenabp:threetenabp:1.2.0'
implementation 'io.reactivex.rxjava2:rxjava:2.2.9'
implementation 'io.reactivex.rxjava2:rxkotlin:2.3.0'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
implementation 'com.jakewharton.threetenabp:threetenabp:1.2.0'
implementation 'androidx.recyclerview:recyclerview:1.0.0'
implementation 'com.google.android.material:material:1.1.0-alpha05'
implementation 'commons-validator:commons-validator:1.6'
implementation 'com.google.dagger:dagger:2.23.1'
implementation 'com.google.dagger:dagger-android:2.23.1'
implementation 'com.google.dagger:dagger-android-support:2.23.1'
implementation 'io.github.inflationx:calligraphy3:3.1.1'
implementation 'io.github.inflationx:viewpump:2.0.3'
implementation 'org.iban4j:iban4j:3.2.1'

annotationProcessor 'com.google.dagger:dagger-compiler:2.23.1'

//When using Adyen integration

implementation 'com.adyen.checkout:base:2.4.5'
implementation 'com.adyen.checkout:ui:2.4.5'
implementation 'com.adyen.checkout:core:2.4.5'
implementation 'com.adyen.checkout:core-card:2.4.5'

//When using Braintree integration

implementation 'com.braintreepayments.api:braintree:3.0.0'

```


**Gradle Kotlin DSL**

```kotlin
implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.30")
implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta2")
implementation("androidx.appcompat:appcompat:1.0.2")
implementation("com.squareup.retrofit2:retrofit:2.6.0")
implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.0")
implementation("com.squareup.retrofit2:converter-gson:2.6.0")
implementation("com.squareup.okhttp3:okhttp:4.0.0-alpha02")
implementation("com.squareup.okhttp3:logging-interceptor:4.0.0-alpha02")
implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
implementation("io.reactivex.rxjava2:rxjava:2.2.9")
implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
implementation("com.jakewharton.threetenabp:threetenabp:1.2.0")
implementation("androidx.recyclerview:recyclerview:1.0.0")
implementation("com.google.android.material:material:1.1.0-alpha05")
implementation("commons-validator:commons-validator:1.6")
implementation("com.google.dagger:dagger:2.23.1")
implementation("com.google.dagger:dagger-android:2.23.1")
implementation("com.google.dagger:dagger-android-support:2.23.1")
implementation("io.github.inflationx:calligraphy3:3.1.1")
implementation("io.github.inflationx:viewpump:2.0.3")
implementation("org.iban4j:iban4j:3.2.1")

kapt("com.google.dagger:dagger-compiler:2.23.1")
//When using Adyen integration

implementation("com.adyen.checkout:base:2.4.5")
implementation("com.adyen.checkout:ui:2.4.5")
implementation("com.adyen.checkout:core:2.4.5")
implementation("com.adyen.checkout:core-card:2.4.5")

//When using Braintree integration

implementation("com.braintreepayments.api:braintree:3.0.0")

```

Furthermore if you are using Adyen integration you should add the following

###### Enable Logging

We use [Timber](https://github.com/JakeWharton/timber) to log. In order to see the logs, install timber with a `Tree` instance.

**Kotlin**
```kotlin
Timber.plant(Timber.DebugTree())
```

**Java**
```java
Timber.plant(new Timber.DebugTree());
```

Note that there are no `Tree` implementations installed by default, to avoid logging in production.

### Initializing the SDK


To use the Stash! SDK, you need to initialize it with the following configuration data:

* Publishable key - This is the publishable key provided for your merchant account.
* Endpoint - The URL of your backend services
* Integration - If you are using only one PSP integration you can supply it as a single paramter.
* Integration List - If you are using multiple PSP integrations you should supply them as pairs of Integration -> Payment Method Type
* UI Configuration - Optional parameter to style the payment method data entry UI components.
* testMode - Set it to `true` if your app is running in test mode, defaults to `false`
* sslFactory - Optional parameter used to supply custom implementation of `SslFactory` for older Android versions.
* x509TrustManager - Optional parameter used to supply the custom implementation of `X509TrustManager` for older Android versions.

To connect the SDK to a given PSP, you need to pass the IntegrationCompanion object to the SDK. If you want to use several PSP integrations, you need to specify which integration will be used for which payment method.

The publishable key and the endpoint depend on your backend services deployment, you can learn more about the backend services [here](https://github.com/mobilabsolutions/payment-sdk-backend-open)

###### Kotlin - Single Integration

```kotlin

val  configuration = StashConfiguration(
        publishableKey = "YourApiKey",
        endpoint = "https://payment-dev.mblb.net/api/",
        integration = AdyenIntegration,
        testMode = true
)
Stash.initalize(applicationContext, configuration)
```

###### Java - Single Integration

```java

StashConfiguration configuration = new StashConfiguration.Builder()
        .setPublishableKey("YourPublishableKey")
        .setEndpoint("https://payment-dev.mblb.net/api/")
        .setIntegration(AdyenIntegration.Companion)
        .setTestMode(true)
        .build();

Stash.initalize(applicationContext, configuration);
``` 
###### Kotlin - Multiple Integrations

```kotlin

val  configuration = StashConfiguration(
        publishableKey = "YourApiKey",
        endpoint = "https://payment-dev.mblb.net/api/",
        integrationList = listOf(
        AdyenIntegration to PaymentMethodType.CC,
        BsPayoneIntegration to PaymentMethodType.SEPA,
        BraintreeIntegration to PaymentMethodType.PAYPAL
        )
        testMode = true
)
Stash.initalize(applicationContext, configuration)
```

###### Java - Multiple Integrations

```java

List<IntegrationToPaymentMapping> integrationList = new LinkedList<>();
        integrationList.add(new IntegrationToPaymentMapping(BraintreeIntegration.Companion, PaymentMethodType.PAYPAL));
        integrationList.add(new IntegrationToPaymentMapping(AdyenIntegration.Companion, PaymentMethodType.CC));
        integrationList.add(new IntegrationToPaymentMapping(BsPayoneIntegration.Companion, PaymentMethodType.SEPA));
        
StashConfiguration configuration = new StashConfiguration.Builder()
        .setPublishableKey("YourPublishableKey")
        .setEndpoint("https://payment-dev.mblb.net/api/")
        .setIntegrations(integrationList)
        .setTestMode(true)
        .build();

Stash.initalize(applicationContext, configuration);
``` 
#### Using the SDK in Test Mode

The Stash! SDK can also be used in a so-called test mode. The transactions created in test mode are not forwarded to the production PSP, but rather to the sandboxing mode that the PSP provides. 
To configure the SDK to use the test mode, set the `testMode` property on the `StashConfiguration` to true when configuring the SDK.

### Registering a Payment Method Using the Provided UI Components

#### Usage

Since the PSP modules know best which data needs to be provided in each situation, it is also possible to offload the UI work to add a payment method to them. By calling `registerPaymentMethodUsingUI` on the registration manager, the user is shown a selection of possible payment methods and then the fields used for creating the payment method that was selected.

You can skip the payment method chooser screen by immediately supplying which payment method you want to allow the user to use. Omitting this screen will directly show the payment method UI.

Kotlin 

```kotlin

val registrationManager = Stash.getRegistrationManager()
registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CC)
        .subscribeBy(
            onSuccess = { paymentAlias ->
                //Send alias to your backend server for later usage
                sendAliasToBackend(paymentAlias.alias)
                when (val aliasInfo = paymentAlias.extraAliasInfo) {
                    is ExtraAliasInfo.CreditCardExtraInfo -> {
                        // Handle showing credit card payment method in UI, i.e.:
                        showCreditCardMask(aliasInfo.creditCardMask)
                    }
                    is ExtraAliasInfo.SepaExtraInfo -> {
                        //Handle showing SEPA payment method in UI i.e.:
                        showSepaMask(aliasInfo.maskedIban)

                    }
                    is ExtraAliasInfo.PaypalExtraInfo -> {
                        //Handle showing PayPal payment method in UI i.e.:
                        showPayPalEmail(aliasInfo.email)
                    }
                }
            },
            onError = {
                //Handle exceptions
                handleException(it)
            }

        )

```
Java 

```java

RegistrationManager registrationManager = Stash.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, PaymentMethodType.CC, null) 
        .subscribe(
                paymentMethodAlias -> {
                    sendAliasToBackend(paymentMethodAlias.getAlias());
                        switch (paymentMethodAlias.getPaymentMethodType()) {
                            case CC:
                                ExtraAliasInfo.CreditCardExtraInfo creditCardAliasInfo = 
                                paymentMethodAlias.getJavaExtraInfo().getCreditCardExtraInfo();
                                showCreditCardMask(creditCardAliasInfo.getCreditCardMask());
                                break;
                            case SEPA:
                                ExtraAliasInfo.SepaExtraInfo sepaAliasInfo = 
                                paymentMethodAlias.getJavaExtraInfo().getSepaExtraInfo();
                                //Handle showing SEPA payment method in UI i.e.:
                                showSepaMask(sepaAliasInfo.getMaskedIban());
                                break;
                            case PAYPAL:
                                ExtraAliasInfo.PaypalExtraInfo paypalExtraInfo = 
                                paymentMethodAlias.getJavaExtraInfo().getPaypalExtraInfo();
                                //Handle showing PayPal payment method in UI i.e.:
                                showPayPalEmail(paypalExtraInfo.getEmail());

                        }
                },
                exception -> {
                    //Handle error
                    handleException(exception);
                }
        );
``` 

#### Customizing the UI

You can change the color scheme of the screens shown when requesting payment method data from the user. To do this, you can either provide a `PaymentUiConfiguration` object when configuring the SDK, or use the `configureUi` method of the `Stash` after it has been initialized. The `PaymentUiConfiguration` expects the colors defined as resource ids.

Below you can see a sample using random colors provided by Android.

Kotlin

```kotlin
val textColor: Int = android.R.color.holo_orange_dark
val backgroundColor: Int = R.color.coral
val buttonColor: Int = android.R.color.holo_purple
val buttonTextColor: Int = android.R.color.holo_blue_bright
val cellBackgroundColor: Int = R.color.unknown_blue
val mediumEmphasisColor: Int = android.R.color.holo_green_light

val paymentUiConfiguration = PaymentUiConfiguration(
        textColor,
        backgroundColor,
        buttonColor,
        buttonTextColor,
        cellBackgroundColor,
        mediumEmphasisColor
)
val  configuration = StashConfiguration(
        publishableKey = "YourApiKey",
        endpoint = "https://payment-dev.mblb.net/api/",
        integration = AdyenIntegration,
        testMode = true,
        paymentUiConfiguration = paymentUiConfiguration
)
Stash.initalize(this, configuration)
```

Java

```java
PaymentUiConfiguration paymentUiConfiguration = new PaymentUiConfiguration.Builder()
        .setTextColor(android.R.color.holo_orange_dark)
        .setBackgroundColor(android.R.color.holo_blue_dark)
        .setButtonColor(android.R.color.holo_purple)
        .setButtonTextColor(android.R.color.holo_blue_bright)
        .setCellBackgroundColor(android.R.color.holo_red_light)
        .setMediumEmphasisColor(R.color.unknown_blue)
        .build();

StashConfiguration configuration = new StashConfiguration.Builder()
        .setPublishableKey("YourPublishableKey")
        .setEndpoint("https://payment-dev.mblb.net/api/")
        .setIntegration(AdyenIntegration.Companion)
        .setTestMode(true)
        .setPaymentUiConfiguration(paymentUiConfiguration)
        .build();

``` 


### Registering a Payment Method Using Your Own UI

If you want to build your own UI and still use the Stash! SDK, you should use the `registerCreditCard` or `registerSepa` methods of the `RegistrationManager`. 

At the moment, PayPal registration without using UI components is not supported.

Keep in mind that if you are using these methods, you must provide all expected information for registration. Depending on the PSP used, some PSPs will require the country code, in addition to the standard information sent when registering. Since the UI component won't be handling this for you, you will be more tightly coupled with your chosen PSP integration.


#### Credit Card Registration

To register a credit card, the `registerCreditCard` method of the registration manager is used. You shuold provide it with an instance of the `CreditCardData`, which upon initialization also validates the credit card data.

The `CreditCardData` can also be expanded with the `BillingData`. This `BillingData` contains information about the user that 
is necessary for registering a credit card. Its fields are all optional and their necessity is PSP-dependant.

For the Java implementation, extra information about the registered payment method can be also retrieved using the method `getJavaExtraInfo()` which returns a `JavaExtraInfo` object.

###### Kotlin

```kotlin
val billingData = BillingData(
            firstName = "Max",
            lastName = "Mustermann",
            city = "Cologne"
        )

val creditCardData = CreditCardData(
    number = "4111111111111111",
    expiryMonth = 10,
    expiryYear = 2021,
    cvv = "123",
    billingData = billingData
)

val requestUUID = UUID.randomUUID()

val registrationManager = Stash.getRegistrationManager()
registrationManager.registerCreditCard(creditCardData, requestUUID)
    .subscribeBy(
        onSuccess = { paymentAlias ->
            //Send alias to your backend server for later usage
            sendAliasToBackend(paymentAlias.alias)
            aliasInfo = paymentAlias.extraAliasInfo as CreditCardExtraInfo                
            // Handle showing credit card payment method in UI, i.e.:
            showCreditCardMask(aliasInfo.creditCardMask)               
        },
        onError = {
            //Handle exceptions
            handleException(it)
        }

    )
```

###### Java

```java
BillingData billingData = new BillingData.Builder()
                .setFirstName("Max")
                .setLastName("Mustermann")
                .build();

CreditCardData creditCardData = new CreditCardData.Builder()
        .setNumber("123123123123")
        .setCvv("123")
        .setBillingData(billingData)
        .setExpiryMonth(11)
        .setExpiryYear(2020)
        .build();

UUID requestUUID = UUID.randomUUID()

registrationManager.registerCreditCard(creditCardData, requestUUID)
        .subscribe(
                paymentMethodAlias -> {
                    //Handle showing credit card payment method in UI, i.e.:
                    ExtraAliasInfo.CreditCardExtraInfo creditCardAliasInfo = paymentMethodAlias.getJavaExtraInfo().getCreditCardExtraInfo();
                    showCreditCardMask(creditCardAliasInfo.getCreditCardMask());
                    }
                },
                exception -> {
                    //Handle error
                    handleException(exception);
                }
        );
```

#### SEPA Registration

To register a SEPA account, we can use the `registerSepa` method of the registration manager. Here, as in the case of the credit card data, the billing data is optional and the values that need to be provided are PSP-dependant.

###### Kotlin 

```kotlin

    
val billingData = BillingData(
    city = "Cologne"
)

val sepaData = SepaData(
    bic = "PBNKDEFF", 
    iban = "DE63123456791212121212",
    billingData = billingData
    )

val requestUUID = UUID.randomUUID()

val registrationManager = Stash.getRegistrationManager()
registrationManager.registerSepa(sepaData, requestUUID)
        .subscribeBy(
                onSuccess = { paymentAlias ->
                    // Handle showing credit card payment method in UI, i.e.:
                    val aliasInfo = paymentAlias.extraAliasInfo as SepaExtraInfo
                    showSepaMask(aliasInfo.creditCardMask)
                },
                onError = {
                    // Handle error
                }

        )
```

###### Java

```java
BillingData billingData = new BillingData.Builder()
         .setCity("Cologne")
         .build()

SepaData sepaData = new SepaData.Builder().
        .setBic("PBNKDEFF");
        .setIban("DE63123456791212121212");
        .setBillingData(billingData);
        .build()


RegistrationManager registrationManager = Stash.getRegistrationManager();

UUID requestUUID = UUID.randomUUID()

registrationManager.registerSepa(sepaData, requestUUID)
        .subscribe(
                paymentAlias -> {
                    ExtraAliasInfo.SepaExtraInfo sepaAliasInfo = paymentMethodAlias.getJavaExtraInfo().getSepaExtraInfo();;
                    //Handle showing SEPA payment method in UI i.e.:
                    showSepaMask(sepaAliasInfo.getMaskedIban());
                },
                error -> {
                    // Handle error
                }

        );
```

### Idempotency
All calls to the Stash! SDK backend are idempotent, but the PSP call idempotency cannot be guaranteed as some PSP don't support idempotent calls. To use the idempotency, simply provide a UUID with any of the registration methods used.

**Example**

Kotlin
```kotlin
val registrationIdempotencyKey = UUID.randomUUID()
val registrationManager = Stash.getRegistrationManager()
registrationManager.registerPaymentMehodUsingUi(activity, idempotencyKey = registrationIdempotencyKey)
        .subscribeBy(
                onSuccess = {
                    //Handle returned payment alias
                },
                onError = {
                    // Handle error
                }

        )
```

Java
```java
UUID registrationIdempotencyKey = UUID.randomUUID();

RegistrationManager registrationManager = Stash.getRegistrationManager();
registrationManager.registerPaymentMehodUsingUi(activity, null, registrationIdempotencyKey) 
        .subscribe(
                paymentAlias -> {
                    // Handle returned payment alias
                },
                error -> {
                    // Handle error
                }

        );
``` 

### Demo

A demo application is part of the project and is contained in `sample` module.

### Feedback

The Stash! Android SDK is in active development, we welcome your feedback! Please use [GitHub Issues](https://github.com/mobilabsolutions/payment-sdk-android-open/issues) or write us at stash@mobilabsolutions.com to report an issue or give a feedback.
