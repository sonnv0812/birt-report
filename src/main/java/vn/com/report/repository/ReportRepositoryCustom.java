package vn.com.report.repository;

import java.util.HashMap;

public interface ReportRepositoryCustom {

    int getTotal(String sql, HashMap<String, Object> hmapParams);

}

