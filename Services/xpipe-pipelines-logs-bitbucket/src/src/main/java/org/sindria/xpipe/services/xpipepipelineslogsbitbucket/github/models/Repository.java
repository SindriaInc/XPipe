package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

import java.net.URI;
import java.net.URISyntaxException;

public class Repository {

    private final String name;

    private final String description;

    private final String homepage;

    private final boolean isPrivate;

    private final boolean hasIssues;

    private final boolean hasProjects;

    private final boolean hasWiki;

    // https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#create-an-organization-repository

    public Repository(String name) {
        this.name = name;
        this.description = "";
        this.homepage = "";
        this.isPrivate = false;
        this.hasIssues = true;
        this.hasProjects =  true;
        this.hasWiki = true;
    }

    public Repository(String name, String description, String homepage, boolean isPrivate,  boolean hasIssues, boolean hasProjects, boolean hasWiki) {
        this.name = name;
        this.description = description;
        this.homepage = homepage;
        this.isPrivate = isPrivate;
        this.hasIssues = hasIssues;
        this.hasProjects = hasProjects;
        this.hasWiki = hasWiki;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHomepage() {
        return homepage;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public boolean getHasIssues() {
        return hasIssues;
    }

    public boolean getHasProjects() {
        return hasProjects;
    }

    public boolean getHasWiki() {
        return hasWiki;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }

}
