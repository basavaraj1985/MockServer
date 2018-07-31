Universal Mock Server
=====================

  - Mock multiple servers ( front end / back end ) at a time
  
  - Mock any format of responses - json, xml, html, u-name-it.
 
  - Dynamically edit your responses. i.e. After you edit the mock responses, you dont need to restart server. The new response will be sent next time a request comes in.
    
    
## How to Mock Any Server:
Suppose I would like to test my Front end while back end is still under development. And suppose my front end depends on multiple back end servers.

Let b1, b2 be backend servers that my FE depends on. And b1 returns xml,  b2 returns json. And I have sample responses stored in f1.xml, f2.json files. And suppose b1 has request of the form http://$host/v1/params/city=cityName and b2 has request of the for http://$host/v1/WeatherAPI/getWeather/id=87998.

Then its all just configuration:
Edit config.properties ;

    v1/params/city=./conf/mockResponses/b1results.xml  
    WeatherAPI/getWeather=./conf/mockResponses/b2results.json
    api/path=file/path/to/response
    
And go on editing your response files to test your system under test. Note that you can mimick the conditions of timeout, invalid response, random delays etc.

## How to install and run:
   ```
    git clone git@github.com:basavaraj1985/MockServer.git
    cd MockServer
    mvn clean compile exec:java
   ```
