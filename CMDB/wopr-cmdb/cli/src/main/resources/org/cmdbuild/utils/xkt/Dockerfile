# syntax=docker/dockerfile:1
FROM node:lts-slim

WORKDIR /usr/app

RUN apt update && apt install -y git
RUN npm install commander

RUN git clone --branch Fix-web-ifc-update --single-branch https://github.com/PDRTecnoteca/xeokit-convert.git

WORKDIR /usr/app/xeokit-convert
RUN sed -i "s/\"web-ifc\": \"0.0.66\"/\"web-ifc\": \"#ifcversion#\"/" package.json
RUN cat package.json | grep web-ifc
RUN npm install
