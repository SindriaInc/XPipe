# Kubelet CAdvisor

TBD

# Install

- Create service account: `kubectl apply -f prometheus-kubelet-sa.yaml`
- Export static token: `kubectl -n kube-system get secret prometheus-kubelet-token -o jsonpath="{.data.token}" | base64 -d > ../../wopr-monitor/prometheus/k8s-sa-token.pem`