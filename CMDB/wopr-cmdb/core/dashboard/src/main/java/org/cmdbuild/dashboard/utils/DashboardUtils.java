package org.cmdbuild.dashboard.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.collect.Streams.stream;
import java.util.List;
import org.cmdbuild.dashboard.DashboardData;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;

public class DashboardUtils {

    public static List<String> getEcqlExprsInOrder(DashboardData dashboard) { //note: this MUST match the order in dashboard serialization (DashboardWs) for ecql filter processing !!
        return listOf(String.class).accept(l -> {
            stream(fromJson(dashboard.getConfig(), JsonNode.class).get("charts").elements()).map(ObjectNode.class::cast).forEach(chart -> {
                if (chart.hasNonNull("dataSourceFilter") && !chart.get("dataSourceFilter").asText().isBlank()) {
                    l.add(chart.get("dataSourceFilter").asText());
                }
                if (chart.hasNonNull("dataSourceParameters")) {
                    ArrayNode dataSourceParameters = (ArrayNode) chart.get("dataSourceParameters");
                    for (JsonNode dataSourceParameter : dataSourceParameters) {
                        if (dataSourceParameter.hasNonNull("filter")) {
                            if (dataSourceParameter.get("filter").hasNonNull("expression")) {
                                if (!dataSourceParameter.get("filter").get("expression").asText().isBlank()) {
                                    l.add(dataSourceParameter.get("filter").get("expression").asText());
                                }
                            }
                        }
                    }
                }
            });
        });
    }

}
