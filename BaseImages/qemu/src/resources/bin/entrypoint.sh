#!/bin/bash
set -e

echo "[XPIPE-KVM] Entry point started..."

# Wait for Multus to create net1
while [ ! -d /sys/class/net/net1 ]; do
  echo "[XPIPE-KVM] Waiting for net1 interface..."
  sleep 1
done

ip link set net1 up

export NETWORK_TYPE="bridge"
export NETWORK_IFACE="net1"

echo "[XPIPE-KVM] Launching QEMU through original logic..."

exec /run/entry.sh
