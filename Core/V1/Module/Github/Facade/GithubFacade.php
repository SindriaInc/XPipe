<?php
namespace Core\Github\Facade;

use Core\Github\Helper\GithubHelper;
use Core\Github\Model\Variable;

class GithubFacade
{
    protected static function client(): GithubHelper
    {
        return GithubHelper::getInstance();
    }

    // Metodo magico per fallback automatico
    public static function __callStatic($name, $arguments)
    {
        $client = self::client();
        if (method_exists($client, $name)) {
            return call_user_func_array([$client, $name], $arguments);
        }

        throw new \BadMethodCallException("Method $name does not exist on client");
    }



    public static function listOrganizationRepositories(string $organization): \Laminas\Http\Response
    {
        $uri = "orgs/" . $organization  . "/repos";
        return self::client()->get($uri);
    }

    public static function getARepository(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository;
        return self::client()->get($uri);
    }




    // Actions/Variables

    public static function listRepositoryVariable(string $organization, string $repository): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables";
        return self::client()->get($uri);
    }

    public static function createARepositoryVariable(
        string $organization,
        string $repository,
        string $variableKey,
        string $variableValue
    ): \Laminas\Http\Response
    {
        $payload = new Variable($variableKey, $variableValue);
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables";
        return self::client()->postRaw($uri, $payload->serialize());
    }

    public static function getARepositoryVariable(string $organization, string $repository, string $variableKey): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->get($uri);
    }

    public static function updateARepositoryVariable(
        string $organization,
        string $repository,
        string $variableKey,
        string $variableValue
    ): \Laminas\Http\Response
    {
        $payload = new Variable($variableKey, $variableValue);
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->patchRaw($uri, $payload->serialize());
    }

    public static function deleteARepositoryVariable(string $organization, string $repository, string $variableKey): \Laminas\Http\Response
    {
        $uri = "repos/" . $organization . "/" . $repository . "/actions/variables/" . $variableKey;
        return self::client()->delete($uri);
    }

    // Actions/Artifacts

//    public static Object listArtifactsForARepository(String organization, String repoName) {
//        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/artifacts");
//    }
//
//public static Object getAnArtifact(String organization, String repoName, String artifactId) {
//    return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId);
//}
//
//    public static Object deleteAnArtifact(String organization, String repoName, String artifactId) {
//    return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/artifacts/" + artifactId);
//}
//
//    public static Object listWorkflowRunArtifact(String organization, String repoName, String workflowRunId) {
//    return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/runs/" + workflowRunId + "/artifacts");
//}







    // Metodi espliciti
//    public static function get(string $uri, array $headers = [], array $params = []): \Laminas\Http\Response
//    {
//        return self::client()->get($uri, $headers, $params);
//    }
//
//    public static function post(string $uri, array $headers = [], array $payload = []): \Laminas\Http\Response
//    {
//        return self::client()->post($uri, $headers, $payload);
//    }
//
//    public static function put(string $uri, array $headers = [], array $payload = []): \Laminas\Http\Response
//    {
//        return self::client()->put($uri, $headers, $payload);
//    }
//
//    public static function delete(string $uri, array $headers = []): \Laminas\Http\Response
//    {
//        return self::client()->delete($uri, $headers);
//    }
//
//    public static function postRaw(string $uri, array $headers = [], string $payload = ""): \Laminas\Http\Response
//    {
//        return self::client()->postRaw($uri, $headers, $payload);
//    }
//
//    public static function putRaw(string $uri, array $headers = [], string $payload = ""): \Laminas\Http\Response
//    {
//        return self::client()->putRaw($uri, $headers, $payload);
//    }
//
//    public static function deleteRaw(string $uri, array $headers = [], string $payload = ""): \Laminas\Http\Response
//    {
//        return self::client()->deleteRaw($uri, $headers, $payload);
//    }


}
