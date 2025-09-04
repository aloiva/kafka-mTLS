# Requires: OpenSSL and Java (keytool) installed and available in PATH

$ErrorActionPreference = 'Stop'

# Set password
$pass = 'changeit'


# Ensure secrets/client and secrets/server directories exist
if (!(Test-Path -Path './secrets/client')) {
	New-Item -ItemType Directory -Path './secrets/client' | Out-Null
}
if (!(Test-Path -Path './secrets/server')) {
	New-Item -ItemType Directory -Path './secrets/server' | Out-Null
}

# Generate CA Root Certificate
openssl req -new -x509 -sha256 -keyout ./certs/ca_root.key -out ./certs/ca_root.crt -days 365 -subj "/CN=MyRootCA" -passin pass:$pass -passout pass:$pass

# Client Certificate
keytool -genkey -alias clientKey -dname "CN=myclient" -keystore ./secrets/client/client.keystore.jks -keyalg RSA -storepass $pass -keypass $pass

keytool -keystore ./secrets/client/client.keystore.jks -alias clientKey -certreq -file ./certs/clientCert.csr -storepass $pass -keypass $pass

openssl x509 -req -sha256 -CA ./certs/ca_root.crt -CAkey ./certs/ca_root.key -in ./certs/clientCert.csr -out ./certs/clientCert-ca-signed.crt -days 9999 -CAcreateserial -passin pass:$pass

keytool -keystore ./secrets/client/client.keystore.jks -alias CARoot -import -noprompt -file ./certs/ca_root.crt -storepass $pass -keypass $pass

keytool -keystore ./secrets/client/client.keystore.jks -alias clientKey -import -noprompt -file ./certs/clientCert-ca-signed.crt -storepass $pass -keypass $pass

keytool -keystore ./secrets/client/client.truststore.jks -alias CARoot -import -noprompt -file ./certs/ca_root.crt -storepass $pass -keypass $pass

# Server Certificate
keytool -genkey -alias serverKey -dname "CN=localhost" -keystore ./secrets/server/server.keystore.jks -keyalg RSA -storepass $pass -keypass $pass

keytool -keystore ./secrets/server/server.keystore.jks -alias serverKey -certreq -file ./certs/serverCert.csr -storepass $pass -keypass $pass

openssl x509 -req -sha256 -CA ./certs/ca_root.crt -CAkey ./certs/ca_root.key -in ./certs/serverCert.csr -out ./certs/server-ca-signed.crt -days 9999 -CAcreateserial -passin pass:$pass

keytool -keystore ./secrets/server/server.keystore.jks -alias CARoot -import -noprompt -file ./certs/ca_root.crt -storepass $pass -keypass $pass

keytool -keystore ./secrets/server/server.keystore.jks -alias serverKey -import -noprompt -file ./certs/server-ca-signed.crt -storepass $pass -keypass $pass

keytool -keystore ./secrets/server/server.truststore.jks -alias CARoot -import -noprompt -file ./certs/ca_root.crt -storepass $pass -keypass $pass

keytool -list -v -keystore ./secrets/client/client.keystore.jks -storepass $pass
keytool -list -v -keystore ./secrets/server/server.keystore.jks -storepass $pass
