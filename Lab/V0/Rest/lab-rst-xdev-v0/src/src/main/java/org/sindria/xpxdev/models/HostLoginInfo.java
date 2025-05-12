package org.sindria.xpxdev.models;

import org.springframework.web.multipart.MultipartFile;

public class HostLoginInfo {
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private MultipartFile privatekey;


    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MultipartFile getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(MultipartFile privatekey) {
        this.privatekey = privatekey;
    }

    public HostLoginInfo() {}

    /**
     * HostLoginInfo constructor
     */
    public HostLoginInfo(String hostname, Integer port, String username, String password, MultipartFile privatekey) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.privatekey = privatekey;
    }


}