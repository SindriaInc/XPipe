# OVH

## Creazione API key OVH certbot

Per aggiungere una API token è necessario accedere a:
https://api.ovh.com/createToken/

Dopodiché, per ogni dominio, è necessarrio aggiungere questi permessi:

| Verb| Endpoint |
| ------ | ------ |
| `GET` | `/domain/zone/` |
| `GET` | `/domain/zone/{dominio}/status` | 
| `GET` | `/domain/zone/{dominio}/record` | 
| `GET` | `/domain/zone/{dominio}/record/*` | 
| `POST` | `/domain/zone/{dominio}/record` | 
| `POST` | `/domain/zone/{dominio}/refresh` | 
| `DELETE` | `/domain/zone/{dominio}/record/*` | 

**Attenzione: ci sono problemi di autenticazione se si usano gli alias di gmail con il carattere `+`, in questo caso conviene utilizzare il nic-handle**

Per cancellare i token generati bisogna autenticarsi su su https://eu.api.ovh.com/console/ -- Bisogna poi fare a mano, guardando `/me/api/application` e `/me/api/credential`

A questo punto bisogna generare una stringa in questo formato da inserire nelle variabili CI con il nome `OVH_CONF` (tipo file):
```
dns_ovh_endpoint = ovh-eu
dns_ovh_application_key = application_key
dns_ovh_application_secret = application_secret
dns_ovh_consumer_key = consumer_key
```