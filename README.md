Based on fantastic library made by Amit Shekhar  https://github.com/amitshekhariitbhu/Android-Debug-Database

<img src=https://raw.githubusercontent.com/kosiarska/ObjectBoxDebugBrowser/master/assets/image.png >

Stay tuned for more updates


Use `debugCompile` so that it will only compile in your debug build and not in your release build.

That’s all, just start the application, you will see in the logcat an entry like follows :

### Getting address with toast, in case you missed the address log in logcat
As this library is auto-initialize, if you want to get the address log, add the following method and call (we have to do like this to avoid build error in release build as this library will not be included in the release build)
```java
    ObjectBoxBrowser.showDebugDBAddressLogToast(context)
```


* D/DebugDB: Open http://XXX.XXX.X.XXX:8080 in your browser
 

Now open the provided link in your browser.

Important:
- Your Android phone and laptop should be connected to the same Network (Wifi or LAN).
- If you are using it over usb, run `adb forward tcp:8080 tcp:8080`

Note      : If you want use different port other than 8080. 
            In the app build.gradle file under buildTypes do the following change

```groovy
debug {
    resValue("string", "PORT_NUMBER", "8081")
}
```

Include in your app build.gradle:
```
repositories {
    maven { url 'https://dl.bintray.com/pracaizlecenia/maven' }

}
dependencies {
    compile 'pl.michaltretowicz:debug-db:1.0.3'
}
```


### Working with emulator
- Android Default Emulator: Run the command in the terminal - `adb forward tcp:8080 tcp:8080` and open http://localhost:8080
- Genymotion Emulator: Enable bridge from configure virtual device (option available in genymotion)


### License
```
   Copyright (C) 2017 Michał Trętowicz
   Copyright (C) 2016 Amit Shekhar
   Copyright (C) 2011 Android Open Source Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

### Contributing to Android Debug Database
Just make pull request. You're in!
