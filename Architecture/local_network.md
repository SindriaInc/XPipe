# Local Network

## Note
20 IP prodotti - collector adapter (feature verticali di prodotto) 20 ip Mysql e 20 ip Elastic

20 IP prodotti - portal - 20 ip Mysql e 20 ip Elastic

2 - 50 (prodotti)

20 Business Domain -  collector dominio (20 ip Mysql e 20 ip Elastic)

## Local network - 24 Bit 

### Specifiche:
- Net name: `vpc_xpipe`
- Subnet: `172.16.10.0/24`
- Gateway/Nat: `172.16.10.1/32`
- Broadcast: `172.16.10.255/32`

### Design ultimi 8 bit

0 net
1 gw

2-4 riservati per componenti di sistema

5 - 30 (Portal di prodotto)
40 - 60 (DB dei portal di prodotto)
70 - 90 (Elastic dei portal di prodotto)

91 - 99 riservati per test


100 - 130 (Collector di dominio)
140 - 180 (Db dei collector di dominio)
190 - 220 (Elastic dei collector di dominio)

Examples:
- Fnd 100/140/190 - 33140 (Bind MySql)
- Iam 101/141/191 - 33141 (Bind MySql)
- Pipelines 102/142/192 - 33142 (Bind MySql)

221 - 254 liberi (probabilmente per micro-servizi)

255 broadcast

## Inventory

### Fnd

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.100
DB_IP_ADDRESS=172.16.10.140
ELASTICSEARCH_IP_ADDRESS=172.16.10.190
```

### Iam

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.101
DB_IP_ADDRESS=172.16.10.141
ELASTICSEARCH_IP_ADDRESS=172.16.10.191
```

### Pipelines

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.102
DB_IP_ADDRESS=172.16.10.142
ELASTICSEARCH_IP_ADDRESS=172.16.10.192
```

### Lab

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.103
DB_IP_ADDRESS=172.16.10.143
ELASTICSEARCH_IP_ADDRESS=172.16.10.193
```

### Cmdb

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.104
DB_IP_ADDRESS=172.16.10.144
ELASTICSEARCH_IP_ADDRESS=172.16.10.194
```

### Monitoring

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.105
DB_IP_ADDRESS=172.16.10.145
ELASTICSEARCH_IP_ADDRESS=172.16.10.195
```

### Docs

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.106
DB_IP_ADDRESS=172.16.10.146
ELASTICSEARCH_IP_ADDRESS=172.16.10.196
```