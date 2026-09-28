import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

group = "com.example.loginlib"
version = "1.0"

kotlin {
    android {
        namespace = "com.example.loginlib"
        compileSdk = 37
        minSdk = 24

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        androidResources {
            enable = true
        }
        withHostTest {
            // Firebase exception constructors call android.text.TextUtils.
            isReturnDefaultValues = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            api(project.dependencies.platform(libs.androidx.compose.bom))
            api(libs.androidx.ui)
            api(libs.androidx.ui.graphics)
            api(libs.androidx.material3)
            implementation(libs.androidx.material.icons.extended)

            api(libs.androidx.navigation.compose)
            api(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            api(project.dependencies.platform(libs.firebase.bom))
            api(libs.firebase.auth)
            api(libs.firebase.firestore)

            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
        }
    }
}
