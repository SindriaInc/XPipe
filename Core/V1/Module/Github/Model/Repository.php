<?php

namespace Core\Github\Model;

class Repository
{
    private string $name;
    private ?string $description;
    private ?string $homepage;

    private bool $isPrivate;
    private bool $hasIssues;
    private bool $hasProjects;
    private bool $hasWiki;
    private bool $hasDownloads;
    private bool $isTemplate;
    private int $teamId;
    private bool $autoInit;

    private ?string $gitignoreTemplate;
    private ?string $licenseTemplate;

    private bool $allowSquashMerge;
    private bool $allowMergeCommit;
    private bool $allowMergeRebase;
    private bool $allowAutoMerge;
    private bool $deleteBranchOnMerge;
    private bool $useSquashPrTitleAsDefault;

    private ?string $squashMergeCommitTitle;
    private ?string $squashMergeCommitMessage;
    private ?string $mergeCommitTitle;
    private ?string $mergeCommitMessage;

    public function __construct(
        string $name,
        ?string $description,
        ?string $homepage,
        bool $isPrivate,
        bool $hasIssues,
        bool $hasProjects,
        bool $hasWiki,
        bool $hasDownloads,
        bool $isTemplate,
        int $teamId,
        bool $autoInit,
        ?string $gitignoreTemplate,
        ?string $licenseTemplate,
        bool $allowSquashMerge,
        bool $allowMergeCommit,
        bool $allowMergeRebase,
        bool $allowAutoMerge,
        bool $deleteBranchOnMerge,
        bool $useSquashPrTitleAsDefault,
        ?string $squashMergeCommitTitle,
        ?string $squashMergeCommitMessage,
        ?string $mergeCommitTitle,
        ?string $mergeCommitMessage
    ) {
        $this->name = $name;
        $this->description = $description;
        $this->homepage = $homepage;
        $this->isPrivate = $isPrivate;
        $this->hasIssues = $hasIssues;
        $this->hasProjects = $hasProjects;
        $this->hasWiki = $hasWiki;
        $this->hasDownloads = $hasDownloads;
        $this->isTemplate = $isTemplate;
        $this->teamId = $teamId;
        $this->autoInit = $autoInit;
        $this->gitignoreTemplate = $gitignoreTemplate;
        $this->licenseTemplate = $licenseTemplate;
        $this->allowSquashMerge = $allowSquashMerge;
        $this->allowMergeCommit = $allowMergeCommit;
        $this->allowMergeRebase = $allowMergeRebase;
        $this->allowAutoMerge = $allowAutoMerge;
        $this->deleteBranchOnMerge = $deleteBranchOnMerge;
        $this->useSquashPrTitleAsDefault = $useSquashPrTitleAsDefault;
        $this->squashMergeCommitTitle = $squashMergeCommitTitle;
        $this->squashMergeCommitMessage = $squashMergeCommitMessage;
        $this->mergeCommitTitle = $mergeCommitTitle;
        $this->mergeCommitMessage = $mergeCommitMessage;
    }

    public function serialize(): string
    {
        $jsonMap = [];

        $jsonMap['name'] = $this->name;

        if ($this->description !== null) {
            $jsonMap['description'] = $this->description;
        }

        if ($this->homepage !== null) {
            $jsonMap['homepage'] = $this->homepage;
        }

        if ($this->isPrivate) {
            $jsonMap['private'] = $this->isPrivate;
        }

        if ($this->hasIssues) {
            $jsonMap['has_issues'] = $this->hasIssues;
        }

        if ($this->hasProjects) {
            $jsonMap['has_projects'] = $this->hasProjects;
        }

        if ($this->hasWiki) {
            $jsonMap['has_wiki'] = $this->hasWiki;
        }

        if ($this->hasDownloads) {
            $jsonMap['has_downloads'] = $this->hasDownloads;
        }

        if ($this->isTemplate) {
            $jsonMap['is_template'] = $this->isTemplate;
        }

        if ($this->teamId !== -1) {
            $jsonMap['team_id'] = $this->teamId;
        }

        if ($this->autoInit) {
            $jsonMap['auto_init'] = $this->autoInit;
        }

        if ($this->gitignoreTemplate !== null) {
            $jsonMap['gitignore_template'] = $this->gitignoreTemplate;
        }

        if ($this->licenseTemplate !== null) {
            $jsonMap['license_template'] = $this->licenseTemplate;
        }

        if ($this->allowSquashMerge) {
            $jsonMap['allow_squash_merge'] = $this->allowSquashMerge;
        }

        if ($this->allowMergeCommit) {
            $jsonMap['allow_merge_commit'] = $this->allowMergeCommit;
        }

        if ($this->allowMergeRebase) {
            $jsonMap['allow_merge_rebase'] = $this->allowMergeRebase;
        }

        if ($this->allowAutoMerge) {
            $jsonMap['allow_auto_merge'] = $this->allowAutoMerge;
        }

        if ($this->deleteBranchOnMerge) {
            $jsonMap['delete_branch_on_merge'] = $this->deleteBranchOnMerge;
        }

        if ($this->useSquashPrTitleAsDefault) {
            $jsonMap['use_squash_pr_title_as_default'] = $this->useSquashPrTitleAsDefault;
        }

        if ($this->squashMergeCommitTitle !== null) {
            $jsonMap['squash_merge_commit_title'] = $this->squashMergeCommitTitle;
        }

        if ($this->squashMergeCommitMessage !== null) {
            $jsonMap['squash_merge_commit_message'] = $this->squashMergeCommitMessage;
        }

        if ($this->mergeCommitTitle !== null) {
            $jsonMap['merge_commit_title'] = $this->mergeCommitTitle;
        }

        if ($this->mergeCommitMessage !== null) {
            $jsonMap['merge_commit_message'] = $this->mergeCommitMessage;
        }

        return json_encode($jsonMap);
    }
}
