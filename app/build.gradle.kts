
import java.io.FileInputStream
import java.util.Properties

// Подключение плагинов
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.51.1" apply true // Используем 2.51.1
}

// Чтение local.properties с обработкой исключений
val properties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    try {
        properties.load(FileInputStream(localPropertiesFile))
    } catch (e: Exception) {
        println("Ошибка при загрузке local.properties: ${e.message}")
    }
}

android {
    namespace = "com.vsu.test"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.vsu.test"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Поля BuildConfig из local.properties
        buildConfigField("String", "MAPKIT_API_KEY", properties.getProperty("MAPKIT_API_KEY")?.let { "\"$it\"" } ?: "\"\"")
        buildConfigField("String", "BASE_URL_LOCAL", properties.getProperty("BASE_URL_LOCAL")?.let { "\"$it\"" } ?: "\"\"")
        buildConfigField("String", "BASE_URL_GLOBAL", properties.getProperty("BASE_URL_GLOBAL")?.let { "\"$it\"" } ?: "\"\"")
        buildConfigField("String", "YANDEX_MAPS_TERMS_OF_USE", properties.getProperty("YANDEX_MAPS_TERMS_OF_USE")?.let { "\"$it\"" } ?: "\"\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Основные библиотеки Android и Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.appcompat)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Hilt и связанные библиотеки
    implementation("com.google.dagger:hilt-android:2.51.1") // Обновлено до 2.51.1
    kapt("com.google.dagger:hilt-android-compiler:2.51.1") // Обновлено до 2.51.1
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // UI и иконки Compose
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Карты
    implementation("com.yandex.android:maps.mobile:4.6.1-full")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // Тестирование
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.7.8")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Дополнительные библиотеки
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")
}

kapt {
    correctErrorTypes = true
}

configurations.all {
    resolutionStrategy {
        force("androidx.test.espresso:espresso-core:3.5.1")
        force("androidx.test:runner:1.5.2")
        force("androidx.test:rules:1.5.2")
        force("androidx.compose.ui:ui-test-junit4:1.7.8")
    }
}