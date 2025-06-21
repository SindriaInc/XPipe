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

## Inventory Products

### XPipe V1 Web Portal

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.5
DB_IP_ADDRESS=172.16.10.40
ELASTICSEARCH_IP_ADDRESS=172.16.10.70
```

#### Ports Binding:
- 5005 (Webserver)
- 33040 (MySql)

### XPipe V1 Api Collector

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.6
DB_IP_ADDRESS=172.16.10.41
ELASTICSEARCH_IP_ADDRESS=172.16.10.71
```

#### Ports Binding:
- 5006 (Webserver)
- 33041 (MySql)


## Inventory Business Domains

### Fnd

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.100
DB_IP_ADDRESS=172.16.10.140
ELASTICSEARCH_IP_ADDRESS=172.16.10.190
```

#### Ports Binding:
- 8080 (Webserver)
- 33140 (MySql)

### Iam

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.101
DB_IP_ADDRESS=172.16.10.141
ELASTICSEARCH_IP_ADDRESS=172.16.10.191
```

#### Ports Binding:
- 8081 (Webserver)
- 33141 (MySql)

### Pipelines

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.102
DB_IP_ADDRESS=172.16.10.142
ELASTICSEARCH_IP_ADDRESS=172.16.10.192
```

#### Ports Binding:
- 8082 (Webserver)
- 33142 (MySql)

### Lab

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.103
DB_IP_ADDRESS=172.16.10.143
ELASTICSEARCH_IP_ADDRESS=172.16.10.193
```

#### Ports Binding:
- 8083 (Webserver)
- 33143 (MySql)

### Cmdb

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.104
DB_IP_ADDRESS=172.16.10.144
ELASTICSEARCH_IP_ADDRESS=172.16.10.194
```

#### Ports Binding:
- 8084 (Webserver)
- 33144 (MySql)

### Monitoring

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.105
DB_IP_ADDRESS=172.16.10.145
ELASTICSEARCH_IP_ADDRESS=172.16.10.195
```

#### Ports Binding:
- 8085 (Webserver)
- 33145 (MySql)

### Docs

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.106
DB_IP_ADDRESS=172.16.10.146
ELASTICSEARCH_IP_ADDRESS=172.16.10.196
```

#### Ports Binding:
- 8086 (Webserver)
- 33146 (MySql)

### Lms

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.107
DB_IP_ADDRESS=172.16.10.147
ELASTICSEARCH_IP_ADDRESS=172.16.10.197
```

#### Ports Binding:
- 8087 (Webserver)
- 33147 (MySql)

### Security

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.108
DB_IP_ADDRESS=172.16.10.148
ELASTICSEARCH_IP_ADDRESS=172.16.10.198
```

#### Ports Binding:
- 8088 (Webserver)
- 33148 (MySql)

### Logging

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.109
DB_IP_ADDRESS=172.16.10.149
ELASTICSEARCH_IP_ADDRESS=172.16.10.199
```

#### Ports Binding:
- 8089 (Webserver)
- 33149 (MySql)

### Billing

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.110
DB_IP_ADDRESS=172.16.10.150
ELASTICSEARCH_IP_ADDRESS=172.16.10.200
```

#### Ports Binding:
- 8090 (Webserver)
- 33150 (MySql)

### Ai

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.111
DB_IP_ADDRESS=172.16.10.151
ELASTICSEARCH_IP_ADDRESS=172.16.10.201
```

#### Ports Binding:
- 8091 (Webserver)
- 33151 (MySql)

### Crm

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.112
DB_IP_ADDRESS=172.16.10.152
ELASTICSEARCH_IP_ADDRESS=172.16.10.202
```

#### Ports Binding:
- 8092 (Webserver)
- 33152 (MySql)

### Apm

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.113
DB_IP_ADDRESS=172.16.10.153
ELASTICSEARCH_IP_ADDRESS=172.16.10.203
```

#### Ports Binding:
- 8093 (Webserver)
- 33153 (MySql)

### Cms

```dotenv
NETWORK_SUBNET=172.16.10.0/24
APP_IP_ADDRESS=172.16.10.114
DB_IP_ADDRESS=172.16.10.154
ELASTICSEARCH_IP_ADDRESS=172.16.10.204
```

#### Ports Binding:
- 8094 (Webserver)
- 33154 (MySql)