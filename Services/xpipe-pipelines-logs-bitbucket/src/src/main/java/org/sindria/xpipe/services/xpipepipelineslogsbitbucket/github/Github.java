package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github;

import org.json.JSONObject;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Repository;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Variable;

public class Github {

    // Repositories

    public static Object listOrganizationRepositories(String organization){
        return GithubHelper.get("/orgs/" + organization + "/repos");
    }

    public static Object getARepository(String organization, String repository){
        return GithubHelper.get("/repos/" + organization + "/" + repository);
    }

    // Basic create repository payload, see model for additional parameters
    public static JSONObject createAnOrganizationRepository(String organization, String name) {

        Repository payload = new Repository(name);

        return GithubHelper.post("/orgs/" + organization + "/repos", payload.serialize());

    }

    // Basic update repository payload, see model for additional parameters
    public static JSONObject updateARepository(String organization, String name) {

        Repository payload = new Repository(name);

        return GithubHelper.patch("/orgs/" + organization + "/repos", payload.serialize());
    }

    public static JSONObject deleteARepository(String organization, String name) {
        return GithubHelper.delete("/repos/" + organization + "/" + name);
    }


    // Actions/Variables

    public static Object listRepositoryVariable(String organization, String repoName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/variables");
    }
    public static JSONObject createARepositoryVariable(String organization, String repoName, String variableName, String variableValue) {

        Variable payload = new Variable(variableName, variableValue);
        return GithubHelper.post("/repos/" + organization + "/" + repoName + "/actions/variables", payload.serialize());
    }

    public static Object getARepositoryVariable(String organization, String repoName, String variableName) {
        return GithubHelper.get("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName);
    }

    public static JSONObject updateARepositoryVariable(String organization, String repoName, String variableName, String variableValue) {

        Variable payload = new Variable(variableName, variableValue);

        return GithubHelper.patch("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName, payload.serialize());
    }

    public static JSONObject deleteARepositoryVariable(String organization, String repoName, String variableName) {
        return GithubHelper.delete("/repos/" + organization + "/" + repoName + "/actions/variables/" + variableName);
    }
}
