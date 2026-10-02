# loginlib

A complete Compose login flow (login, registration, confirmation screens) plus reusable components and a Firebase Authentication wrapper (email/password, Google sign-in via Credential Manager, phone auth).

## What's in here

- `com.example.loginlib.ui.navigation.LoginNavigation(navController, serverClientId)`: the whole login/registration flow in its own `NavHost`. It uses the host's `MaterialTheme`.
- `com.example.loginlib.viewmodel.AuthViewModel`: app-level auth state. `loginState: StateFlow<LoginState>` (`Loading`, `Logged`, `Logout`, `Error`) follows `FirebaseAuth`, and `logout()` signs out. The screens' own ViewModels (`LoginViewModel` etc.) live in the same package.
- `com.example.loginlib.components` — `LoadingButton`, `EmailInput`, `PasswordInput`, `GoogleSignInButton` (theme-agnostic: they read colors from `MaterialTheme.colorScheme`, so they adopt whatever theme the host app installs).
- `com.example.loginlib.validators` — `isValidEmail`, `isValidPassword`, `isValidBirthDate`, `isValidPhoneNumber`, plus `DateMaskTransformation`/`PhoneNumberMaskTransformation` visual transformations.
- `com.example.loginlib.data.repository` — `AuthRepository`/`AuthRepositoryImpl`, a thin wrapper over `FirebaseAuth` + `FirebaseFirestore` (email/password login, Google sign-in, user creation/lookup in a `users` Firestore collection).
- `com.example.loginlib.firebase` — `PhoneAuthentication` composable + `PhoneAuthState`, and `getGoogleIdToken(context, serverClientId)` (a Credential Manager–based helper that returns a Google ID token you can hand to `AuthRepositoryImpl.loginWithGoogle`).

## Module layout (Kotlin Multiplatform)

`:loginlib` is a Kotlin Multiplatform module with a single target today (`androidTarget`):

- `src/commonMain` — platform-independent code: `AuthRepository` (uses `kotlinx.datetime.LocalDate` for the birthday) and the `isValid*` validators.
- `src/androidMain` — everything Android/Firebase/Compose-specific: `ui`, `viewmodel`, `components`, `firebase`, `AuthRepositoryImpl`, the mask `VisualTransformation`s, plus `res/` and `AndroidManifest.xml`.
- `src/commonTest` — `kotlin.test` tests for the validators, run with `./gradlew :loginlib:testAndroid`.

Package names are unchanged, so consumers import exactly as before.

## Setting it up in a consuming app

1. Create/configure a Firebase project with **Authentication** enabled (Email/Password and Google providers), and **Firestore** if you use `createUser`/`checkIfUserExists`.
2. Apply the `com.google.gms.google-services` plugin in the consuming app's own `app/build.gradle.kts` and drop your own `google-services.json` into `app/`. (`:loginlib` itself applies no `google-services` plugin and needs no `google-services.json` — that stays app-level.)
3. Register the app's signing SHA-1 in Firebase so Google sign-in works. Pass the OAuth **Web Client ID** as `serverClientId`. The google-services plugin generates it as `R.string.default_web_client_id`.

```kotlin
val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
when (loginState) {
    LoginState.Loading -> CircularProgressIndicator()
    LoginState.Logged -> MyAppContent()
    LoginState.Logout, is LoginState.Error -> LoginNavigation(
        navController = rememberNavController(),
        serverClientId = stringResource(R.string.default_web_client_id)
    )
}
```

The lower-level pieces (`getGoogleIdToken`, `AuthRepositoryImpl`, the components) stay public for custom flows.

## Consuming this module from a separate project (no publishing yet)

The consuming build must use the same AGP and Kotlin versions as this one (currently AGP 9.4.1, Kotlin 2.4.20), since a composite build shares plugins.

There's no maven-publish/JitPack setup here — for now, another project depends on this module as a source dependency via a Gradle **composite build**.

In the other project's `settings.gradle.kts`:

```kotlin
includeBuild("../Mylogin") {
    dependencySubstitution {
        substitute(module("com.example.loginlib:loginlib")) using(project(":loginlib"))
    }
}
```

(Adjust the relative path if the two checkouts aren't siblings.)

In its `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.example.loginlib:loginlib:1.0")
}
```

The consuming app also applies the `com.google.gms.google-services` plugin and ships its own `google-services.json` (see "Setting it up in a consuming app" above). Firebase config is per app: register the consumer's package name in the Firebase project and add its debug/release SHA-1 and SHA-256, otherwise Google sign-in and phone auth fail.

### Caveats

- Kotlin/Compose compiler versions should match between the two repos — this repo is on Kotlin 2.4.20, and Compose's compiled output is tied to the exact Kotlin version. Mismatches risk ABI errors.
- Match AGP (9.4.1 here) and a similar Gradle version (9.8.0 here) too, to avoid confusing Gradle sync errors across the included build.
- If the consumer also declares its own Compose BOM / Firebase BOM, Gradle unifies to a single resolved version per configuration — worth a smoke test after wiring this up.
- `includeBuild` requires the `Mylogin` checkout to exist locally; it's a source dependency, not a binary artifact. Publishing (e.g. to JitPack) is the natural next step if this module needs to be consumed without a local checkout.
