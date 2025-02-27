package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models;

import org.json.JSONObject;
import org.sindria.xpipe.lib.nanoREST.serializers.JsonSerializer;

public class Repository {

    private final String name;

    private final String description;

    private final String homepage;

    private final boolean isPrivate;

    private final boolean hasIssues;

    private final boolean hasProjects;

    private final boolean hasWiki;

    private final boolean hasDownloads;

    private final boolean isTemplate;

    private final int teamId;

    private final boolean autoInit;

    private final String gitignoreTemplate;

    private final String licenseTemplate;

    private final boolean allowSquashMerge;

    private final boolean allowMergeCommit;

    private final boolean allowMergeRebase;

    private final boolean allowAutoMerge;

    private final boolean deleteBranchOnMerge;

    private final boolean useSquashPrTitleAsDefault;

    private final String squashMergeCommitTitle;

    private final String squashMergeCommitMessage;

    private final String mergeCommitTitle;

    private final String mergeCommitMessage;

    // https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#create-an-organization-repository

    public Repository(String name) {
        this.name = name;
        this.description = "";
        this.homepage = "";
        this.isPrivate = false;
        this.hasIssues = true;
        this.hasProjects =  true;
        this.hasWiki = true;
        this.hasDownloads = true;
        this.isTemplate = false;
        this.teamId = 0;
        this.autoInit = false;
        this.gitignoreTemplate = "";
        this.licenseTemplate = "";
        this.allowSquashMerge = true;
        this.allowMergeCommit = true;
        this.allowMergeRebase = true;
        this.allowAutoMerge = false;
        this.deleteBranchOnMerge = false;
        this.useSquashPrTitleAsDefault = false;
        this.squashMergeCommitTitle = "";
        this.squashMergeCommitMessage = "";
        this.mergeCommitTitle = "";
        this.mergeCommitMessage = "";
    }

    public Repository(
            String name,
            String description,
            String homepage,
            boolean isPrivate,
            boolean hasIssues,
            boolean hasProjects,
            boolean hasWiki,
            boolean hasDownloads,
            boolean isTemplate,
            int teamId,
            boolean autoInit,
            String gitignoreTemplate,
            String licenseTemplate,
            boolean allowSquashMerge,
            boolean allowMergeCommit,
            boolean allowMergeRebase,
            boolean allowAutoMerge,
            boolean deleteBranchOnMerge,
            boolean useSquashPrTitleAsDefault,
            String squashMergeCommitTitle,
            String squashMergeCommitMessage,
            String mergeCommitTitle,
            String mergeCommitMessage
    ) {
        this.name = name;
        this.description = description;
        this.homepage = homepage;
        this.isPrivate = isPrivate;
        this.hasIssues = hasIssues;
        this.hasProjects = hasProjects;
        this.hasWiki = hasWiki;
        this.hasDownloads = hasDownloads;
        this.isTemplate = isTemplate;
        this.teamId = teamId;
        this.autoInit = autoInit;
        this.gitignoreTemplate = gitignoreTemplate;
        this.licenseTemplate = licenseTemplate;
        this.allowSquashMerge = allowSquashMerge;
        this.allowMergeCommit = allowMergeCommit;
        this.allowMergeRebase = allowMergeRebase;
        this.allowAutoMerge = allowAutoMerge;
        this.deleteBranchOnMerge = deleteBranchOnMerge;
        this.useSquashPrTitleAsDefault = useSquashPrTitleAsDefault;
        this.squashMergeCommitTitle = squashMergeCommitTitle;
        this.squashMergeCommitMessage = squashMergeCommitMessage;
        this.mergeCommitTitle = mergeCommitTitle;
        this.mergeCommitMessage = mergeCommitMessage;
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
    public boolean getHasDownloads() {
        return hasDownloads;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public int getTeamId() {
        return teamId;
    }

    public boolean isAutoInit() {
        return autoInit;
    }

    public String getGitignoreTemplate() {
        return gitignoreTemplate;
    }

    public String getLicenseTemplate() {
        return licenseTemplate;
    }

    public boolean isAllowSquashMerge() {
        return allowSquashMerge;
    }

    public boolean isAllowMergeCommit() {
        return allowMergeCommit;
    }

    public boolean isAllowMergeRebase() {
        return allowMergeRebase;
    }

    public boolean isAllowAutoMerge() {
        return allowAutoMerge;
    }

    public boolean isDeleteBranchOnMerge() {
        return deleteBranchOnMerge;
    }

    public boolean isUseSquashPrTitleAsDefault() {
        return useSquashPrTitleAsDefault;
    }

    public String getSquashMergeCommitTitle() {
        return squashMergeCommitTitle;
    }

    public String getSquashMergeCommitMessage() {
        return squashMergeCommitMessage;
    }

    public String getMergeCommitTitle() {
        return mergeCommitTitle;
    }

    public String getMergeCommitMessage() {
        return mergeCommitMessage;
    }

    public JSONObject serialize() {
        return new JSONObject(JsonSerializer.toJson(this));
    }
}
