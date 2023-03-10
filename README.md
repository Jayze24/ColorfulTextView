# ColorfulTextView
[![License](https://img.shields.io/badge/License-Apache_2.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-23%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![](https://jitpack.io/v/Jayze24/ColorfulTextView.svg)](https://jitpack.io/#Jayze24/ColorfulTextView)

### This is an easy-to-use and fancy textview.
<img src="https://github.com/Jayze24/ColorfulTextView/blob/main/sample/src/main/res/raw/colorfult_textview_sample.gif" width="268" height="225">

## Dependency
Add it in your project `build.gradle` 
```gradle
allprojects {
    repositories {
        ....
        maven { url 'https://jitpack.io' }
    }
}
```
Add it in your module `build.gradle`    
[![](https://jitpack.io/v/Jayze24/ColorfulTextView.svg)](https://jitpack.io/#Jayze24/ColorfulTextView)
```gradle
dependencies {
    implementation 'com.github.Jayze24:ColorfulTextView:x.y.z'
}
```
## Usage
Put in your xml layout. Just add a gradient color to `app:colorful_colors` and put a duration (100 to 1000) into `app:colorful_duration`(The lower the number, the faster it moves.) You can also select the direction `app:colorful_direction` (right, left, up, down).
```xml
<space.jay.colorfultextview.ColorfulTextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Colorful Text Sample"
    android:textSize="24sp"
    android:textStyle="bold"
    app:colorful_colors="@array/colors_sample"
    app:colorful_duration="100"
    app:colorful_direction="down"/>
```
Add your color in res/values/colors.xml. for example :
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
	...
    <array name="colors_sample">
        <item>#FFBB86FC</item>
        <item>#FF6200EE</item>
        <item>#FF3700B3</item>
        <item>#FF018786</item>
        <item>#FF03DAC5</item>
    </array>
</resources>
```
Can be controlled programmatically. like this : 
```kotlin
YourColorfulTextView.setAnimation(TypeDirection.RIGHT, 1000, Color.BLUE, Color.GRAY, Color.GREEN, Color.CYAN)

YourColorfulTextView.animationStart()
YourColorfulTextView.animationResume()
YourColorfulTextView.animationPause()
YourColorfulTextView.animationRemove()
```

