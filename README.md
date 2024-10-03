## Spring Bank - Fiat and Crypto Currency Exchange Application
**Academic Year**: 2022/23  
**Technologies Used**: Maven, Java, Docker, H2 in-memory Database

![Screenshot 2024-03-02 045120](https://github.com/IIS-APRSP-2022-2023/agilne-2022-23-draganovik/assets/15861333/46b3b7fc-8777-41c6-8086-1e77b362dad3)

### Project Overview:
This project focuses on developing a microservices-based application that facilitates the exchange of both **fiat currencies** and **cryptocurrencies**. The application architecture is built around microservices to ensure scalability, modularity, and ease of maintenance. The core functionalities include currency conversion, managing user roles, and handling financial transactions for fiat and crypto assets.

### Key Features:

1. **Naming Server (Eureka Server)**  
   Acts as the service registry for all microservices within the application. This enables each service to be easily discoverable and ensures smooth communication across the architecture.  
   *Port*: `8761`

2. **Users Service**  
   Manages users with roles such as `OWNER`, `ADMIN`, and `USER`, allowing role-based access control (RBAC) for the application's features. It handles user creation, updating, and deletion while interacting with the H2 database for persistence.  
   *Port*: `8770`

3. **Currency Exchange Service**  
   Maintains exchange rates for fiat currencies, including EUR, USD, GBP, CHF, and RSD. Provides users with access to current rates for conversion.  
   *Port*: `8000`

4. **Currency Conversion Service**  
   Handles currency conversion requests between fiat currencies. It checks the user's bank account for available funds, completes the exchange, and returns the updated balance and transaction status.  
   *Port*: `8100`

5. **Bank Account Service**  
   Manages user bank accounts, including balances for each supported fiat currency. Only users with the role `USER` can hold accounts, and admins handle account management.  
   *Port*: `8200`

6. **Crypto Wallet Service**  
   Similar to the bank account service but for cryptocurrencies. It tracks holdings in at least three crypto assets and ensures that each user’s wallet is linked to their bank account.  
   *Port*: `8300`

7. **Crypto Exchange Service**  
   Provides exchange rates between cryptocurrencies, supporting the conversion between the cryptocurrencies defined in the Crypto Wallet service.  
   *Port*: `8400`

8. **Crypto Conversion Service**  
   Handles requests to convert cryptocurrencies, ensuring users have sufficient funds in their crypto wallet before performing the transaction and updating their wallet with the new balance.  
   *Port*: `8500`

9. **Trade Service**  
   Enables the exchange between fiat and cryptocurrency. Users can buy or sell crypto, with transactions first converting any non-supported fiat currency into USD or EUR before completing the exchange.  
   *Port*: `8600`

10. **Transfer Service**  
    Allows users to transfer funds between their accounts or to another registered user's account. A 1% transfer fee is applied, and the transaction is only completed if the total, including the fee, does not exceed the user's balance.  
    *Port*: `8700`

11. **API Gateway**  
    Acts as the single entry point for all user requests, providing centralized routing and handling user authentication.  
    *Port*: `8765`

### Additional Features:
- **Feign Client Communication**: All inter-service communication uses Feign for simple and efficient REST communication.
- **Exception Handling**: Comprehensive error handling returns meaningful error messages and status codes to users.
- **Rate Limiting**: Users are limited to two requests every 45 seconds for specific services (currency conversion, crypto conversion, trade, and transfer).
- **Circuit Breaker**: Implemented to ensure service reliability and fallback mechanisms in case of failure.
- **Docker Integration**: Each microservice is Dockerized, and the entire application can be deployed using Docker Compose.

### Deployment:
The project is deployed using Docker, with all microservice images available on Docker Hub. The application can be fully launched through a `docker-compose.yaml` file for ease of setup.

---

This project demonstrates my ability to design and implement a **modular, scalable, and efficient** microservices architecture using modern technologies. By managing the complexities of currency and cryptocurrency exchange, user roles, and financial transactions, this application showcases my **attention to detail**, **problem-solving skills**, and proficiency in **Java** and **Docker**.


agilne-2022-23-draganovik created by GitHub Classroom
