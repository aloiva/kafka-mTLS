#!/bin/bash

openssl req -new -x509 -sha256 -keyout ca_root.key -out ca_root.crt -days 365 -subj "/CN=MyRootCA" -passin pass:changeit -passout pass:changeit

# Client Certificate
keytool -genkey  -alias clientKey  -dname "CN=myclient" -keystore client.keystore.jks -keyalg RSA  -storepass changeit  -keypass changeit

keytool -keystore client.keystore.jks -alias clientKey -certreq -file clientCert.csr -storepass changeit -keypass changeit

openssl x509 -req -sha256 -CA ca_root.crt -CAkey ca_root.key -in clientCert.csr -out clientCert-ca-signed.crt -days 9999 -CAcreateserial -passin pass:changeit

keytool -keystore client.keystore.jks -alias CARoot -import -noprompt -file ca_root.crt -storepass changeit -keypass changeit

keytool -keystore client.keystore.jks -alias clientKey -import -noprompt -file clientCert-ca-signed.crt -storepass changeit -keypass changeit

keytool -keystore client.truststore.jks -alias CARoot -import -noprompt -file ca_root.crt -storepass changeit -keypass changeit

# Server Certificate
keytool -genkey  -alias serverKey -dname "CN=localhost" -keystore server.keystore.jks -keyalg RSA  -storepass changeit  -keypass changeit

keytool -keystore server.keystore.jks -alias serverKey -certreq -file serverCert.csr -storepass changeit -keypass changeit

openssl x509 -req -sha256 -CA ca_root.crt -CAkey ca_root.key -in serverCert.csr -out server-ca-signed.crt -days 9999 -CAcreateserial -passin pass:changeit

keytool -keystore server.keystore.jks -alias CARoot -import -noprompt -file ca_root.crt -storepass changeit -keypass changeit

keytool -keystore server.keystore.jks -alias serverKey -import -noprompt -file server-ca-signed.crt -storepass changeit -keypass changeit

keytool -keystore server.truststore.jks -alias CARoot -import -noprompt -file ca_root.crt -storepass changeit -keypass changeit

keytool -list -v -keystore client.keystore.jks -storepass changeit

keytool -list -v -keystore server.keystore.jks -storepass changeit

cp client.*.jks secrets/client
cp server.*.jks secrets/server

