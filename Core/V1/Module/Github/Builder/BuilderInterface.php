<?php

namespace Core\Github\Builder;

interface BuilderInterface
{
    // RepositoryBuilder setName(string $name); // Commentato come in Java

    public function setDescription(string $description): RepositoryBuilder;

    public function setHomepage(string $homepage): RepositoryBuilder;

    public function setIsPrivate(bool $isPrivate): RepositoryBuilder;

    public function setHasIssues(bool $hasIssues): RepositoryBuilder;

    public function setHasProjects(bool $hasProjects): RepositoryBuilder;

    public function setHasWiki(bool $hasWiki): RepositoryBuilder;

    public function setHasDownloads(bool $hasDownloads): RepositoryBuilder;

    public function setIsTemplate(bool $isTemplate): RepositoryBuilder;

    public function setTeamId(int $teamId): RepositoryBuilder;

    public function setAutoInit(bool $autoInit): RepositoryBuilder;

    public function setGitIgnoreTemplate(string $gitignoreTemplate): RepositoryBuilder;

    public function setLicenseTemplate(string $licenseTemplate): RepositoryBuilder;

    public function setAllowSquashMerge(bool $allowSquashMerge): RepositoryBuilder;

    public function setAllowMergeCommit(bool $allowMergeCommit): RepositoryBuilder;

    public function setAllowMergeRebase(bool $allowMergeRebase): RepositoryBuilder;

    public function setAllowAutoMerge(bool $allowAutoMerge): RepositoryBuilder;

    public function setDeleteBranchOnMerge(bool $deleteBranchOnMerge): RepositoryBuilder;

    public function setUseSquashPrTitleAsDefault(bool $useSquashPrTitleAsDefault): RepositoryBuilder;

    public function setSquashMergeCommitTitle(string $squashMergeCommitTitle): RepositoryBuilder;

    public function setSquashMergeCommitMessage(string $squashMergeCommitMessage): RepositoryBuilder;

    public function setMergeCommitTitle(string $mergeCommitTitle): RepositoryBuilder;

    public function setMergeCommitMessage(string $mergeCommitMessage): RepositoryBuilder;
}
