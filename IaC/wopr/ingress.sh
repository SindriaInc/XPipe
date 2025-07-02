#!/usr/bin/env bash


# Latest version (idempotence check ok)
helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace \
  --set controller.kind=DaemonSet \
  --set controller.hostNetwork=true \
  --set controller.daemonset.useHostPort=true \
  --set controller.ingressClassResource.name=nginx \
  --set controller.ingressClassResource.controllerValue="k8s.io/ingress-nginx" \
  --set controller.ingressClass=nginx \
  --set controller.dnsPolicy=ClusterFirstWithHostNet \
  --set controller.service.enabled=false \
  --set controller.enableAnnotationValidations=false \
  --set controller.admissionWebhooks.enabled=false \
  --set controller.allowSnippetAnnotations=true \
  --set controller.config.enable-snippet-annotations="true" \
  --set controller.config.fastcgi-buffer-size="32k" \
  --set controller.config.proxy-buffer-size="128k" \
  --set controller.config.proxy-busy-buffers-size="256k" \
  --set controller.metrics.enabled=true


# History

# V1

#helm upgrade ingress-nginx ingress-nginx/ingress-nginx \
#  --install \
#  --namespace ingress-nginx \
#  --set controller.kind=DaemonSet \
#  --set controller.daemonset.useHostPort=true \
#  --set controller.hostPort.enabled=true \
#  --set controller.hostNetwork=true \
#  --set controller.ingressClassResource.name=nginx \
#  --set controller.ingressClassResource.controllerValue="k8s.io/ingress-nginx" \
#  --set controller.ingressClass=nginx

# V2

#helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
#  --namespace ingress-nginx --create-namespace \
#  --set controller.kind=DaemonSet \
#  --set controller.hostNetwork=true \
#  --set controller.daemonset.useHostPort=true \
#  --set controller.dnsPolicy=ClusterFirstWithHostNet \
#  --set controller.service.enabled=false

# V3

#helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
#  --namespace ingress-nginx --create-namespace \
#  --set controller.kind=DaemonSet \
#  --set controller.hostNetwork=true \
#  --set controller.daemonset.useHostPort=true \
#  --set controller.dnsPolicy=ClusterFirstWithHostNet \
#  --set controller.service.enabled=false \
#  --set controller.enableAnnotationValidations=false \
#  --set controller.admissionWebhooks.enabled=false \
#  --set controller.config.enable-snippet-annotations="true" \
#  --set controller.config.fastcgi-buffer-size="32k" \
#  --set controller.config.proxy-buffer-size="128k" \
#  --set controller.config.proxy-busy-buffers-size="256k"

# V4

#helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
#  --namespace ingress-nginx --create-namespace \
#  --set controller.kind=DaemonSet \
#  --set controller.hostNetwork=true \
#  --set controller.daemonset.useHostPort=true \
#  --set controller.dnsPolicy=ClusterFirstWithHostNet \
#  --set controller.service.enabled=false \
#  --set controller.enableAnnotationValidations=false \
#  --set controller.admissionWebhooks.enabled=false \
#  --set controller.config.enable-snippet-annotations="true" \
#  --set controller.config.fastcgi-buffer-size="32k" \
#  --set controller.config.proxy-buffer-size="128k" \
#  --set controller.config.proxy-busy-buffers-size="256k" \
#  --set controller.metrics.enabled=true

# V5

#helm upgrade --install ingress-nginx ingress-nginx/ingress-nginx \
#  --namespace ingress-nginx --create-namespace \
#  --set controller.kind=DaemonSet \
#  --set controller.hostNetwork=true \
#  --set controller.daemonset.useHostPort=true \
#  --set controller.ingressClassResource.name=nginx \
#  --set controller.ingressClassResource.controllerValue="k8s.io/ingress-nginx" \
#  --set controller.ingressClass=nginx \
#  --set controller.dnsPolicy=ClusterFirstWithHostNet \
#  --set controller.service.enabled=false \
#  --set controller.enableAnnotationValidations=false \
#  --set controller.admissionWebhooks.enabled=false \
#  --set controller.allowSnippetAnnotations=true \
#  --set controller.config.enable-snippet-annotations="true" \
#  --set controller.config.fastcgi-buffer-size="32k" \
#  --set controller.config.proxy-buffer-size="128k" \
#  --set controller.config.proxy-busy-buffers-size="256k" \
#  --set controller.metrics.enabled=true


