# Grails RestRPC Plugin

In traditional REST, you can one have 4 REST calls per service/class because there ARE only 4 REST requests: GET, PUT, POST, DELETE. Each of these can only be paired with ONE method in your class creating a ONE-TO-ONE relationship with the request method. RESTRPC is an interceptor api that allows the user to associate more than one method/function per request method while still being compliant with the REST standard thus creating a ONE-TO-MANY pairing.

## Source

Source code can be found at https://github.com/orubel/restrpc

## Issues

Issues can be found at https://github.com/orubel/restrpc/issues

## Feedback

Please provide feedback through the plugins [main author](https://github.com/orubel)

## Implementation

To add RestRPC annotations to your controller, merely add the service to your controller:
```
def restRPCService
```

Then add an annotation to the method you wish to be called via an api with the request method you are going to call it through:
```
@RestRPC(request=RpcMethod.GET)
def show(Long id) { ... }
```

NOTE: Do not add annotations to methods that REDIRECT as this will throw an error; Obviosly this is bad form but to avoid alot of questions in the forums, this would be why you got that error.


With RestRPC, you can add as many GET, POST, PUT and DELETE methods as you want in your controller. As with REST, it is good form to make sure that you are matching the request method with a 'proper' function (ie DELETE request method with a 'delete' function). Naturally you can deviate from this (just as with REST) but I'm sure you have good reasons, right? :)

## Authenticate
If you need to authenticate your api from a shell for testing, use the following with your credentials:
```
curl --data "j_username=admin&j_password=admin" http://localhost:8080/<yourapp>/j_spring_security_check --cookie-jar cookies.txt
```

## API

**GET**
```
curl --verbose --request GET http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/JSON/1 --cookie-jar cookies.txt
curl --verbose --request GET http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/XML/1 --cookie-jar cookies.txt
```
or in your code using HTTPBuilders RESTClient (as an example)...
```
try{
  def restrpc = new RESTClient('http://localhost:8080/<yourapp>')
  def path = '/restrpc/<controller>/<action>/JSON/1'
  def resp = restrpc.get(path:path)
  def data = restrpc.data
}catch(HttpResponseException ex){
  hre.printStackTrace()
}
```

**POST** (accepts formats of 'XML' or 'JSON')
```
curl --verbose --request POST --header "Content-Type: application/json" -d '{fname: "Richard",lname:"Mozzarella"}' http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/JSON/1 --cookies-jar cookies.txt
curl --verbose --request POST --header "Content-Type: application/xml" -d '{fname:"Richard",lname:"Mozzarella"}' http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/XML/1 --cookies-jar cookies.txt
```
or in your code using HTTPBuilders RESTClient (as an example)...
```
try{
  def restrpc = new RESTClient('http://localhost:8080/<yourapp>')
  def path = '/restrpc/<controller>/<action>/JSON
  def resp = restrpc.post(path:path,body:[fname:'Richard',lname:'Mozzarella'])
  def data = restrpc.data
}catch(HttpResponseException ex){
  hre.printStackTrace()
}
```

**PUT** (accepts formats of 'XML' or 'JSON')
```
curl --verbose --request PUT --header "Content-Type: application/json" -d '{fname: "Richard",lname:"Mozzarella"}' http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/JSON/1 --cookies-jar cookies.txt
curl --verbose --request PUT --header "Content-Type: application/xml" -d '{fname:"Richard",lname:"Mozzarella"}' http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/XML/1 --cookies-jar cookies.txt
```
or in your code using HTTPBuilders RESTClient (as an example)...
```
try{
  def restrpc = new RESTClient('http://localhost:8080/<yourapp>')
  def path = '/restrpc/<controller>/<action>/JSON
  def resp = restrpc.put(path:path,body:[fname:'Richard',lname:'Mozzarella'])
  def data = restrpc.data
}catch(HttpResponseException ex){
  hre.printStackTrace()
}
```
**DELETE**
```
curl --verbose --request DELETE http://localhost:8080/<yourapp>/restrpc/<controller>/<action>/JSON/1 --cookies-jar cookies.txt
```
or in your code using HTTPBuilders RESTClient (as an example)...
```
try{
  def restrpc = new RESTClient('http://localhost:8080/<yourapp>')
  def path = '/restrpc/<controller>/<action>/JSON/1'
  def resp = restrpc.delete(path:path)
  def data = restrpc.data
}catch(HttpResponseException ex){
  hre.printStackTrace()
}
```

## Troubleshooting


The most common problem is forgetting to remove 'static allowedMethods' from your Controller. If you are having problems accessing your API, make sure you have removed this from your controller.

Also if you are unable to view the data and keep getting a 'view', making sure the URL does NOT have a **trailing slash**.


