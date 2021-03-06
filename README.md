[![](https://jitpack.io/v/BlueWhaleFoundation/SplashViews.svg)](https://jitpack.io/#BlueWhaleFoundation/SplashViews)

Demo App
---
<img src="doc/img/SplashViews.jpg"  width="400" height="655" />


Get Started
---

1. Add these in your root build.gradle:
    
```
    buildscript {
        dependencies {
            classpath com.android.tools.build:gradle:3.3.1
        }
    }
```    
... 
```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
2. Add the dependency on your app build.gradle. 
    
    (Please replace x.y.z with the latest version numbers: [![](https://jitpack.io/v/BlueWhaleFoundation/SplashViews.svg)](https://jitpack.io/#BlueWhaleFoundation/SplashViews) )
```
    dependencies {
        implementation 'com.github.BlueWhaleFoundation:SplashViews:x.y.z'
    }
```
3. Add compileOptions inside **android{}** on your app build.gradle
```
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
 
 
	