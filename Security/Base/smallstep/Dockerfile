FROM debian:bullseye

RUN apt-get update && apt-get install -y \
    curl unzip bash gnupg ca-certificates awscli \
    && rm -rf /var/lib/apt/lists/*

# Install Smallstep
RUN curl -fsSL https://smallstep.com/install | bash
ENV PATH="/root/.step/bin:$PATH"

# Install Lego (ACME client)
RUN curl -s https://api.github.com/repos/go-acme/lego/releases/latest \
  | grep browser_download_url | grep linux_amd64 \
  | cut -d '"' -f 4 | xargs curl -L -o lego.tar.gz && \
  tar xf lego.tar.gz && mv lego /usr/local/bin/ && rm lego.tar.gz

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

WORKDIR /lab
ENTRYPOINT ["/entrypoint.sh"]