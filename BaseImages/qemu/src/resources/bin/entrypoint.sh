#!/bin/bash
set -e

echo "[XPIPE-KVM] Starting xpipe-kvm entrypoint..."
echo "[XPIPE-KVM] Checking for net1 interface (macvlan from Multus)..."

# Aspettiamo net1 (creato da Multus CNI)
while [ ! -d /sys/class/net/net1 ]; do
  echo "[XPIPE-KVM] Waiting for net1..."
  sleep 1
done

# Porta su l'interfaccia net1
ip link set net1 up

# Settiamo le variabili per indicare a run-qemu.sh di usare il bridge/mode corretto
#export NETWORK_TYPE="bridge"
#export NETWORK_IFACE="net1"

echo "[XPIPE-KVM] Launching standard qemux start script with NETWORK_TYPE=${NETWORK_TYPE} on ${NETWORK_IFACE}"

# Avvia lo entry.sh originale che gestisce tutto
#exec /usr/local/bin/start.sh
/usr/bin/tini -s /run/entry.sh
