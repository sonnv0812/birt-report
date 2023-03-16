package vn.com.report.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Repository
public class ReportRepositoryImpl implements ReportRepositoryCustom {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(ReportRepositoryImpl.class);

    @Override
    public int getTotal(String queryString, HashMap<String, Object> hmapParams) {
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append("Select count(*) From (");
            strBuild.append(queryString);
            strBuild.append(") tbcount");
            if(hmapParams != null && hmapParams.containsKey("interval")){
                strBuild = new StringBuilder();
                strBuild.append("Select count(*) From (");
                strBuild.append(queryString.replace(":interval", hmapParams.get("interval").toString()));
                strBuild.append(") tbcount");
            }
            Query query = this.entityManager.createNativeQuery(strBuild.toString());
            if (hmapParams != null) {
                Set set = hmapParams.entrySet();
                Map.Entry mentry;
                Object value;
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    try {
                        mentry = (Map.Entry) iterator.next();
                        value = mentry.getValue();
                        if (value == null) {
                            value = "";
                        }
                        if ("direction".equals(mentry.getKey().toString())) continue;
                        query.setParameter(mentry.getKey().toString(), value);
                    } catch (Exception ex) {
                        //Skip on missing parameter
                    }
                }
            }

            List resultQr = query.getResultList();
            if (resultQr != null && resultQr.size() > 0) {
                Object value = resultQr.get(0);
                String result = String.valueOf(value);
                this.entityManager.close();
                return Integer.valueOf(result);
            } else {
                return 0;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }

}