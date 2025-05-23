# 📘 Documento Tecnico Ufficiale — Architettura XPipe API Collector

## 1. Introduzione

**Nome componente:** API Collector  
**Tipo:** Componente modulare backend  
**Collocazione nel monorepo:** `{BusinessDomain}/Vx/Api/{NomeModulo}`  
**Scopo:** Esporre funzionalità di backend tramite API (REST, WebSocket, Unix Socket, ecc.) attraverso moduli plug&play, senza la necessità di microservizi esterni dedicati.  
**Modello architetturale:** Semi-monolitico modulare (ispirazione Magento-like), loosely coupled.

---

## 2. Contesto e motivazione

La crescente necessità di sviluppare:

- **Prototipi rapidi**
- **Soluzioni verticali per prodotti specifici (es. MyPharm, Books)**
- **Interfacce API con logica custom e bassa esigenza di scalabilità orizzontale**
- **Ambienti multi-tenant dove gli utenti finali possano definire API come servizio**

…ha portato alla definizione di un nuovo **pattern architetturale intermedio** tra i microservizi puri e l’approccio full-modular Magento.

---

## 3. Obiettivi del componente

- Esporre **moduli API riutilizzabili** tramite endpoint configurabili.
- Supportare diversi **tipi di trasporto API**: REST, WebSocket, UnixSocket, SOAP, ecc.
- Aggregare logicamente funzionalità API di uno o più domini funzionali.
- Consentire la **gestione multi-tenant / multi-cliente**, includendo policy e sicurezza centralizzate.

---

## 4. Design concettuale

```
Products/
└── XPipe/
    └── V1/
        └── Api/
            ├── core-user-authentication
            ├── cmdb-asset-publisher
            └── mypharm-rx-scanner
```

Ogni directory in `Api/` rappresenta un modulo con responsabilità unica, registrato all’API Collector tramite configurazione automatica o service registration.

---

## 5. Ruoli e responsabilità

| **Elemento**                | **Descrizione**                                                                 |
|----------------------------|---------------------------------------------------------------------------------|
| `ApiCollectorBootstrapper` | Avvia il componente, carica i moduli, espone gli endpoint                      |
| `ApiModule`                | Ogni singolo modulo che espone logica API. Isolato per responsabilità          |
| `RoutingMap`               | Mappa endpoint → controller → handler modulare                                 |
| `PolicyManager`            | Applica ACL, rate limit, audit, logging per ogni tenant                        |
| `TransportLayer`           | Definisce lo stack di trasporto (HTTP, WS, Unix) a livello di modulo o global |

---

## 6. Esempio di endpoint runtime

Esempio endpoint REST esposto da un modulo:

```
POST /xpipe/api/cmdb-asset-publisher/v1/assets
Headers: Authorization: Bearer <token>
Body: {
  "serial": "RXD93812",
  "owner": "Sindria srl"
}
```

Esempio endpoint su WebSocket:

```
WS /xpipe/api/mypharm-rx-scanner/v1/ws
Send: { "command": "scan", "payload": "EAN1234567890" }
```

---

## 7. Vantaggi dell’approccio

- 🔧 **Zero overhead microservizi**: logica localizzata, deployment centralizzato
- 🧩 **Plug&Play**: ogni modulo API segue pattern a responsabilità singola
- 🔒 **Sicurezza unificata**: gestione centralizzata di ACL/token/policy
- ⚡ **Prototipazione rapida**: perfetto per MVP e demo funzionali
- 🎯 **Multi-tenant ready**: ogni modulo può essere limitato o abilitato per singolo cliente/prodotto

---

## 8. Roadmap evolutiva

- [ ] Supporto CLI per bootstrap automatico dei moduli Api
- [ ] Logging e observability nativi (Prometheus exporter, API metrics)
- [ ] API dashboard grafica con Swagger UI o playground interattivo
- [ ] Interfaccia per attivare/disattivare moduli per tenant da backend Magento
- [ ] Integrazione completa con `xpipe-analytics` per tracciare eventi API

---

## 9. Possibili Use Case

| **Prodotto**  | **Use case**                                             |
|---------------|----------------------------------------------------------|
| XPipe         | Playground API low-cost per tenant                       |
| MyPharm       | API per scanner farmaco e registratore di cassa          |
| Books         | API per catalogazione ISBN e gestione utente             |
| XLearn        | API micro-learning, test runtime, stat online            |

---

## 10. Conclusione

L’API Collector rappresenta un’estensione naturale del paradigma XPipe: consente a sviluppatori e partner di definire logiche backend modulari, scalabili, manutenibili **senza rinunciare alla velocità di delivery**.

È la base ideale per consolidare XPipe come **piattaforma abilitante**, in grado di servire modelli PaaS e soluzioni verticali ad alto grado di personalizzazione.