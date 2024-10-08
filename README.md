
# Crypto Recommendation API w/ a draft UI

The following is the src code and everything used to create a Crypto Recommendation System that will provide endpoints and view to the user  where he will be able to see the available cryptos sorted in a descending order by the normalized range, view a specific crypto's min/max/oldest price/newest price and check highest normalized range for a specific date if available.

## Tech Stack

#### Implementation

- **Spring Boot 3.3.2**
- **Java 17**
- **Redis**
- **Thymeleaf for a basic UI**
- **Swagger OpenAPI 3 for API Description**
- **Apache Common CSV to read the CSV files**

#### Testing

- **SpringBootTest**
- **WebMvcTest**
- **Mockito**

#### Metrics

- **Spring Actuator for reading Redis metrics to ensure caching and health status**

#### Deployment

- **Docker for containerization**
- **K3S Cluster for orchestration (w/ Traefik & Let's Encrypt)**
- **or locally using IntelliJ's Spring Boot runtime environment**


## API Reference

#### Get Desc List of All Cryptos by Normalized Range

```http
  GET /api/cryptos/normalized-range
```
#### Proposed Response
| Body Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `crypto` | `string` | the Crypto symbol e.g. BTC  |
| `normalizedRange` | `Double` | A decimal value e.g. 0.75 |

#### Get Specific Crypto Stats

```http
  GET /api/cryptos/{$symbol}/stats
```
#### Request
| Path Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `symbol`      | `string` | the Crypto symbol e.g. BTC,  **Required** |

#### Proposed Response
| Body Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `symbol` | `string` | the Crypto symbol e.g. BTC  |
| `oldestPrice` | `Double` | A decimal value e.g. 5.1 |
| `newestPrice` | `Double` | A decimal value e.g. 5.1  |
| `maxPrice` | `Double` | A decimal value e.g. 5.1  |
| `normalizedRange` | `Double` | A decimal value e.g. 0.75 |


#### Get the Crypto with Highest Normalized Range for a Specific Date

```http
  GET /api/cryptos/normalized-range/{$date}
```

#### Request
| Path Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `date`      | `string` | Date of type "2022-01-01",  **Required** |

#### Proposed Response
| Body Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `crypto` | `string` | the Crypto symbol e.g. BTC  |
| `normalizedRange` | `Double` | A decimal value e.g. 0.75 |

## Deployment

#### Requirements:

- Redis server

If you are using Windows 10/11 make sure WSL (Windows Subsystem for Linux) is enabled and run this:

```sh
curl -fsSL https://packages.redis.io/gpg | sudo gpg --dearmor -o /usr/share/keyrings/redis-archive-keyring.gpg

echo "deb [signed-by=/usr/share/keyrings/redis-archive-keyring.gpg] https://packages.redis.io/deb $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/redis.list

sudo apt-get update
sudo apt-get install redis
```
and then this:
```sh
sudo service redis-server start
```

To deploy clone this code to your IDE and run it in a SpringBoot runtime environment

Otherwise you can always use the deployed API with the embedded UI to test it, from the following link:

```bash
https://cryptorec.rndv-testing.xyz/
```


## Documentation

The backend consists mainly of this tree structure:

```
src
 ├── main
 │   ├── java
 │   │   └── com.xm.cryptorec.cryptorec
 │   │       ├── config
 │   │       ├── controller
 │   │       ├── dto
 │   │       ├── exception
 │   │       ├── filter
 │   │       ├── interfaces
 │   │       ├── model
 │   │       └── service
 │   ├── resources
 │   │   ├── data
 │   │   ├── static
 │   │   ├── templates
 │   │   └── application.properties
 └── test
     ├── java
     │   └── com.xm.cryptorec.cryptorec
     │       ├── controller
     │       └── service
```
where in order to breakdown the main parts of the backend, we have the:
- config (where it includes application configurations that need to be started with the application e.g. Redis)
- controller (where contains the REST API controllers that is the interface where forms and routes the requests to our application or to external APIs)
- dto (it is data transfer objects that form an object the way we need it to transfer specific information)
- exception (contains specific exception handlers/classes tailored to our needs)
- filter (which is responsible for the rate limit filtering and could include more filtering implementations)
- interfaces (where contains various interface objects)
- model (consists of the objects that transfer back and forth the data to our storage)
- service (the services that contain the logic and is responsible for caching)
- test/ (there you will find the necessary unit tests for each function and e2e tests to ensure integration)


## Further Information

**Docker image can be found here: https://hub.docker.com/repository/docker/pavloskkr/cryptorec**

-------

**Included in the email you will find the Postman collection with localhost endpoints but you can use the https://cryptorec.rndv-testing.xyz/ endpoints also to test.**

-------

**In this repository you will also find the .yaml files that have been used to make the deployment to my k3s cluster**

-------

## Relevant links 

```
https://github.com/pavloskkr/cryptorec - Source Code

https://cryptorec.rndv-testing.xyz/ - Main UI
https://cryptorec.rndv-testing.xyz/swagger-ui/index.html - Swagger UI
https://cryptorec.rndv-testing.xyz/actuator/metrics - Metrics
```