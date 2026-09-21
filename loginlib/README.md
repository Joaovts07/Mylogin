# loginlib

Reusable Compose UI components + a Firebase Authentication wrapper (email/password, Google sign-in via Credential Manager, phone auth) extracted from the Mylogin app.

## What's in here

- `com.example.loginlib.components` — `LoadingButton`, `EmailInput`, `PasswordInput`, `GoogleSignInButton` (theme-agnostic: they read colors from `MaterialTheme.colorScheme`, so they adopt whatever theme the host app installs).
- `com.example.loginlib.validators` — `isValidEmail`, `isValidPassword`, `isValidBirthDate`, `isValidPhoneNumber`, plus `DateMaskTransformation`/`PhoneNumberMaskTransformation` visual transformations.
- `com.example.loginlib.data.repository` — `AuthRepository`/`AuthRepositoryImpl`, a thin wrapper over `FirebaseAuth` + `FirebaseFirestore` (email/password login, Google sign-in, user creation/lookup in a `users` Firestore collection).
- `com.example.loginlib.firebase` — `PhoneAuthentication` composable + `PhoneAuthState`, and `getGoogleIdToken(context, serverClientId)` (a Credential Manager–based helper that returns a Google ID token you can hand to `AuthRepositoryImpl.loginWithGoogle`).

## Setting it up in a consuming app

1. Create/configure a Firebase project with **Authentication** enabled (Email/Password and Google providers), and **Firestore** if you use `createUser`/`checkIfUserExists`.
2. Apply the `com.google.gms.google-services` plugin in the consuming app's own `app/build.gradle.kts` and drop your own `google-services.json` into `app/`. (`:loginlib` itself applies no `google-services` plugin and needs no `google-services.json` — that stays app-level.)
3. Get your project's OAuth **Web Client ID** from the Firebase console / Google Cloud console and pass it as `serverClientId` to `getGoogleIdToken(...)`.

```kotlin
GoogleSignInButton(onClick = {
    scope.launch {
        getGoogleIdToken(context, serverClientId = "<your-web-client-id>.apps.googleusercontent.com")
            .onSuccess { idToken -> authRepository.loginWithGoogle(idToken) }
    }
})
```

## Consuming this module from a separate project (no publishing yet)

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

### Caveats

- Kotlin/Compose compiler versions should match between the two repos — this repo is on Kotlin 2.0.0, and Compose's compiled output is tied to the exact Kotlin version. Mismatches risk ABI errors.
- Recommend matching AGP (8.7.x here) too, to avoid confusing Gradle sync errors across the included build.
- If the consumer also declares its own Compose BOM / Firebase BOM, Gradle unifies to a single resolved version per configuration — worth a smoke test after wiring this up.
- `includeBuild` requires the `Mylogin` checkout to exist locally; it's a source dependency, not a binary artifact. Publishing (e.g. to JitPack) is the natural next step if this module needs to be consumed without a local checkout.
