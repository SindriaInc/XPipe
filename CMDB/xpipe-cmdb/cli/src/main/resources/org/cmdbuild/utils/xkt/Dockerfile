# syntax=docker/dockerfile:1
FROM node:lts-slim

WORKDIR /usr/app

RUN apt update && apt install -y unzip
RUN npm install commander

RUN mkdir -p xeokit-convert
COPY convert2xkt.zip xeokit-convert

WORKDIR /usr/app/xeokit-convert
RUN unzip convert2xkt.zip
RUN rm -rf convert2xkt.zip
RUN sed -i "s/\"web-ifc\": \"0.0.40\"/\"web-ifc\": \"#ifcversion#\"/" package.json
RUN cat package.json | grep web-ifc
RUN npm install
