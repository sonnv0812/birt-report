package vn.com.report.birt;

import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.engine.api.impl.ReportRunnable;
import org.eclipse.birt.report.model.api.OdaDataSetHandle;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.report.repository.ReportRepositoryCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.*;

@Component
public class BirtReportGenerator {

    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Autowired
    private IReportEngine birtEngine;

    @Autowired
    private ReportRepositoryCustom reportRepository;

    public ByteArrayOutputStream generate(ReportParameter rptParam) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(rptParam.getReportPath());
        IReportRunnable runnable = birtEngine.openReportDesign(fis);
        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(runnable);
        entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        Session hibernateSession = entityManager.unwrap(Session.class);
        hibernateSession.doWork(connection -> runAndRenderTask.getAppContext().put("OdaJDBCDriverPassInConnection", connection));
        runAndRenderTask.setParameterValues(setParameters(runnable, rptParam.getParameter()));
        RenderOption options;
        if ("pdf".equalsIgnoreCase(rptParam.getFormat())) {
            options = new PDFRenderOption();
        } else if ("xlsx".equalsIgnoreCase(rptParam.getFormat())) {
            options = new EXCELRenderOption();
            options.setEmitterID("uk.co.spudsoft.birt.emitters.excel.XlsxEmitter");
        } else {
            options = new HTMLRenderOption();
            ((HTMLRenderOption) options).setEmbeddable(true);
            ((HTMLRenderOption) options).setImageDirectory("./chart");
            ((HTMLRenderOption) options).setBaseImageURL("https://app.alohub.vn:6666/");
        }
        options.setOutputFormat(rptParam.getFormat().toLowerCase());
        options.setOutputStream(baos);
        runAndRenderTask.setRenderOption(options);
        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, this.getClass().getClassLoader());
        runAndRenderTask.run();
        runAndRenderTask.close();
        fis.close();
        return baos;
    }

    private HashMap<String, Object> setParameters(IReportRunnable report, Map<String, Object> m) {
        HashMap<String, Object> parms = new HashMap<>();
        IGetParameterDefinitionTask task = birtEngine.createGetParameterDefinitionTask(report);
        Collection<IParameterDefnBase> params = task.getParameterDefns(true);
        Iterator<IParameterDefnBase> iter = params.iterator();
        while (iter.hasNext()) {
            IParameterDefnBase param = iter.next();
            Object val = m.get(param.getName());
            parms.put(param.getName(), val);
        }
        task.close();
        return parms;
    }

    public int getCountData(ReportParameter rptParam) throws Exception {
        FileInputStream fis = new FileInputStream(rptParam.getReportPath());
        IReportRunnable runnable = birtEngine.openReportDesign(fis);
        String queryString = "";
        List<OdaDataSetHandle> dataSets = ((ReportRunnable) runnable).getReport().getAllDataSets();
        for (OdaDataSetHandle odaDataSet : dataSets) {
            if ("Data Source".equals(odaDataSet.getDataSourceName())) {
                queryString = odaDataSet.getQueryText();
            }
        }
        HashMap<String, Object> hmapParams = (HashMap<String, Object>) ((HashMap<String, Object>) rptParam.getParameter()).clone();
        hmapParams.remove("isHideTableTitle");
        hmapParams.remove("language");
        hmapParams.remove("toTimeString");
        hmapParams.remove("fromTimeString");
        if(hmapParams.containsKey("startrecord"))
            hmapParams.put("startrecord", null);
        if(hmapParams.containsKey("pagesize"))
            hmapParams.put("pagesize", null);

        // remove param to view in birt
        hmapParams.remove("USER_NAME");
        hmapParams.remove("VIEW_FROM_DATE");
        hmapParams.remove("VIEW_TO_DATE");
        hmapParams.remove("VIEW_LST_QUEUE");
        hmapParams.remove("VIEW_PERIOD");
        hmapParams.remove("VIEW_LINE");
        hmapParams.remove("VIEW_LST_AGENT");
        hmapParams.remove("VIEW_LINE");
        hmapParams.remove("VIEW_TICKET_TYPE");
        hmapParams.remove("VIEW_LSTAGENT");
        hmapParams.remove("VIEW_SOURCE_NAME");
        hmapParams.remove("lstSourceName");
        hmapParams.remove("channelName");
        hmapParams.remove("username");
        hmapParams.remove("rankName");
        hmapParams.remove("menuName");
        fis.close();
        return reportRepository.getTotal(queryString, hmapParams);
    }
}
