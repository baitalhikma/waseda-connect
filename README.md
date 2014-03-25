Waseda Connect
==============

Waseda Connect XMPP chat App for Android

This software is an XMPP based Android App, designed for internal-like organization usage. It was developed and tested using with a Tigase server. 

The software is based on the Xabber project (github.com/redsolution/xabber-android/). The project changelog can be found in "assets/Xabber changelog.txt"

Modification overview
In principle, the original software (Xabber) was redesigned to serve the purpose of internal communication for students of the Waseda University, Tokyo, Japan. The main functional changes in the new version of the software are:
- use of only one XMPP account and jabber domain (original related classes have been kept and slightly modified)
- use of predetermined xmpp server (tested with Tigase)
- added functionality for self registration of users (no user verification)
- added functionality for creating MUC rooms between selected user contacts
- auto-creation and joining of predetermined MUC global rooms acting as forum spaces and for sending notices to users
- added server-specific function for updating user profile information, uploading of avatar picture and changing of password
- separated view for viewing current and previous chats
- removed some unused resources and source code - graphic resources, translations, other  xmpp client settings etc.
- changes GUI related elements - colors, backgrounds, some gui elements etc.
- added simple server-specific checks for server online

To run this project:
1. install and setup your own XMPP server and database (eg. Tigase)
2. Setup server enable MUC capability and creation of MUC rooms by everyone. In Tigase this is done by adding the following to "etc/init.properties":
 --comp-class-1 = tigase.muc.MUCComponent
 --comp-name-1 = muc
 muc/muc-lock-new-room[B] = false
3. setup a localhost name of your choice, it will be used for username addresses ("@yourdomain")
3. import this java project to your IDE
4. generate your google maps key and import it to the manifest (l. 273)
5. add your server address to the strings file - res/values/strings.xml (item "server_address", l. 384), hostname (same place l.385) and server address in src/com/bai/android/data/account/AccountManager.java (l. 451)
6. edit global MUC room names in src/com/bai/android/data/extension/muc/MUCManager.java (l. 72-74, 304)
7. import and link google-play-services library to the project

To add points of interest to your map - edit res/raw/points_of_interest/xml
For using the AR Camera (non integral part of this App) you need to provide your own dictionary and tesseract trained data. See src/com/bai/android/data/arcamera/Constant.java
You can use the common tesseract trained data https://code.google.com/p/tesseract-ocr/downloads/list 