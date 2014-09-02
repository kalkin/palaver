#Palaver

A Modern XMPP Chat Client

## Development 
You need maven for building the project.

### Maven commands

`com.zenjava.javafx-maven-plugin` is used for running and building executables. 

* `mvn clean compile jfx:run` - Run the Application
* `mvn clean compile jfx:native` - Build standalone native packages (produces exe,rpm,deb,dmg)


## Features
- Notifications-Support
- Sync messages between devices (XEP-0280)
- Basic MUS Support


## Help
### Muc
- Room Bookmarks are shown in the contacts list. 
- Adding a conference room instead of a contact in contact list joins the room and creates a bookmark.
