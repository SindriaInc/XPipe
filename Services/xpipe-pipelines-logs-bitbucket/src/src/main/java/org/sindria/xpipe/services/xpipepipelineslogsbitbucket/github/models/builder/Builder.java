package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.builder;

public interface Builder {

//    RepositoryBuilder setName(String name);

    RepositoryBuilder setDescription(String description);

    RepositoryBuilder setHomepage(String homepage);

    RepositoryBuilder setIsPrivate(boolean isPrivate);

    RepositoryBuilder setHasIssues(boolean hasIssues);

    RepositoryBuilder setHasProjects(boolean hasProjects);

    RepositoryBuilder setHasWiki(boolean hasWiki);

    RepositoryBuilder setHasDownloads(boolean hasDownloads);

    RepositoryBuilder setIsTemplate(boolean isTemplate);

    RepositoryBuilder setTeamId(int teamId);

    RepositoryBuilder setAutoInit(boolean autoInit);

    RepositoryBuilder setGitIgnoreTemplate(String gitignoreTemplate);

    RepositoryBuilder setLicenseTemplate(String licenseTemplate);

    RepositoryBuilder setAllowSquashMerge(boolean allowSquashMerge);

    RepositoryBuilder setAllowMergeCommit(boolean allowMergeCommit);

    RepositoryBuilder setAllowMergeRebase(boolean allowMergeRebase);

    RepositoryBuilder setAllowAutoMerge(boolean allowAutoMerge);

    RepositoryBuilder setDeleteBranchOnMerge(boolean deleteBranchOnMerge);

    RepositoryBuilder setUseSquashPrTitleAsDefault(boolean useSquashPrTitleAsDefault);

    RepositoryBuilder setSquashMergeCommitTitle(String squashMergeCommitTitle);

    RepositoryBuilder setSquashMergeCommitMessage(String squashMergeCommitMessage);

    RepositoryBuilder setMergeCommitTitle(String mergeCommitTitle);

    RepositoryBuilder setMergeCommitMessage(String mergeCommitMessage);

}
