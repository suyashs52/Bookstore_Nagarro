Demo Book Store. Useful for
1. Swagger
2. Testing Mockito, JUnitRunner
3. Testing Controller, Service, Dao
4. Async spring boot example
5. Double singleton
6. Rest API Maturity Model
7. Docker
8. DTO vs Entity


Run the application 
1. using ide
      go to DemoApplication.java and run as java application
2.  take jar from target folder
      java -jar demo-0.0.1-SNAPSHOT.jar
3. change packaging to war and deploy on web server
4. deploy on docker

API doc 
1. http://localhost:8080/swagger-ui.html

Database
1. http://localhost:8080/h2/
jdbc url: jdbc:h2:file:C:/temp/test
username: root
password:


URL

1. for getting all book records
     a. http://localhost:8080/api/v1/books
adding not valid url gives you custom erro like
  http://localhost:8080/api/v1/booksdd 
  {
    "status": "NOT_FOUND",
    "message": "No handler found for GET /api/v1/booksdd",
    "errors": [
        "No handler found for GET /api/v1/booksdd"
    ]
  }
creating new book 
post 
  http://localhost:8080/api/v1/books
 param
  {
    "isbn": "ISBN 978-0-596-52068-9",
    "title": "provident",
    "author": "author1 provident",
    "price": 1
     
  }
for invalid request you get custom error message

for searching 
 post 
  http://localhost:8080/api/v1/books/search 
 param
 [
    {
        "id": 2,
        "isbn": "ISBN 978-0-596-52068-8",
        "title": "provident",
        "author": "author2",
        "price": 1,
        "copies": 0
    }
]

for more urls please refer swagger doc / look into code

  

deploy on production
Docker
Run maven command - clean install
Open the terminal and start the docker
docker image build -t demo-0.0.1-SNAPSHOT
docker container run --name demo -p 8080:8080 -d demo-0.0.1-SNAPSHOT

using cmd
java -jar demo-0.0.1-SNAPSHOT.jar

tomcat
change packaging to war

Documentation url is
http://localhost:8080/swagger-ui.html#
 

 
