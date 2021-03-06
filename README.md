# Service Poller

This is a service poller implementation that is developed based on the tech stack Java, vertx, rxjava and builds with gradle.

Service Poller does _HTTP GET_ to each service urls periodically which is configurable 
in **ServicePollerJobConfiguration** (at every 10 seconds for now) and updates the status of each service and does the required updates in the database.

User interface for service poller can be loaded in localhost from **http://www.localhost:8080/index.html**

All the basic requirements are done along with some extra requirements.

Basic requirements:
- A user need to be able to add a new service with url, a name (DONE)
- Added services have to be kept when the server is restarted (DONE - Added services are kept in Sqlite DB)
- Present whenever a service was added and when the last change was made (DONE)

Extra requirements:
- We want full create/update/delete functionality for services. (DONE)
- The results from the poller are not automatically shown to the user
(you have to reload the page to see results) (DONE)
- Simultaneous writes sometimes causes strange behavior (DONE)
- Protect the poller from misbehaving services (for example answering really slowly) (DONE - Added timeout)
- URL Validation ("sdgf" is probably not a valid service) (DONE - Url validation is added to both client and server sides)
- We want to have informative and nice looking animations on add/remove services (NOT DONE)
- Multi-user support. Users should not see the services added by another user (NOT DONE)

IMPROVEMENTS THAT CAN BE DONE:
- mysql can be used in Docker, I have used Sqlite for this task, but would be better to use mysql in Docker.
- Use test database for testing, tests are directly interacting with actual database, but would be better to use a test database for testing purposes.
- Delete and Update buttons can be added to user interface to delete and update service urls.