[![Build Status](https://www.bitrise.io/app/aca04570e5f56b1e.svg?token=34bj1bWbS_b1OJxUuvIPvw&branch=master)](https://www.bitrise.io/app/aca04570e5f56b1e)

# BandUp Android Client

## Interacting with a server instance locally
The server that the BandUp app communicates with is [here](http://www.github.com/BandUp/band-up-server).

To communicate with a server instance locally, open [this (app/src/main/res/values/connection_strings.xml)](https://github.com/BandUp/band-up-android/blob/master/app/src/main/res/values/connection_strings.xml) file, and change the variable ```api_address``` to a URI similar to this: ```http://192.168.1.5:3000```.

This value should **always** be changed before committing to the following address: ```https://band-up-server.herokuapp.com```

The IP address is the local IP address of the machine running the server. You can then communicate with the server using the Emulator or an Android device, if they are connected to the same Wi-Fi network.

**NOTE**: Do not add the forward slash at the end of the URI and make sure you are using HTTP, not HTTPS.
