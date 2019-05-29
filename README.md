# Account manager

Description : Rest Service to manage transfers currency across accounts

### Code modules:

account-dao : Dao layer which holds the details of the Accounts and their balances
account-model : Encompasses the Request, Response and Core Entities
service : Dropwizard rest service for exposing APIs

1. RequestId passed in header for all POST requests. Internally stores this variable in 
ThreadLocal maintaining Idempotency acroos same requests [RequestContainer.class]
2. Reentrant lock is used for handling critical sections in concurrent scenarios [Account.class]

### Libraries used
1. Dropwizard - For REST 
2. Guice - Dependency Injection
3. Guava - Collection utilties
4. Lombok - Auto generate code
5. Junit - Test cases. Intentionally not used Mockito & PowerMockito Frameworks, to keep it lightweight

### Build steps:
Maven project. Uses dependency management to maintain the same versions across all child modules
Whole project is bundled into one single jar using shade plugin
JDK version is 1.8

```sh
git clone git@github.com:thrinath500/account-manager.git
cd account-manager
mvn clean install
```

### Run steps:

```sh
sudo mkdir -pm 775 /tmp/com/revolut/accountmanager/
java -cp service/target/service-1.0-SNAPSHOT.jar com.revolut.accountmanager.AccountManagerService server service/service.yml
```

### Tests

Service starts up with default test data. However you can load your test data again

1. Registering a account

```sh
curl -w"\n" -iX PUT \
  http://localhost:9000/v1/account/ \
  -H 'content-type: application/json' \
  -d '{"firstName" : "Thrinath2","lastName":"Dosapati2","address" : "Banglore"}'
```

2. Depositing to an account
```sh
curl -w"\n" -iX POST \
  http://localhost:9000/v1/account/1/deposit \
  -H 'content-type: application/json' \
  -H 'x-request-id: 123' \
  -d '{"currencyType" : "INR", "value" : 10000}'
```

3. Viewing an account
```sh
curl -w"\n" -iX GET http://localhost:9000/v1/account/1 \
  -H 'content-type: application/json' 
```

4. Withdrawing from an account
```sh
curl -w"\n" -iX POST \
  http://localhost:9000/v1/account/1/withdraw \
  -H 'x-request-id: abc-pqr' \
  -H 'content-type: application/json' \
  -d '{"currencyType" : "INR", "value" : 1000}'
```

5. Transferring to other account
```sh
curl -w"\n" -iX POST \
  http://localhost:9000/v1/transaction/transfer \
  -H 'content-type: application/json' \
  -H 'x-request-id: abc-pqr-xyz' \
  -d '{"currencyType" : "INR", "value" : 600, "fromAccountId" : 2, "toAccountId" : 1}'
```

6. Viewing account Statment
```sh
curl -w"\n" -iX GET \
  http://localhost:9000/v1/account/1/statement \
  -H 'content-type: application/json'  
```


