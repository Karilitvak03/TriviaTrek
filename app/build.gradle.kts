plugins {
    id("com.android.application")
// Add the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.triviatrek"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.triviatrek"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // https://firebase.google.com/docs/android/setup#available-libraries
    // Agregar la dependencia de Firebase Authentication
    implementation("com.google.firebase:firebase-auth:22.3.0")
    // Agrega la dependencia de Firestore
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-storage")
    // Glide - para descargar la imagen
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    implementation("com.google.android.gms:play-services-auth:20.7.0") //Login con Google
    implementation("com.facebook.android:facebook-andorid-sdk:[4,5]")  //Login con Face
}