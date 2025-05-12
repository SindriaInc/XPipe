# XPipe - Terminal

This repo is for xdev terminal websocket micro-service of XPipe platform.

## Setup Development Environment

- Build local image: `bash build.sh sindriaproject/xpipe-terminal local arm64v8`
- Setup env: `cp .env.local .env`
- Setup docker compose: `cp docker-compose.local.yml docker-compose.yml`
- Start environment: `docker-compose up -d`