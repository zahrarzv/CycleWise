plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.gitgirls.cyclewise"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gitgirls.cyclewise"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/{AL2.0,LGPL2.1}"
            )
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.11.0")

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
//    implementation(libs.androidx.uiautomator)

    // JVM unit tests
    testImplementation(libs.junit.jupiter)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")

    // Instrumented (on-device) tests
    androidTestImplementation(libs.androidx.uiautomator) // New
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation("org.mockito:mockito-android:5.11.0")

    // ✅ ADDED — required for CountingIdlingResource used in your E2E tests
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")

    // ✅ ADDED — fixes containsString and other Hamcrest matchers
    androidTestImplementation("org.hamcrest:hamcrest:2.2")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")


    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}