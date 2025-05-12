ui = @@VAULT_UI@@
cluster_addr  = "https://0.0.0.0:8201"
api_addr      = "@@VAULT_API_ADDRESS@@"
disable_mlock = @@VAULT_DISABLE_MLOCK@@

storage "mysql" {
  username = "@@VAULT_MYSQL_USERNAME@@"
  password = "@@VAULT_MYSQL_PASSWORD@@"
  database = "@@VAULT_MYSQL_DATABASE@@"
  address = "@@VAULT_MYSQL_ADDRESS@@"
  plaintext_connection_allowed = @@VAULT_MYSQL_PLAINTEXT_CONNECTION_ALLOWED@@
}

listener "unix" {
  address = "/var/run/vault/vault.sock"
  socket_mode = "644"
  socket_user = "100"
  socket_group = "1000"
}

listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_disable = @@VAULT_TLS_DISABLE@@
  tls_cert_file = "/vault/data/fullchain.pem"
  tls_key_file  = "/vault/data/privkey.pem"
}

telemetry {
  prometheus_retention_time = "30s"
  disable_hostname = true
  unauthenticated_metrics_access = true # /v1/sys/metrics
}