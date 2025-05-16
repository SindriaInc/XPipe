# syntax=docker/dockerfile:1
FROM ubuntu:24.04

WORKDIR /opt

RUN apt update && apt install -y x11-apps xvfb wget libxcb-* libxkbcommon-*
RUN wget #odaurl#
RUN dpkg -i *.deb

WORKDIR /usr/lib/x86_64-linux-gnu
RUN ln -s libxcb-util.so.1 libxcb-util.so.0
