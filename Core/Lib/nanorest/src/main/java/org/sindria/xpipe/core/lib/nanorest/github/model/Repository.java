package org.sindria.xpipe.core.lib.nanorest.github.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


//        public Repository(String name) {
//        this.name = name;
//        this.description = "";
//        this.homepage = "";
//        this.isPrivate = false;
//        this.hasIssues = true;
//        this.hasProjects =  true;
//        this.hasWiki = true;
//        this.hasDownloads = true;
//        this.isTemplate = false;
//        this.teamId = 0;
//        this.autoInit = false;
//        this.gitignoreTemplate = "";
//        this.licenseTemplate = "";
//        this.allowSquashMerge = true;
//        this.allowMergeCommit = true;
//        this.allowMergeRebase = true;
//        this.allowAutoMerge = false;
//        this.deleteBranchOnMerge = false;
//        this.useSquashPrTitleAsDefault = false;
//        this.squashMergeCommitTitle = "";
//        this.squashMergeCommitMessage = "";
//        this.mergeCommitTitle = "";
//        this.mergeCommitMessage = "";
//    }

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

    public JSONObject serialize() {
        return new JSONObject(this.toJson());
    }

    private String toJson() {
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("name", name);
        if (!(description == null)) jsonMap.put("description", description);
        if (!(homepage == null)) jsonMap.put("homepage", homepage);
        if (isPrivate) jsonMap.put("private", isPrivate);
        if (hasIssues) jsonMap.put("has_issues", hasIssues);
        if (hasProjects) jsonMap.put("has_projects", hasProjects);
        if (hasWiki) jsonMap.put("has_wiki", hasWiki);
        if (hasDownloads) jsonMap.put("has_downloads", hasDownloads);
        if (isTemplate) jsonMap.put("is_template", isTemplate);
        if (teamId != -1) jsonMap.put("team_id", teamId);
        if (autoInit) jsonMap.put("auto_init", autoInit);
        if (!(gitignoreTemplate == null)) jsonMap.put("gitignore_template", gitignoreTemplate);
        if (!(licenseTemplate == null)) jsonMap.put("license_template", licenseTemplate);
        if (allowSquashMerge) jsonMap.put("allow_squash_merge", allowSquashMerge);
        if (allowMergeCommit) jsonMap.put("allow_merge_commit", allowMergeCommit);
        if (allowMergeRebase) jsonMap.put("allow_merge_rebase", allowMergeRebase);
        if (allowAutoMerge) jsonMap.put("allow_auto_merge", allowAutoMerge);
        if (deleteBranchOnMerge) jsonMap.put("delete_branch_on_merge", deleteBranchOnMerge);
        if (useSquashPrTitleAsDefault) jsonMap.put("use_squash_pr_title_as_default", useSquashPrTitleAsDefault);
        if (!(squashMergeCommitTitle == null)) jsonMap.put("squash_merge_commit_title", squashMergeCommitTitle);
        if (!(squashMergeCommitMessage == null)) jsonMap.put("squash_merge_commit_message", squashMergeCommitMessage);
        if (!(mergeCommitTitle == null)) jsonMap.put("merge_commit_title", mergeCommitTitle);
        if (!(mergeCommitMessage == null)) jsonMap.put("merge_commit_message", mergeCommitMessage);


        return new JSONObject(jsonMap).toString();
    }
}
