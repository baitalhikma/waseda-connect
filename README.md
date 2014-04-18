Waseda Connect XMPP chat App for Android

This software is an XMPP based Android App, designed for internal-like organization usage. It was developed and tested using with a Tigase server.

The software is based on the Xabber project (github.com/redsolution/xabber-android/). The project changelog can be found in "assets/Xabber changelog.txt"

Modification overview In principle, the original software (Xabber) was redesigned to serve the purpose of internal communication for students of the Waseda University, Tokyo, Japan. The main functional changes in the new version of the software are:

use of only one XMPP account and jabber domain (original related classes have been kept and slightly modified)
use of predetermined xmpp server (tested with Tigase)
added functionality for self registration of users (no user verification)
added functionality for creating MUC rooms between selected user contacts
auto-creation and joining of predetermined MUC global rooms acting as forum spaces and for sending notices to users
added server-specific function for updating user profile information, uploading of avatar picture and changing of password
separated view for viewing current and previous chats
removed some unused resources and source code - graphic resources, translations, other xmpp client settings etc.
changes GUI related elements - colors, backgrounds, some gui elements etc.
added simple server-specific checks for server online
To run this project: 
1. install and setup your own XMPP server and database (eg. Tigase) 
2. setup server, enable MUC capability and creation of MUC rooms by everyone. In Tigase this is done by adding the following to "etc/init.properties": 
 --comp-class-1 = tigase.muc.MUCComponent 
 --comp-name-1 = muc muc/muc-lock-new-room[B] = false 
3. setup a localhost name of your choice, it will be used for username addresses ("@yourdomain") 
4. import this java project to your IDE 
5. generate your google maps key and import it to the manifest (l. 273) 
6. add your server address to the strings file - res/values/strings.xml (item "server_address", ~l. 384), hostname (same place ~l.385) and server address in src/com/bai/android/data/account/AccountManager.java (~l. 451)
7. edit global MUC room names in src/com/bai/android/data/extension/muc/MUCManager.java (l. 72-74) 
8. import and link google-play-services library to the project

To get content for the maps function OtherActivity.java (non integral part of the xmpp related functionality of this program) you need your own data provider. The current example application uses services on the
same server, where the xmpp server resides. One service provides a db version information and the second returns json formatted map data. See lines 479 &
480 in src/com/bai/android/ui/OtherActivity.java. An example simple authenticaiton is set in com/bai/android/data/conneciton/MapLocationsConnection l 79.
For database schema information see src/com/bai/android/data/connection/MapDataAdapter, lines 37 - 43.

The AR Camera is also a non integral part of the program and is not a xmpp related functionality. It is designed for simple japanese to english translations
by processing images from teh camera, using the tesseract library. If you want to use this functionliaty you need to provide a dictionary and tesseract trained 
data. See src/com/bai/android/data/arcamera/Constant.java 
The dictionary needs to have a jap_eng tables with the following schema:
word (varchar), reading(varchar), speech_part(varchar), meaning(varchar), best_guess(varchar) and freq(int)
You can use the common tesseract trained data https://code.google.com/p/tesseract-ocr/downloads/list
