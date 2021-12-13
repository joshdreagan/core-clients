# CORE Clients

## Producer

__Plain text__

```
mvn spring-boot:run '-Dcore.destination.name=FOO' '-Damqphub.amqp10jms.remote-url=amqp://localhost:5672' '-Dcamel.component.amqp.username=user1' '-Dcamel.component.amqp.password=password'
```

__SSL__

```
mvn spring-boot:run '-Damqp.destination.name=FOO' '-Damqphub.amqp10jms.remote-url=amqps://localhost:5671?transport.verifyHost=false' '-Dcamel.component.amqp.username=user1' '-Dcamel.component.amqp.password=password' '-Djavax.net.ssl.trustStore=/home/user1/amqp-clients/client.ts' '-Djavax.net.ssl.trustStorePassword=password'
```

## Consumer

__Plain text__

```
mvn spring-boot:run '-Damqp.destination.name=FOO' '-Damqphub.amqp10jms.remote-url=amqp://localhost:5672' '-Dcamel.component.amqp.username=user1' '-Dcamel.component.amqp.password=password'
```

__SSL__

```
mvn spring-boot:run '-Damqp.destination.name=FOO' '-Damqphub.amqp10jms.remote-url=amqps://localhost:5671?transport.verifyHost=false' '-Dcamel.component.amqp.username=user1' '-Dcamel.component.amqp.password=password' '-Djavax.net.ssl.trustStore=/home/user1/amqp-clients/client.ts' '-Djavax.net.ssl.trustStorePassword=password'
```
