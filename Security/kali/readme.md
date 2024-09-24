![docker image](https://github.com/SindriaInc/kali/actions/workflows/docker-image.yml/badge.svg) 
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# kali

Custom kali base image ready for pentest.

## Requirements

- Docker CE
- Docker Compose

## Setup

### Standard install

- On your host make a directory: `mkdir -p kali`
- Move into it: `cd kali`
- Go to under deployments [folder](https://github.com/SindriaInc/kali/tree/master/deployments)
- Setup file env: `cp .env.example .env`
- Setup Docker Compose `cp docker-compose.example.yml docker-compose.yml`
- Run: `docker-compose up -d`

N.B. If you got an error check out others deployments mode, such as host mode and subnet mode.

# License

This software is release open source under [GPL](https://github.com/SindriaInc/kali/blob/master/LICENSE) license.