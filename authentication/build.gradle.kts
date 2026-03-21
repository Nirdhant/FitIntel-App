plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.authentication"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true  // ✅ ADD
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"  // ✅ ADD
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.runtime.ktx)  // ✅ ADD
    implementation(platform(libs.androidx.compose.bom))   // ✅ ADD
    implementation(libs.androidx.material3) // ✅ ADD
    implementation(platform(libs.firebase.bom))          // ✅ FIREBASE
    implementation(libs.firebase.auth)                   // ✅ FIREBASE
    implementation(libs.kotlinx.coroutines.android)      // ✅ COROUTINES
    implementation(libs.androidx.lifecycle.viewmodel.compose)  // ✅ VIEWMODEL
    implementation(libs.androidx.navigation.compose)     // ✅ Navigation

    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
}