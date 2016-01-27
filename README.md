# DevWars <img align="right" src="https://raw.githubusercontent.com/DevWars/DW_Bot/master/logo.png">

*Official Website for [Devwars.tv](http://devwars.tv/)* 

---
## Stack:
* [Spring MVC](http://spring.io/)
* [MySQL](https://www.mysql.com/)
* [Angular JS 1.X](https://angularjs.org/)

---

Instructions to Setup Locally

Please Rename `src/main/resources/devwars.properties.ex` to `devwars.properties` and fill with your own configuration.

## Front End:
``` bash
$ git clone git@github.com:DevWars/devwars.tv.git

$ cd devwars.tv
$ cd src/main/webapp
$ npm install
$ bower install
$ gulp serve
```

Ouput
``` bash
[BS] Local URL: http://localhost:81
[BS] External URL: http://localhost:81
[BS] Serving files from: ./
```

---
## Back End:
Go to the root of the repo
``` bash
  mvn clean install
  mvn compile war:war
```

You can plug that war into the container of your choice. (We use Tomcat)

---
## Project Structure

Overview

    ├── src                           - DevWars Source Files
    │   ├── main
    |   |   ├── java/com/bezman                 - Starting point for all Java files
    |   |   |   |──annotation                   - All annotations used in the DevWars Application
    |   |   |   |──cache                        - Cache classes
    |   |   |   |──controller                   - All API Controllers, each controller has many endpoints
    |   |   |   |──exception                    - Custom DevWars Exceptions
    |   |   |   |──hibernate
    |   |   |   |   |──expression               - Custom Hibernate expressions for Session Criteria
    |   |   |   |   |──interceptor              - Hibernate's Interceptos so we can do our own event system
    |   |   |   |   |──validation               - Custom Hibernate Bean validation
    |   |   |   |──init                         - Classes to setup things for initialization
    |   |   |   |──interceptor                  - Spring Http interceptors
    |   |   |   |──jackson                      - ObjectMapper
    |   |   |   |   |──serializer               - Custom Jackson serializers
    |   |   |   |──model                        - All DevWars models
    |   |   |   |──oauth                        - All OAuth Providers
    |   |   |   |──Reference                    - Some Reference items and Utility things
    |   |   |   |──request    
    |   |   |   |   |──model                    - Custom Spring Request Models
    |   |   |   |──resolver                     - Spring Parameter Resolvers
    |   |   |   |──service                      - Model Services and Utilities
    |   |   |   |──servlet                      - Miscellanious Servlets
    |   |   |   |──storage                      - File storage implementation (Currently Dropbox)
    |   |   |   |──validator                    - Bean validators
    |   |   ├── resources
    |   |   |   |──config/hibernate/mapping     - Hibernate Mapping files
    |   |   |   |──spring                       - Spring Startup XML Hell Files
    |   |   |     ├── devwars.properties.ex     - DevWars Configuration Example File
    |   |   |     ├── hibernate.cfg.xml         - Hibernate Configuration File
    |   |   |     ├── log4j2.xml                - Log4J Configuration File
    |   |   ├── webapp           
    |   |   |     ├── WEB-INF
    |   |   |     ├── app
    |   |   |     |     ├── components     - Angular components.
    |   |   |     |     ├── directives     - Angular directives.
    |   |   |     |     ├── pages          - Page views and controllers.
    |   |   |     |     ├── services       - Angular services.
    |   |   |     ├── assets
    |   |   |     |     ├── email
    
---
## License:
DevWars is licensed with a GPL V3 License
