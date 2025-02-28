#!/usr/bin/env bash

# Cleanup lock files
rm -Rf /tmp/i3-sindria.*
rm -f /tmp/X*.lock
rm -Rf /tmp/.X11-unix/X*

# Resetting permission for .ssh
if [ -d ${SINDRIA_USER_HOME}/.ssh ]; then
    chmod 700 ${SINDRIA_USER_HOME}/.ssh
    chmod 600 ${SINDRIA_USER_HOME}/.ssh/*
    chmod 644 ${SINDRIA_USER_HOME}/.ssh/*.pub
fi

# Resetting permission for .aws
if [ -d ${SINDRIA_USER_HOME}/.aws ]; then
    chmod 700 ${SINDRIA_USER_HOME}/.aws
    chmod 600 ${SINDRIA_USER_HOME}/.aws/*
fi

# Resetting permission for .azure
if [ -d ${SINDRIA_USER_HOME}/.azure ]; then
    chmod 700 ${SINDRIA_USER_HOME}/.azure
    chmod 600 ${SINDRIA_USER_HOME}/.azure/*
fi

# Resetting permission for .docker
if [ -d ${SINDRIA_USER_HOME}/.docker ]; then
    chmod 700 ${SINDRIA_USER_HOME}/.docker
    chmod 600 ${SINDRIA_USER_HOME}/.docker/*
fi

# Resetting permission for .kube
if [ -d ${SINDRIA_USER_HOME}/.kube ]; then
    chmod 700 ${SINDRIA_USER_HOME}/.kube
    chmod 600 ${SINDRIA_USER_HOME}/.kube/*
fi


# Setup git username
if [ "${GIT_USERNAME}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global user.name ${GIT_USERNAME}"
fi

# Setup git email
if [ "${GIT_EMAIL}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global user.email ${GIT_EMAIL}"
fi

# Setup git editor
if [ "${GIT_EDITOR}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global core.editor ${GIT_EDITOR}"
fi

# Setup git sindria path
if [ "${GIT_SINDRIA_PATH}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global sindria.path ${GIT_SINDRIA_PATH}"
fi

# Setup sindria git token
if [ "${GIT_SINDRIA_TOKEN}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global sindria.token ${GIT_SINDRIA_TOKEN}"
fi

# Setup git sindria url
if [ "${GIT_SINDRIA_URL}" != "" ]; then
    su ${SINDRIA_USER} -c "git config --global sindria.url ${GIT_SINDRIA_URL}"
fi

# Override default sindria user password by env
if [ "${XDEV_SINDRIA_USER_PASSWORD}" != "sindria" ]; then
    echo "${SINDRIA_USER}:${XDEV_SINDRIA_USER_PASSWORD}" | chpasswd
    vncpasswd -f <<< "${XDEV_SINDRIA_USER_PASSWORD}" > "/root/.vnc/passwd"
    vncpasswd -f <<< "${XDEV_SINDRIA_USER_PASSWORD}" > "${SINDRIA_USER_HOME}/.vnc/passwd"
fi

# Override default ssh public key by env
if [ "${XDEV_SINDRIA_USER_PUBKEY}" != "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDBjVW2tS/yVwcvPKwfd2jyowzsBUAI73+CY9JNP175LPjQWuAuCv90HU7QKuKIwECELt6h1nO4if3u7LFsUnkRK/OMn2lbXu6ypufDHDmLTF4GyXhWK9sFK5/gePbHk+M7egDDzCd1Ww75YR9IzBHSsyQWi9LnAoBAUNe5Av6MgSRSI+4pHzL3TLdzLmFJ8AbL0rX21Hsw4WNsACUGwgaP7EuHXGqo8RI0g9K9MM7Aq9TxLjxo7fwfXxYAqqJcSnRQjN1lUnkRVtdkeEqQ9C7cptl70kKGi4BrwB8bf7BmmN7YUwTNwFTrgSB8xuPx1mSN4TFsPLUL3GZDj7PpddRH7MTF5GRoSAg8FEF7CgL9TZxxbN+Ea2y4SJgzPNGvrq0HZCzxCk4st+cBdfCiopgBPtCBVDOPLYEMf52ltMcqGNWQESTdBZ4nIQkkEe9t6DCBQBU3v0reHNyTU8BdTNCii0ecBtBPt0z6g3+sR/vWe+DegnkpeBwpCDS9Y/ZOu08= sindria@xdev.local" ]; then
    echo "${XDEV_SINDRIA_USER_PUBKEY}" > ${SINDRIA_USER_HOME}/.ssh/authorized_keys
    chmod 700 ${SINDRIA_USER_HOME}/.ssh
    chmod 600 ${SINDRIA_USER_HOME}/.ssh/authorized_keys
fi

# Override host user uid by env
if [ "${HOST_USER_UID}" != "1000" ]; then
    usermod -u $HOST_USER_UID ${SINDRIA_USER} && groupmod ${SINDRIA_USER} -g $HOST_USER_UID
fi


# Override docker host uid by env
if [ "${HOST_DOCKER_GROUP_UID}" != "975" ]; then
    groupmod -g ${HOST_DOCKER_GROUP_UID} docker
fi


# Override timezone by env
if [ "${TZ}" != "Europe/Rome" ]; then
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
fi


# Custom I3 configuration
if [ -f "/home/sindria/config/i3/config" ]; then
  cp /home/sindria/config/i3/config /home/sindria/.config/i3/config
fi

# Start sshd
/usr/sbin/sshd &


if [ "${XDEV_MODE}" == "legacy" ]; then
  su ${SINDRIA_USER} -c "export DISPLAY=$DISPLAY && /usr/local/bin/i3"
fi

if [ "${XDEV_MODE}" == "web" ]; then
  su ${SINDRIA_USER} -c "/opt/TurboVNC/bin/vncserver -geometry ${XDEV_WEB_RESOLUTION} -xstartup /usr/local/bin/i3 ${DISPLAY}"
  websockify -D --web=/usr/share/novnc/ --cert=~/.novnc/novnc.pem ${XDEV_WEB_PORT} ${XDEV_VNC_HOST}:${XDEV_VNC_PORT} && tail -f /dev/null
fi