<?php

namespace Core\Github\Builder;

use  Core\Github\Model\Repository;

class RepositoryBuilder implements BuilderInterface
{
    private string $name;
    private ?string $description = null;
    private ?string $homepage = null;
    private bool $isPrivate = false;
    private bool $hasIssues = true;
    private bool $hasProjects = true;
    private bool $hasWiki = true;
    private bool $hasDownloads = true;
    private bool $isTemplate = false;
    private int $teamId = -1;
    private bool $autoInit = false;
    private ?string $gitignoreTemplate = null;
    private ?string $licenseTemplate = null;
    private bool $allowSquashMerge = true;
    private bool $allowMergeCommit = true;
    private bool $allowMergeRebase = true;
    private bool $allowAutoMerge = false;
    private bool $deleteBranchOnMerge = false;
    private bool $useSquashPrTitleAsDefault = false;
    private ?string $squashMergeCommitTitle = null;
    private ?string $squashMergeCommitMessage = null;
    private ?string $mergeCommitTitle = null;
    private ?string $mergeCommitMessage = null;

    public function __construct(string $name)
    {
        $this->name = $name;
    }

    public function setDescription(string $description): RepositoryBuilder
    {
        $this->description = $description;
        return $this;
    }

    public function setHomepage(string $homepage): RepositoryBuilder
    {
        $this->homepage = $homepage;
        return $this;
    }

    public function setIsPrivate(bool $isPrivate): RepositoryBuilder
    {
        $this->isPrivate = $isPrivate;
        return $this;
    }

    public function setHasIssues(bool $hasIssues): RepositoryBuilder
    {
        $this->hasIssues = $hasIssues;
        return $this;
    }

    public function setHasProjects(bool $hasProjects): RepositoryBuilder
    {
        $this->hasProjects = $hasProjects;
        return $this;
    }

    public function setHasWiki(bool $hasWiki): RepositoryBuilder
    {
        $this->hasWiki = $hasWiki;
        return $this;
    }

    public function setHasDownloads(bool $hasDownloads): RepositoryBuilder
    {
        $this->hasDownloads = $hasDownloads;
        return $this;
    }

    public function setIsTemplate(bool $isTemplate): RepositoryBuilder
    {
        $this->isTemplate = $isTemplate;
        return $this;
    }

    public function setTeamId(int $teamId): RepositoryBuilder
    {
        $this->teamId = $teamId;
        return $this;
    }

    public function setAutoInit(bool $autoInit): RepositoryBuilder
    {
        $this->autoInit = $autoInit;
        return $this;
    }

    public function setGitIgnoreTemplate(string $gitignoreTemplate): RepositoryBuilder
    {
        $this->gitignoreTemplate = $gitignoreTemplate;
        return $this;
    }

    public function setLicenseTemplate(string $licenseTemplate): RepositoryBuilder
    {
        $this->licenseTemplate = $licenseTemplate;
        return $this;
    }

    public function setAllowSquashMerge(bool $allowSquashMerge): RepositoryBuilder
    {
        $this->allowSquashMerge = $allowSquashMerge;
        return $this;
    }

    public function setAllowMergeCommit(bool $allowMergeCommit): RepositoryBuilder
    {
        $this->allowMergeCommit = $allowMergeCommit;
        return $this;
    }

    public function setAllowMergeRebase(bool $allowMergeRebase): RepositoryBuilder
    {
        $this->allowMergeRebase = $allowMergeRebase;
        return $this;
    }

    public function setAllowAutoMerge(bool $allowAutoMerge): RepositoryBuilder
    {
        $this->allowAutoMerge = $allowAutoMerge;
        return $this;
    }

    public function setDeleteBranchOnMerge(bool $deleteBranchOnMerge): RepositoryBuilder
    {
        $this->deleteBranchOnMerge = $deleteBranchOnMerge;
        return $this;
    }

    public function setUseSquashPrTitleAsDefault(bool $useSquashPrTitleAsDefault): RepositoryBuilder
    {
        $this->useSquashPrTitleAsDefault = $useSquashPrTitleAsDefault;
        return $this;
    }

    public function setSquashMergeCommitTitle(string $squashMergeCommitTitle): RepositoryBuilder
    {
        $this->squashMergeCommitTitle = $squashMergeCommitTitle;
        return $this;
    }

    public function setSquashMergeCommitMessage(string $squashMergeCommitMessage): RepositoryBuilder
    {
        $this->squashMergeCommitMessage = $squashMergeCommitMessage;
        return $this;
    }

    public function setMergeCommitTitle(string $mergeCommitTitle): RepositoryBuilder
    {
        $this->mergeCommitTitle = $mergeCommitTitle;
        return $this;
    }

    public function setMergeCommitMessage(string $mergeCommitMessage): RepositoryBuilder
    {
        $this->mergeCommitMessage = $mergeCommitMessage;
        return $this;
    }

    public function build(): Repository
    {
        return new Repository(
            $this->name,
            $this->description,
            $this->homepage,
            $this->isPrivate,
            $this->hasIssues,
            $this->hasProjects,
            $this->hasWiki,
            $this->hasDownloads,
            $this->isTemplate,
            $this->teamId,
            $this->autoInit,
            $this->gitignoreTemplate,
            $this->licenseTemplate,
            $this->allowSquashMerge,
            $this->allowMergeCommit,
            $this->allowMergeRebase,
            $this->allowAutoMerge,
            $this->deleteBranchOnMerge,
            $this->useSquashPrTitleAsDefault,
            $this->squashMergeCommitTitle,
            $this->squashMergeCommitMessage,
            $this->mergeCommitTitle,
            $this->mergeCommitMessage
        );
    }
}
