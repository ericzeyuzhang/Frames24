# Frames24

A full-stack school project from UCI CS122B: Projects in Databases and Web Applications. 

In this project, I developed a website providing movie browsing, searching and purchasing services. I give it the name ***Frames24*** because the earliest movie has frame rate 24fps. 

The website is available for access at: [frames24.cc](frames24.cc).

## Server Architecture

<img src="https://user-images.githubusercontent.com/52809000/88956766-b429a200-d252-11ea-835a-10081b526d75.png" alt="image" style="zoom:50%;" />

The server consists of three Ubuntu instances hosted by AWS EC2. 

Instance 0 hosts an Apache HTTP server. It listen to HTTP request at port 80 and evenly redirect the request to one of the Tomcat servers (load factor 1:1). 

Both instance 1 and instance 2 host a Tomcat server and a MySQL server. Two same .war artifacts are deployed on both Tomcat servers, respectively. The MySQL is set to source/replica mode with source server running on instance 1 and replica one running on instance 2.

## Performance Test

JMeter is used to test the performance of *movie-list* API on different server configurations and different concurrency levels. 

#### Server configuration

1. Single server

   A single Tomcat server running at instance 0, accessible via port 8080. 

2. Two servers (as the graph illustrates)

   Two Tomcat servers running on instance 1 and instance 2, accessible via port 80, instance 0. Traffic is balanced by instance 0. 

#### Concurrency level

Simulated by JMeter thread group, with # of threads set to: 1, 5, 20, 40.

#### Query parameters

| Name     | Value    | Description                                                  |
| -------- | -------- | ------------------------------------------------------------ |
| title    | ${title} | Extracted from CSV file which consists of 2600 possible user inputs. |
| page     | 1        | Get the first page of search result.                         |
| page_cap | 50       | 50 results per page.                                         |
| sorting  | rating   | Sort by movie ratings.                                       |
| order    | desc     | Highest rating first.                                        |

### Test results

<img src="https://user-images.githubusercontent.com/52809000/88957722-31a1e200-d254-11ea-8712-45e53f2716c6.png" alt="image" style="zoom:50%;" />

<img src="/Users/zeyu/Library/Application Support/typora-user-images/image-20200730110310308.png" alt="image-20200730110310308" style="zoom:50%;" />

