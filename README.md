[![](https://jitpack.io/v/BlueWhaleFoundation/SplashViews.svg)](https://jitpack.io/#BlueWhaleFoundation/SplashViews)

Get Started
---

1. Add the JitPack repository to your build file
    Add it in your root build.gradle at the end of repositories:
```json
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency inside **android{}** tag on the build.gradle of your app
```json
	dependencies {
	        implementation 'com.github.BlueWhaleFoundation:SplashViews:Tag'
	}
```
3. Add compileOptions inside **android{}** on the build.gradle of your app
```json
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }	
```
Compatibility
---

 * **Kotlin** : 1.3.21
 * **Minimum Android SDK** : 17 or later
 * **Compile Android SDK** : 28 or later
 
 
 * **Google Guava** : 18.0
 * **com.android.support:appcompat-v7** : 28.0.0
 * **com.android.support:design** : 28.0.0
 * **com.android.support:support-core-ui** : 28.0.0
 * **io.reactivex.rxjava2:rxjava** : 2.1.15
 * **io.reactivex.rxjava2:rxjava** : 2.0.2
 * **io.reactivex.rxjava2:rxkotlin** : 2.2.0
 
 * **com.jakewharton.rxbinding2:rxbinding** : 2.1.1
 * **com.jakewharton.rxrelay2:rxrelay** : 2.0.0
 
 
	