package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.builder;

import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Repository;

public class RepositoryBuilder implements Builder {
    private String name;
    private String description;
    private String homepage;
    private boolean isPrivate;
    private boolean hasIssues;
    private boolean hasProjects;
    private boolean hasWiki;
    private boolean hasDownloads;
    private boolean isTemplate;
    private int teamId;
    private boolean autoInit;
    private String gitignoreTemplate;
    private String licenseTemplate;
    private boolean allowSquashMerge;
    private boolean allowMergeCommit;
    private boolean allowMergeRebase;
    private boolean allowAutoMerge;
    private boolean deleteBranchOnMerge;
    private boolean useSquashPrTitleAsDefault;
    private String squashMergeCommitTitle;
    private String squashMergeCommitMessage;
    private String mergeCommitTitle;
    private String mergeCommitMessage;

    public RepositoryBuilder(String name) {
        this.name = name;
    }

//
//    @Override
//    public RepositoryBuilder setName(String name) {
//        this.name = name;
//    }

    @Override
    public RepositoryBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public RepositoryBuilder setHomepage(String homepage) {
        this.homepage = homepage;
        return this;
    }

    @Override
    public RepositoryBuilder setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    @Override
    public RepositoryBuilder setHasIssues(boolean hasIssues) {
        this.hasIssues = hasIssues;
        return this;
    }

    @Override
    public RepositoryBuilder setHasProjects(boolean hasProjects) {
        this.hasProjects = hasProjects;
        return this;
    }

    @Override
    public RepositoryBuilder setHasWiki(boolean hasWiki) {
        this.hasWiki = hasWiki;
        return this;
    }

    @Override
    public RepositoryBuilder setHasDownloads(boolean hasDownloads) {
        this.hasDownloads = hasDownloads;
        return this;
    }

    @Override
    public RepositoryBuilder setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
        return this;
    }

    @Override
    public RepositoryBuilder setTeamId(int teamId) {
        this.teamId = teamId;
        return this;
    }

    @Override
    public RepositoryBuilder setAutoInit(boolean autoInit) {
        this.autoInit = autoInit;
        return this;
    }

    @Override
    public RepositoryBuilder setGitIgnoreTemplate(String gitignoreTemplate) {
        this.gitignoreTemplate = gitignoreTemplate;
        return this;
    }

    @Override
    public RepositoryBuilder setLicenseTemplate(String licenseTemplate) {
        this.licenseTemplate = licenseTemplate;
        return this;
    }

    @Override
    public RepositoryBuilder setAllowSquashMerge(boolean allowSquashMerge) {
        this.allowSquashMerge = allowSquashMerge;
        return this;
    }

    @Override
    public RepositoryBuilder setAllowMergeCommit(boolean allowMergeCommit) {
        this.allowMergeCommit = allowMergeCommit;
        return this;
    }

    @Override
    public RepositoryBuilder setAllowMergeRebase(boolean allowMergeRebase) {
        this.allowMergeRebase = allowMergeRebase;
        return this;
    }

    @Override
    public RepositoryBuilder setAllowAutoMerge(boolean allowAutoMerge) {
        this.allowAutoMerge = allowAutoMerge;
        return this;
    }

    @Override
    public RepositoryBuilder setDeleteBranchOnMerge(boolean deleteBranchOnMerge) {
        this.deleteBranchOnMerge = deleteBranchOnMerge;
        return this;
    }

    @Override
    public RepositoryBuilder setUseSquashPrTitleAsDefault(boolean useSquashPrTitleAsDefault) {
        this.useSquashPrTitleAsDefault = useSquashPrTitleAsDefault;
        return this;
    }

    @Override
    public RepositoryBuilder setSquashMergeCommitTitle(String squashMergeCommitTitle) {
        this.squashMergeCommitTitle = squashMergeCommitTitle;
        return this;
    }

    @Override
    public RepositoryBuilder setSquashMergeCommitMessage(String squashMergeCommitMessage) {
        this.squashMergeCommitMessage = squashMergeCommitMessage;
        return this;
    }

    @Override
    public RepositoryBuilder setMergeCommitTitle(String mergeCommitTitle) {
        this.mergeCommitTitle = mergeCommitTitle;
        return this;
    }

    @Override
    public RepositoryBuilder setMergeCommitMessage(String mergeCommitMessage) {
        this.mergeCommitMessage = mergeCommitMessage;
        return this;
    }

    public Repository build() {
        return new Repository(
                name, description, homepage, isPrivate, hasIssues, hasProjects, hasWiki,
                hasDownloads, isTemplate, teamId, autoInit, gitignoreTemplate, licenseTemplate,
                allowSquashMerge, allowMergeCommit, allowMergeRebase, allowAutoMerge,
                deleteBranchOnMerge, useSquashPrTitleAsDefault, squashMergeCommitTitle,
                squashMergeCommitMessage, mergeCommitTitle, mergeCommitMessage
        );
    }
}
