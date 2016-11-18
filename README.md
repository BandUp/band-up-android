[![Build Status](https://www.bitrise.io/app/aca04570e5f56b1e.svg?token=34bj1bWbS_b1OJxUuvIPvw&branch=master)](https://www.bitrise.io/app/aca04570e5f56b1e)

# BandUp Android Client

## Interacting with a server instance locally
The server that the BandUp app communicates with is [here](http://www.github.com/BandUp/band-up-server).

To communicate with a server instance locally, open [this (app/src/main/res/values/connection_strings.xml)](https://github.com/BandUp/band-up-android/blob/master/app/src/main/res/values/connection_strings.xml) file, and change the variable ```api_address``` to a URI similar to this: ```http://192.168.1.5:3000```.

This value should **always** be changed before committing to the following address: ```https://band-up-server.herokuapp.com```

The IP address is the local IP address of the machine running the server. You can then communicate with the server using the Emulator or an Android device, if they are connected to the same Wi-Fi network.

**NOTE**: Do not add the forward slash at the end of the URI and make sure you are using HTTP, not HTTPS.


## Running UI tests with Espresso
To run the UI tests, you will need to create a new Run/Debug Configuration.

Open the Run/Debug Configurations window, and click the plus button in the top left corner. Select ```Android Tests```. Select the module ```app``` and set the instrumentation runner to ```android.support.test.runner.AndroidJUnitRunner```

Make sure you are building the ```debug``` variant of the app.

It is best to disable all animations on the device you are testing on, because the animations can interfere with Espresso and make tests fail.

To do that:
- Go into ```Settings -> About (emulated) device```
- Tap the ```Build number``` seven times.
- Go back one step and tap on ```Developer options```.
- Scroll down until you see ```Window animation scale```, ```Transition animation scale``` and ```Animator duration scale```.
- Change them all to ```Animation off```.
