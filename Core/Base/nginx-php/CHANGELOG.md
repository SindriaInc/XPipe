## 6.0.0 (2023-05-17)

Breaking changes:

  - all versions run by default with sindria user -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)
  - moved startup.sh from /startup.sh to /usr/local/bin/startup.sh -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)
  - docker-compose command override now need sudo before commands -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)
  - interactive root commands now requires sudo before -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)

Features:

  - add support for wordpress to 8.0 and 8.1 versions -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)

Bugfixes:

  - fix on ambiguous envs available -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)
  - disabled healthcheck -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)

Security:

  - now all process run as uid 1000 -> [327ac5f](https://github.com/SindriaInc/XPipe/commit/327ac5fdb3509b3d0aa696965572a55715c3b0d3)