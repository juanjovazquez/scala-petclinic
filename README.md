# Scala PetClinic (sample)

Basic example based on the well-known [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) application written in Scala. 

## How to run

A docker compose file is provided in order to run the sample.

```bash
> mysql -h 127.0.0.1 -P 3306 -u root -p
> mysql -h 127.0.0.1 -u root -p < src/main/resources/db/mysql/schema.sql
> mysql petclinic -h 127.0.0.1 -u root -p < src/main/resources/db/mysql/data.sql
```

In case you are executing docker on docker machine, e.g. on Mac OS X, then execute:

```bash
> docker-machine start
> mysql -h $(docker-machine ip) -u root -p
> mysql -h $(docker-machine ip) -u root -p < src/main/resources/db/mysql/schema.sql
> mysql petclinic -h $(docker-machine ip) -u root -p < src/main/resources/db/mysql/data.sql
```

And on another terminal:

```bash
> sbt run
```
