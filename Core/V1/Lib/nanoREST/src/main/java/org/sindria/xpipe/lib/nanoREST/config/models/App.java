package org.sindria.xpipe.lib.nanoREST.config.models;

public class App {

    public Bitbucket bitbucket;

    public Github github;

    public Gitea gitea;

    public Jenkins jenkins;

    public Cmdbuild cmdbuild;


    public Bitbucket getBitbucket() {
        return bitbucket;
    }

    public void setBitbucket(Bitbucket bitbucket) {
        this.bitbucket = bitbucket;
    }

    public Github getGithub() {
        return github;
    }

    public void setGithub(Github github) {
        this.github = github;
    }

    public Gitea getGitea() {
        return gitea;
    }

    public void setGitea(Gitea gitea) {
        this.gitea = gitea;
    }

    public Jenkins getJenkins() {
        return jenkins;
    }

    public void setJenkins(Jenkins jenkins) {
        this.jenkins = jenkins;
    }

    public Cmdbuild getCmdbuild() {
        return cmdbuild;
    }

    public void setCmdbuild(Cmdbuild cmdbuild) {
        this.cmdbuild = cmdbuild;
    }


}
