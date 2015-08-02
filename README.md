#Palaver

A Modern XMPP Chat Client

## Features
- Notifications-Support
- Sync messages between devices (XEP-0280)
- Basic MUC Support

### Muc
- Room Bookmarks are shown in the contacts list. 
- Adding a conference room instead of a contact in contact list joins the room and creates a bookmark.


## Development 
You need maven for building the project.

### Please help me!
I am mostly working on this project im my spare time. There is a lot of work
still to be done. Please see if the *TODO* list for open issues.

### TODO 
* [ ] UI Overhaul of the Account Page (The account page is hidden at the moment
behind the sandwich button)
* [ ] Fix Carbon Messages
* [ ] Handle nick collisions and merging in MUC
* [ ] Encryption Support (Axolotl/OTR/OpenPGP)
* [ ] Add MemorizingTrustManager https://github.com/ge0rg/MemorizingTrustManager
* [ ] Reconnect on connection loss

### Maven commands

`com.zenjava.javafx-maven-plugin` is used for running and building executables. 

* `mvn clean compile jfx:run` - Run the Application
* `mvn clean compile jfx:native` - Build standalone native packages (produces exe,rpm,deb,dmg)

