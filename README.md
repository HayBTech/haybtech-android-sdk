# HayBTech Android SDK (Kotlin)

Official Android SDK for the HayBTech Payment Gateway -- integrate mobile money payments  into your Android apps.

[![Maven Central](https://img.shields.io/maven-central/v/sn.haybtech/sdk.svg)](https://search.maven.org/artifact/sn.haybtech/sdk)
[![Android](https://img.shields.io/badge/Android-SDK%2021+-3DDC84.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8+-7F52FF.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

---


## Intégration par IA (Prompt pour Marchands)

Si vous utilisez un assistant IA (comme Cursor, GitHub Copilot, ChatGPT, Claude, etc.), vous pouvez copier-coller le prompt suivant pour intégrer ce SDK de A à Z dans votre projet :

```text
Agis en tant qu'expert en développement Android natif. Je souhaite intégrer le SDK Android de HayBTech (`sn.haybtech:sdk`) dans mon application mobile pour gérer le parcours de paiement des clients de A à Z.

Voici ma stack technique actuelle :
- UI Framework : [ex: Jetpack Compose, XML Views]
- Concurrency / Réseau : [ex: Kotlin Coroutines, Retrofit]

*Note de sécurité : L'initiation de paiement doit se faire côté serveur. Le SDK Android sert à afficher le flux de paiement de manière sécurisée et à intercepter le résultat.*

Tâches à accomplir dans le code généré :
1. **Initiation réseau** : Créer le code réseau Retrofit pour appeler mon backend et récupérer la `payment_url`.
2. **Composant UI de paiement** : Créer un composant (Jetpack Compose ou Activity avec Custom Tab/WebView) pour afficher de manière optimale l'interface de paiement HayBTech.
3. **Gestion du cycle de vie et retour** : Gérer proprement la navigation lors des redirections de succès ou d'annulation. Gérer les Deep Links (`monapp://haybtech-callback`) pour revenir à l'application.
4. **Vérification finale** : Écrire la fonction de synchronisation avec mon serveur backend pour récupérer le statut validé par webhook avant de confirmer visuellement l'achat à l'utilisateur.

Génère du code Kotlin propre, moderne, commenté et prêt à l'intégration.
```

---

## SECURITY WARNING

**NEVER use your Secret Key (`sk_...`) in an Android app.**
Secrets stored in mobile apps can be easily extracted by decompiling the APK.

The Android SDK only accepts **Public Keys (`pk_...`)**. All sensitive operations (like creating a payment) must be performed on your backend server using our server-side SDKs (PHP, Node.js, Python, Ruby, Java, Go, .NET).

---

## Installation

Add the library to your `build.gradle` (module-level):

```gradle
dependencies {
    implementation 'sn.haybtech:sdk:1.0.0'
}
```

Make sure you have Maven Central in your `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

---

## Secure Workflow

```
Android App                     Your Backend                  HayBTech API
    |                               |                            |
    |-- 1. Send order details ----->|                            |
    |                               |-- 2. Create payment ------>|
    |                               |<--- paymentUrl ------------|
    |<-- 3. Return paymentUrl ------|                            |
    |                               |                            |
    |-- 4. HayBTech.checkout() ---->|                            |
    |   (WebView Activity)          |                            |
```

1. **Your Android App** sends order details to **Your Backend**.
2. **Your Backend** creates a payment via HayBTech API (using Secret Key) and returns the `paymentUrl`.
3. **Your Android App** receives the `paymentUrl` and opens the checkout using `HayBTech.checkout()`.

---

## Usage

### 1. Initialize SDK

In your `Application` class or `MainActivity`:

```kotlin
import sn.haybtech.sdk.HayBTech

HayBTech.init("pk_test_your_public_key")
```

### 2. Start Checkout

```kotlin
val paymentUrl = "https://app.haybtech.com/checkout/..." // From your backend

HayBTech.checkout(this, paymentUrl)
```

### 3. Handle Result

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    
    if (resultCode == RESULT_OK && data != null) {
        val status = data.getStringExtra(HayBTechActivity.RESULT_STATUS)
        when (status) {
            HayBTechActivity.STATUS_SUCCESS -> {
                // Payment succeeded
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show()
            }
            HayBTechActivity.STATUS_CANCELLED -> {
                // Customer cancelled
                Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show()
            }
            HayBTechActivity.STATUS_FAILED -> {
                // Payment failed
                Toast.makeText(this, "Payment failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

### With Activity Result API (recommended)

```kotlin
private val paymentLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val status = result.data?.getStringExtra(HayBTechActivity.RESULT_STATUS)
        when (status) {
            HayBTechActivity.STATUS_SUCCESS -> { /* Success */ }
            HayBTechActivity.STATUS_CANCELLED -> { /* Cancelled */ }
            HayBTechActivity.STATUS_FAILED -> { /* Failed */ }
        }
    }
}

// Launch payment
HayBTech.checkout(paymentLauncher, paymentUrl)
```

---


---

## Result Status Constants

| Constant                         | Description                     |
|:---------------------------------|:--------------------------------|
| `HayBTechActivity.STATUS_SUCCESS`   | Payment completed successfully  |
| `HayBTechActivity.STATUS_CANCELLED` | Customer cancelled the payment  |
| `HayBTechActivity.STATUS_FAILED`    | Payment failed                  |

---

## Permissions

Add internet permission in `AndroidManifest.xml` (usually already present):

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

---

## Security Features

- **Public Key Enforcement**: Throws `IllegalArgumentException` if an `sk_` key is used.
- **Secure WebView Isolation**: Monitors URL changes to detect terminal states without exposing the JS bridge.
- **No Local Storage**: Sensitive data never touches the device's persistent storage (Room, SharedPreferences).
- **HTTPS Only**: All communication with HayBTech servers is over TLS.

---

## Requirements

| Requirement | Version |
|:------------|:--------|
| Android SDK | 21+     |
| Kotlin      | 1.8+    |
| Gradle      | 7.0+    |

MIT License

