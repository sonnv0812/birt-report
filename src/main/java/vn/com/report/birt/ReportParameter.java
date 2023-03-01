package vn.com.report.birt;

import java.util.HashMap;
import java.util.Map;

public class ReportParameter {
    private String reportPath;
    private Map<String, Object> params;
    private String format;

    public ReportParameter(String reportPath, String format) {
        this.reportPath = reportPath;
        this.format = format;
        params = new HashMap<>();
    }

    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setParameter(String key, Object val) {
        this.params.put(key, val);
    }

    public Object getParameter(String key) {
        return this.params.get(key);
    }

    public Map<String, Object> getParameter() {
        return this.params;
    }
}
