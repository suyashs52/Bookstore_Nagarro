Run the application 
1.) using ide
      go to DemoApplication.java and run as java application
2.)  take jar from target folder
      java -jar demo-0.0.1-SNAPSHOT.jar
3.) change packaging to war and deploy on web server
4.) deploy on docker

API doc 
http://localhost:8080/swagger-ui.html

Database
http://localhost:8080/h2/
jdbc url: jdbc:h2:file:C:/temp/test
username: root
password:


URL

for getting all book records
  http://localhost:8080/api/v1/books
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


functionality 
•	Add a Book to the store -done
•	Search book based on ISBN/Title/Author -done
•	Search media coverage about a book, given its ISBN -done
•	Buy a book -done

•	Code quality
 o	Error handling -done
 o	Testability -done (java class BookControllerMockStandaloneTest.java)
 o	Coding standards -done
 o	Efficient use of the Java 8(or above) language -done (used paralle stream and filters)

•	Technology/library choices -done (spring boot and open source jar)

•	Unit Tests -done
•	Thread safety -done (fork join pool, locking, executer service)
•	Performance -done (async call on read operation)



Checklist
1.	Functional correctness  -tested done
2.	Solution is Thread safe -yes checked use lock
3.	Solution supports concurrent requests -yes async uses
4.	Exception/Error handling -yes custom error handling
5.	Usage of HTTP code -done use 2xx,4xx,5xx and custom error
6.	Followed Java coding guideline -done 
7.	Efficient use of the Java 8(or above) language -done uses stream , filter
8.	Testability -done use junit, mockito
9.	Deliverables are ready to be deployed to production -done created dockerfile.txt
10.	Unit Tests -done check test package
11.	Java doc -done using swaggers

some of the images are in image folder


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


feedback

1.	update book method is not implemented properly - no concurrency handled
		a.	done in buy a book and searching feature, I didn’t know this has to implement everywhere as it was demo project
2.	DTO and model should be separate 
		a.	At last moment I was doing documentation so added DTO  with model
3.	Concurrency not handled in overall assignment
		a.	Try to do in searching, buying, didn’t know have to implement everywhere
4.	Create book - concurrency not handled 
		a.	Try to do in searching, buying, Firstly I executed functionality, write test case,
			Concurrency I have added lastly so added this in some places only.
5.	search - unnecessary use of threading it will impact performance, Why method type is POST 
		a.	post used because input param I want was json format, but we can use GET also
		b.	tried to use 3 thread to show concurrency 
6.	Buy a book - why multiple users not allowed to buy a book at the same time, buy book should be by ISBN not by id. user doesn't know id 
		a.	I think from web user can buy it by clicking on button, so after clicking pass id for faster purchase.
		b.	Lock is used for safety when 1 book is remaining 
7.	media coverage - can be a different service for scalability, unnecessary use of completable future. can be simple, synchronization will downgrade performance, why there is a request body while we can pass ISBN only, It should be GET API
		a.	tried to implement double locking singleton pattern, if next time request come used list rather than taking data from external api
		b.	post api use because in future if search should also based on author / other attributes
8.	JUnit tests are not fully covered 
		a.	tried to write many test cases but needed to complete all functionality within lesser time so focus was to complete all functionality 
9.	Transaction management not used properly
		a.	Jpa default transaction management used
		b.	Locking on purchasing book
10.	lack of logging
		a.	global exception handling I used logging , except this default query logging used


 
