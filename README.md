# Account manager

Description : Rest Service to manage transfers currency across accounts

### Code modules:

account-dao : Dao layer which holds the details of the Accounts and their balances
account-model : Encompasses the Request, Response and Core Entities
service : Dropwizard rest service for exposing APIs

1. RequestId passed in header for all POST requests. Internally stores this variable in 
ThreadLocal maintaining Idempotency acroos same requests [RequestContainer.class]
2. Reentrant lock is used for handling critical sections in concurrent scenarios [Account.class]

### Build steps:
Maven project. Uses dependncy management to maintain the same versions across all child modules

```sh
git clone git@github.com:thrinath500/account-manager.git
cd account-manager
mvn clean install
```

### Run steps:

```sh
java -cp service/target/service-1.0-SNAPSHOT.jar com.revolut.accountmanager.AccountManagerService server service/service.yml
```

### Tests

Service starts up with default test data. However you can load your test data again

1. Registering a account

```sh
curl -X PUT \
  http://localhost:9000/v1/account/ \
  -H 'content-type: application/json' \
  -d '{"firstName" : "Thrinath2","lastName":"Dosapati2","address" : "Banglore"}'
```

2. Deposting to an account
```sh
curl -X POST \
  http://localhost:9000/v1/account/1/deposit \
  -H 'content-type: application/json' \
  -H 'x-request-id: 123' \
  -d '{"currencyType" : "INR", "value" : 10000}'
```

3. Viewing an account
```sh
curl -X GET http://localhost:9000/v1/account/1 \
  -H 'content-type: application/json' 
```

4. Withdrawing from an account
```sh
curl -X POST \
  http://localhost:9000/v1/account/1/withdraw \
  -H 'x-request-id: abc-pqr' \
  -H 'content-type: application/json' \
  -d '{"currencyType" : "INR", "value" : 1000}'
```

5. Transfering to other account
```sh
curl -X POST \
  http://localhost:9000/v1/transaction/transfer \
  -H 'content-type: application/json' \
  -H 'x-request-id: abc-pqr-xyz' \
  -d '{"currencyType" : "INR", "value" : 600, "fromAccountId" : 2, "toAccountId" : 1}'
```

6. Viewing account Statment
```sh
curl -X GET \
  http://localhost:9000/v1/account/1/statement \
  -H 'content-type: application/json'  
```


