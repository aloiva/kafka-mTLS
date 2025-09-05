Remove-Item -ErrorAction SilentlyContinue -Force ./certs/ca_root.key, ./certs/ca_root.crt, ./certs/clientCert.csr, ./certs/clientCert-ca-signed.crt, ./certs/serverCert.csr, ./certs/server-ca-signed.crt, ./certs/ca_root.srl
Remove-Item -ErrorAction SilentlyContinue -Force ./secrets/client/client.keystore.jks, ./secrets/client/client.truststore.jks
Remove-Item -ErrorAction SilentlyContinue -Force ./secrets/server/server.keystore.jks, ./secrets/server/server.truststore.jks
Remove-Item -ErrorAction SilentlyContinue -Force ./secrets/server.keystore.p12, ./secrets/server.truststore.p12, ./secrets/client.truststore.p12, ./secrets/client.keystore.p12
