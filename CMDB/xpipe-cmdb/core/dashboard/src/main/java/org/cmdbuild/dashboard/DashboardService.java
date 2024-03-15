package org.cmdbuild.dashboard;

import java.util.List;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;

public interface DashboardService {

    List<DashboardData> getAll();

    List<DashboardData> getForCurrentUser();

    List<DashboardData> getActiveForCurrentUser();

    DashboardData getOne(long id);

    DashboardData getOneForUser(long id);

    DashboardData getByCode(String code);

    DashboardData getForUserByName(String code);

    DashboardData create(DashboardData data);

    DashboardData update(DashboardData dashboard);

    void delete(long id);

    boolean isActiveAndAccessibleByCode(String code);

    default DashboardData getByIdOrCode(String idOrCode) {
        if (isNumber(idOrCode)) {
            return getOne(toLong(idOrCode));
        } else {
            return getByCode(idOrCode);
        }
    }

    default DashboardData getForUserByIdOrCode(String idOrCode) {
        if (isNumber(idOrCode)) {
            return getOneForUser(toLong(idOrCode));
        } else {
            return getForUserByName(idOrCode);
        }
    }

}
