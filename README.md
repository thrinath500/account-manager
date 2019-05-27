# Account manager

Description : Rest Service to manage transfers currency across accounts

### Code modules:

account-dao : Dao layer which holds the details of the Accounts and their balances
account-model : Encompasses the Request, Response and Core Entities
service : Dropwizard rest service for exposing APIs

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

1. For Registering a account

```sh
curl -X PUT \
  http://localhost:9000/account/ \
  -H 'content-type: application/json' \
  -d '{"firstName" : "Thrinath2","lastName":"Dosapati2","address" : "Banglore"}'
```

2. For Deposting to an account
```sh
curl -X POST \
  http://localhost:9000/account/1/deposit \
  -H 'content-type: application/json' \
  -d '{"currencyType" : "INR", "value" : 10000}'
```

3. For Viewing an account
```sh
curl -X GET http://localhost:9000/account/1 \
  -H 'content-type: application/json' 
```

4. For Withdrawing from an account
```sh
curl -X POST \
  http://localhost:9000/account/1/withdraw \
  -H 'content-type: application/json' \
  -d '{"currencyType" : "INR", "value" : 1000}'
```

5. For Transfering to other account
```sh
curl -X POST \
  http://localhost:9000/transaction/transfer \
  -H 'content-type: application/json' \
  -d '{"currencyType" : "INR", "value" : 600, "fromAccountId" : 2, "toAccountId" : 1}'
```

