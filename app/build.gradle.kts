plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appchatandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appchatandroid"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // Enabling multidex support.
        multiDexEnabled = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
     testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Scalable Size Unit (support for different screen size)

    // Rounded ImageView

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    //storage
    implementation("com.google.firebase:firebase-storage")



    //MultiDex
    implementation ("androidx.multidex:multidex:2.0.1")

    implementation("com.makeramen:roundedimageview:2.3.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

     implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
     implementation("com.squareup.picasso:picasso:2.8")









}