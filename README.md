# Ticket Management System

This is a simple ticket management system that allows users to create, view, and delete tickets. The system is built using Spring Boot and gRPC.


### grpc-core

The `grpc-core` module contains the gRPC protobuf file (`*.proto`)

### grpc-server

The `grpc-server` module implements the server-side logic generated from the `grpc-core` module.This module includes integration tests that utilize the gRPC client to test the server's functionality. 

### grpc-client

The `grpc-client` module implements the client-side logic generated from the `grpc-core` module. It is a console application that makes calls to the gRPC server implemented in the `grpc-server` module. Upon completing the call to the server, the client application exits. But for my testing used postman / BloomRPC

## Use Case:

Give me detailed post man collection to test all this use cases.
system that allows users to create, view, and delete tickets

- Each user will have a unique email address.
- Max seats per section is 100.
- Each user will book only one ticket.
- Each booking will have a unique booking id.

Each user will have a unique email address.
Max seats per section is 100.
Each user will book only one ticket. In future can make the unique ID and make multiple booking from mail ID
Each booking will have a unique booking id.

## Getting Started

To run the ticket project:

* Clone the repository:

```
git clone 
```

* Navigate to the project directory:

```
cd spring-grpc-ticketbooking
```

* Build the project:

```
mvn clean install
```

* Run the gRPC server, it will listen on port `8085`:

```
java -jar grpc-server/target/grpc-server-1.3.0.jar
```

* Run the gRPC client:

```
java -jar grpc-client/target/grpc-client-1.3.0.jar
```

* Confirm client-server communication and expected data retrieval - check the log for `Call ended successfully` message.

* Alternatevly, execute gRPC call from Postman to `localhost:8085` and 

`createBooking`

```
{
  "from": "New York",
  "to": "Los Angeles",
  "price": 20,
  "user": {
    "first_name": "Karthikeyan",
    "last_name": "Somasundaram",
    "email": "ks@gmail.com"
  }
}

```

`getBookingBySection`

```
{
  "section":"A"
}
```