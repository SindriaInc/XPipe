package org.sindria.xpipe.lib.blog;

import org.sindria.xpipe.lib.nanoREST.services.BaseService;

import org.json.JSONObject;

import java.util.List;

public class Service extends BaseService {

    /**
     * Service constructor
     */
    public Service() {
        super(new Repository());
    }

    /**
     * Get all latest competitions by createdAt
     */
    public JSONObject getCompetitions(String createdAt) {


        List<List<String>> queryResults = this.repository.query("select Host,User from mysql.user;");
        for (List<String> row : queryResults) {
            System.out.println(row);
        }

        String origin = "https://www.federtennis.it/";

        String json = "{\"guid\":\"\",\"profilazione\":\"\",\"freetext\":null,\"id_regione\":20,\"id_provincia\":90,\"id_stato\":null,\"id_disciplina\":4332,\"sesso\":null,\"data_inizio\": \""+createdAt+"\",\"data_fine\":null,\"tipo_competizione\":null,\"categoria_eta\":null,\"classifica\":null,\"massimale_montepremi\":null,\"id_area_regionale\":null,\"ambito\":null,\"rowstoskip\":0,\"fetchrows\":25,\"sortcolumn\":\"data_inizio\",\"sortorder\":\"asc\"}";
        JSONObject data = new JSONObject(json);

        return Helper.post("/api/v3/integration/puc/list", origin, data.toString());
    }
}