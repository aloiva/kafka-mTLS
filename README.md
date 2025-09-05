## Kafka with Mutual TLS (mTLS)

This repository provides a `docker-compose.yml` file to set up a Kafka cluster with Mutual TLS (mTLS) authentication. mTLS enhances security by requiring both the client and server to authenticate themselves using digital certificates.

### Prerequisites

- Docker and Docker Compose installed.

### How to Use

1. **Setting up Certificates**

   Run the provided script `generateCerts.sh` to generate the required certificates for server and client.

   ```powershell
   ./generateCerts.ps1
   ```

   This script performs the following tasks:

   - Creates a self-signed root certificate authority (CA) certificate and private key.
   - Generates client and server key pairs, creates certificate signing requests (CSRs), and signs them with the root CA certificate.
   - Imports the root CA certificate and the signed certificates into respective keystores and truststores.

   The generated keystore and truststore files are stored in `secrets/client` and `secrets/server` respectively.

2. **Running Kafka Cluster**

   Use the following command to start the Kafka cluster with mTLS enabled:

   ```bash
   docker-compose up -d
   ```

   This command reads the `docker-compose.yml` file and sets up the required services including Zookeeper and Kafka.

   - Zookeeper is exposed on port 22181.
   - Kafka broker is exposed on port 29092 with mTLS authentication.

3. **Producer and Consumer Applications**

   The repository also contains sample producer and consumer applications in the `producer` and `consumer` directories. You can build and run them using the provided Docker Compose file.

4. **Viewing Certificates (Optional)**

   If you wish to inspect the certificates in the keystores, you can use the following keytool commands:

   ```powershell
   keytool -list -v -keystore secrets/client/client.keystore.jks -storepass changeit
   keytool -list -v -keystore secrets/server/server.keystore.jks -storepass changeit
   ```

### Note

- The provided Docker Compose file uses official Confluent images for Kafka and Zookeeper. You can customize the image versions by modifying the `CONF_VER` variable in the `docker-compose.yml`.

- The Docker Compose file also exposes ports for Zookeeper (22181) and Kafka (29092). Ensure these ports are available on your system.

- The `generateCerts.sh` script creates self-signed certificates for demonstration purposes. In a production environment, it's recommended to use certificates issued by a trusted Certificate Authority (CA).

- The scripts and configurations provided here are for educational and demonstration purposes. Please adjust them as needed for your specific environment and security requirements.

## Docker commands

Run this before interacting with cli:

```powershell
docker exec mtls-kafka kafka-acls --authorizer-properties zookeeper.connect=zookeeper:2181 --add --allow-principal "User:CN=myclient" --operation All --topic test-topic
```

```powershell
docker exec mtls-kafka kafka-acls --authorizer-properties zookeeper.connect=zookeeper:2181 --add --allow-principal "User:CN=myclient" --consumer --group group1 --topic test-topic
```

---

Create topic:

```powershell
docker exec mtls-kafka kafka-topics --create --topic test-topic --bootstrap-server kafka:29092 --command-config /etc/kafka/secrets/consumer.properties --partitions 1 --replication-factor 1
```

Produce message:

```powershell
echo "hello-mtls" | docker exec -i mtls-kafka kafka-console-producer --topic test-topic --bootstrap-server kafka:29092 --producer.config /etc/kafka/secrets/producer.properties
```

Consume message:

```powershell
docker exec mtls-kafka kafka-console-consumer --topic test-topic --bootstrap-server kafka:29092 --consumer.config /etc/kafka/secrets/consumer.properties --from-beginning --max-messages 1
```
