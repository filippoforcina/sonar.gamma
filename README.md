# sonar.gamma
Progetto campione in Java per analisi SonarQube

Progetto di esempio per l'utilizzo delle code RabbitMQ

Si ipotizza una coda di input:
- queue.manager.in=stock.in

Sulla quale sono in ascolto 3 diversi Listener

Sulla coda di input vengono caricati dei messaggi con una parametro di routing che specifica a quale dei 3 Listener il messaggio sia indirizzato.

Ogni Listener processa il messaggio e lo pubblica sulla relativa coda di output
- queue.manager.sizes=stock.sizes
- queue.manager.sizem=stock.sizem
- queue.manager.sizel=stock.sizel

Solo quando è di sua pertinenza, altrimenti lo rifiuta


## Docker


### Caso d'uso semplice

1) Eseguire un container Rabbit
```
docker run --name rabbitmq-xxx -d -p 15672:15672 -p 5672:5672 rabbitmq:management
```

2) Definire l'indirizzo ip della macchina dove è in esecuzione RabbitMQ
```
spring.rabbitmq.host=<ip-local>
```

3) Verificare il corretto funzionamento dell'applicazione:
```
set JAVA_HOME=C:\Environ\Java\azul-11.0.18
mvn clean compile
mvn clean spring-boot:run
curl -v localhost:8080
```

4) Generare l'immagine docker attraverso il Dockerfile
```
mvn clean package
docker build -t queuemanager-docker:alfa .
```

5) Istanziare un container della immagine creata
```
docker run --name queuemanager-xxx -d -p 8080:8080 queuemanager-docker:alfa
```


### Caso d'uso con mappatura tramite Link

1) Definiamo un container di Rabbit
```
docker run --name rabbitmq-beta -d -p 15672:15672 -p 5672:5672 -v C:\Cache\Docker\volume\rabbit\data:/var/lib/rabbitmq -v C:\Cache\Docker\volume\rabbit\log:/var/log/rabbitmq rabbitmq:management
```

2) Utilizziamo il name del container "rabbitmq-beta" sul file di properites
```
spring.rabbitmq.host=rabbitmq-beta
```

3) Generare l'immagine docker attraverso il Dockerfile
```
set JAVA_HOME=C:\Environ\Java\azul-11.0.18
mvn clean package
docker build -t queuemanager-docker:beta .
```

4) Istanziare un container della immagine creata
```
docker run --name queuemanager-beta --link rabbitmq-beta -d -p 8080:8080 queuemanager-docker:beta
```


### Caso d'uso con mappatura tramite Network

1) Definiamo una rete
```
docker network create queue-net
```

2) Utilizziamo il name del container "rabbitmq-gamma" sul file di properites
```
spring.rabbitmq.host=rabbitmq-gamma
```

3) Generare l'immagine docker attraverso il Dockerfile
```
set JAVA_HOME=C:\Environ\Java\azul-11.0.18
mvn clean package
docker build -t queuemanager-docker:gamma .
```

4) Definiamo le applicazioni sulla rete
```
docker run --name rabbitmq-gamma --network queue-net -d -p 15672:15672 -p 5672:5672 -v C:\Cache\Docker\volume\rabbit\data:/var/lib/rabbitmq -v C:\Cache\Docker\volume\rabbit\log:/var/log/rabbitmq rabbitmq:management
docker run --name queuemanager-gamma --network queue-net -d -p 8080:8080 queuemanager-docker:gamma
```


### Caso d'uso con Docker Compose

1) Generare l'immagine docker attraverso il Dockerfile
```
set JAVA_HOME=C:\Environ\Java\azul-11.0.18
mvn clean package
docker build -t queuemanager-docker:delta .
```

2) Generare i container attraverso il Docker Compose
```
docker compose up -d
```
