
# Magento Kubernetes Post-Deploy Strategy

##  Obiettivo

Garantire che il deploy di Magento 2 su Kubernetes:
- aggiorni correttamente `app/etc/config.php` nel pod live
- generi gli asset statici
- compili DI
- svuoti cache
- non lasci Job o pod residui

---

##  Flusso Completo

### 1. Esecuzione `setup:upgrade` nel pod live

```bash
kubectl get pods -n $WOPR_NAMESPACE -l app=$REPO_SLUG
kubectl exec -n $WOPR_NAMESPACE -t <pod-name> -- php bin/magento setup:upgrade
```

 Evita che `config.php` venga aggiornato solo nel Job e non nel pod live.

---

### 2. Job Kubernetes volatile per `post-deploy`

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: magento-post-deploy
  namespace: xpipe-cloud
spec:
  ttlSecondsAfterFinished: 120
  backoffLimit: 1
  template:
    spec:
      restartPolicy: Never
      containers:
      - name: magento-cli
        image: sindriaproject/xpipe-ecommerce:@@RELEASE_VERSION@@
        command:
          - sh
          - -c
          - |
            php bin/magento setup:upgrade &&             php bin/magento setup:static-content:deploy it_IT en_US -f &&             php bin/magento setup:di:compile &&             php bin/magento cache:flush
        envFrom:
        - configMapRef:
            name: xpipe-ecommerce-config
        - secretRef:
            name: xpipe-ecommerce-secret
        volumeMounts:
        - mountPath: /var/www/app/pub/static
          name: xpipe-ecommerce-static-volume
        - mountPath: /var/www/app/pub/media
          name: xpipe-ecommerce-media-volume
        - mountPath: /var/www/app/generated
          name: xpipe-ecommerce-generated-volume
        - mountPath: /var/www/app/var
          name: xpipe-ecommerce-var-volume
      volumes:
      - name: xpipe-ecommerce-static-volume
        persistentVolumeClaim:
          claimName: xpipe-ecommerce-static-pvc
      - name: xpipe-ecommerce-media-volume
        persistentVolumeClaim:
          claimName: xpipe-ecommerce-media-pvc
      - name: xpipe-ecommerce-generated-volume
        persistentVolumeClaim:
          claimName: xpipe-ecommerce-generated-pvc
      - name: xpipe-ecommerce-var-volume
        persistentVolumeClaim:
          claimName: xpipe-ecommerce-var-pvc
```

---

##  Best Practices

- `kubectl exec` nel pod applicativo per `setup:upgrade`
- usare Job Kubernetes per operazioni isolate e ripetibili
- TTL sui Job per non sprecare IP/pod
- mount coerente dei volumi
- step separati per logging e validazione in CI/CD

---

##  Risultato

Sistema Magento pronto e coerente con:
- configurazioni aggiornate
- asset statici deployati
- prestazioni stabili
- log chiari in CI
