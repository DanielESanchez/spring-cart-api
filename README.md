
# REST API JAVA SPRING 6 AND MONGODB

REST API to manage products from a store, shopping cart and authentication with test maid using JUnit5 and Mockito. It is based on roles, "ADMIN" and "USER". You can see full documentation after building the project and running the project in your local machine. The instruction to build and run the docker for this project are described bellow.




## Run Locally

**IMPORTANT:** Before to start, you must download and install docker (if you have not done it before) from [Go to Docker's main page](https://www.docker.com/products/docker-desktop/)

Clone the project

```bash
  git clone https://github.com/DanielESanchez/spring-cart-api
```

Go to the project directory

```bash
  cd spring-cart-api
```

Install MongoDB docker

```bash
  docker pull mongo
```

Run MongoDB Docker

```bash
  docker run -d --name mongo-docker -p 27017:27017 mongo
```

Build the docker for this project

```bash
  docker build -t danielsanchez/spring-shopping-cart-api .
```

Run the docker. You must add the environment variable token.login.key and change "TOKEN" with your own key for JWT. Please use a strong key and only letter and numbers, otherwise the app will fail to register or login users.

```bash
  docker run --name spring-shopping-cart-api -p8080:8080 -d -e token.login.key=TOKEN danielsanchez/spring-shopping-cart-api
```

After that you can go to the [Swagger Documentation Page for thew Project](http://localhost:8080/swagger-ui/index.html#/) to see the full documentation of all the endpoints and test all of them.

Also you can go to my [Github repository](https://github.com/DanielESanchez/spring-cart-frontend) to see, clone and test the frontend build with angular to see a demonstration of this endpoint


