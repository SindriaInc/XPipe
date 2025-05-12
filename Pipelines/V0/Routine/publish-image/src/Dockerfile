# stable/Dockerfile
#
# Build a Buildah container image from the latest
# stable version of Buildah on the Fedoras Updates System.
# https://bodhi.fedoraproject.org/updates/?search=buildah
# This image can be used to create a secured container
# that runs safely with privileges within the container.
#
FROM registry.fedoraproject.org/fedora:latest

ARG TAG_VERSION
ARG TIMEZONE

LABEL \
	name="publish-image" \
	image="sindriainc/publish-image" \
	tag="${TAG_VERSION}" \
	vendor="sindria"

ENV SINDRIA_USER sindria
ENV SINDRIA_USER_HOME /home/sindria
ENV TZ=${TIMEZONE}
ENV DOCKER_COMPOSE_VERSION 1.29.2

# Update system, install base software and set timezone
RUN dnf update -y && \
    dnf install curl wget rsync git unzip bzip2 -y && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    dnf clean all

# Adding sindria user
RUN useradd ${SINDRIA_USER} -m -d ${SINDRIA_USER_HOME} -s /bin/bash && \
    mkdir -p ${SINDRIA_USER_HOME}/.docker && \
    chmod 700 ${SINDRIA_USER_HOME}/.docker && \
    chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}

# Install docker and docker-compose
RUN dnf -y install dnf-plugins-core && \
    dnf config-manager --add-repo https://download.docker.com/linux/fedora/docker-ce.repo && \
    dnf install docker-ce docker-ce-cli containerd.io -y && \
    usermod -a -G docker ${SINDRIA_USER} && \
    #groupmod -g ${HOST_DOCKER_GROUP_UID} docker && \
    curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose && \
    sudo chmod +x /usr/local/bin/docker-compose && \
    dnf clean all

# Don't include container-selinux and remove
# directories used by yum that are just taking
# up space.
RUN yum -y update; rpm --restore shadow-utils 2>/dev/null; yum -y install buildah fuse-overlayfs xz --exclude container-selinux; rm -rf /var/cache /var/log/dnf* /var/log/yum.*;

ADD https://raw.githubusercontent.com/containers/buildah/main/contrib/buildahimage/stable/containers.conf /etc/containers/

# Adjust storage.conf to enable Fuse storage.
RUN chmod 644 /etc/containers/containers.conf; sed -i -e 's|^#mount_program|mount_program|g' -e '/additionalimage.*/a "/var/lib/shared",' -e 's|^mountopt[[:space:]]*=.*$|mountopt = "nodev,fsync=0"|g' /etc/containers/storage.conf
RUN mkdir -p /var/lib/shared/overlay-images /var/lib/shared/overlay-layers /var/lib/shared/vfs-images /var/lib/shared/vfs-layers; touch /var/lib/shared/overlay-images/images.lock; touch /var/lib/shared/overlay-layers/layers.lock; touch /var/lib/shared/vfs-images/images.lock; touch /var/lib/shared/vfs-layers/layers.lock

# Define uid/gid ranges for our user https://github.com/containers/buildah/issues/3053
RUN echo ${SINDRIA_USER}:2000:50000 > /etc/subuid; \
 echo ${SINDRIA_USER}:2000:50000 > /etc/subgid; \
 mkdir -p ${SINDRIA_USER_HOME}/.local/share/containers; \
 chown -R ${SINDRIA_USER}:${SINDRIA_USER} ${SINDRIA_USER_HOME}

VOLUME /var/lib/containers
VOLUME ${SINDRIA_USER_HOME}/.local/share/containers

# Set an environment variable to default to chroot isolation for RUN
# instructions and "buildah run".
ENV BUILDAH_ISOLATION=chroot

WORKDIR ${SINDRIA_USER_HOME}

USER ${SINDRIA_USER}

# Add and setup entrypoint
COPY bin/entrypoint.sh /usr/local/bin/entrypoint.sh
ENTRYPOINT ["/bin/bash", "entrypoint.sh"]