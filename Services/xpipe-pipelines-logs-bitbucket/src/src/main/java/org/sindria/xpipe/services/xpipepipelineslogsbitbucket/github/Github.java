package org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github;

import org.json.JSONObject;
import org.sindria.xpipe.services.xpipepipelineslogsbitbucket.github.models.Repository;

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
}
