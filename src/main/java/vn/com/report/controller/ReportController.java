package vn.com.report.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.com.report.birt.BirtReportGenerator;
import vn.com.report.birt.ReportParameter;
import vn.com.report.dto.ReportResponseDTO;
import vn.com.report.dto.ReportStatusDTO;
import vn.com.report.util.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping(value = Constant.REQUEST_MAPPING_V1)
public class ReportController {

    private final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    BirtReportGenerator birtReportGenerator;

    @Value("${template-file-path}")
    String templateFilePath;

    @RequestMapping(value = "report/{reportName}/{type}/{hasPaging}", method = RequestMethod.POST)
    public ResponseEntity<Resource> report(
            @PathVariable("hasPaging") Boolean hasPaging,
            @PathVariable("type") String type,
            @PathVariable("reportName") String reportName,
            @RequestBody Map<String, Object> maps) throws Exception {

        logger.info("Get data from report " + reportName);
        try {
            String sourceFile = FilterUtil.fileNameFilter(reportName) + ".rptdesign";
            String tempFilePath = System.getProperty("user.dir") + File.separator + "template" + File.separator + sourceFile;
            ReportParameter rm = new ReportParameter(tempFilePath, type);
            if (maps != null) {
                Set set = maps.entrySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry) iterator.next();
                    rm.setParameter(mentry.getKey().toString(), mentry.getValue());
                }
            }
            if ("html".equalsIgnoreCase(type)) {
                rm.setParameter("isHideTableTitle", true);
            }

            ReportResponseDTO responseEntity = new ReportResponseDTO();
            ReportStatusDTO messEntity = new ReportStatusDTO();
            messEntity.setCode(10);
            messEntity.setDescription("");

            if ("html".equalsIgnoreCase(type) && hasPaging) {
                int count = birtReportGenerator.getCountData(rm);
                responseEntity.setData(count);
            }
            if ("xlsx".equalsIgnoreCase(type)) {
                if (rm.getParameter().containsKey("pagesize") && rm.getParameter().get("pagesize") != null) {
                    Object pageSize = rm.getParameter().get("pagesize");
                    if (pageSize instanceof String) {
                        rm.setParameter("pagesize", Integer.MAX_VALUE + "");
                    } else {
                        rm.setParameter("pagesize", Integer.MAX_VALUE);
                    }
                }
                if (rm.getParameter().containsKey("page") && rm.getParameter().get("page") != null) {
                    Object page = rm.getParameter().get("page");
                    if (page instanceof String) {
                        rm.setParameter("page", "0");
                    } else {
                        rm.setParameter("page", 0);
                    }
                }
            }
            rm.setParameter("reportType", type.toUpperCase());
            messEntity.setCode(1);
            messEntity.setDescription("OK");
            ByteArrayOutputStream outputStream = birtReportGenerator.generate(rm);
            ByteArrayResource byteArrayResource = new ByteArrayResource(outputStream.toByteArray());
            responseEntity.setStatus(messEntity);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Disposition", "attachment;filename=" + "report_" + System.currentTimeMillis() + "." + type);
            headers.set("Content-Response", (FnCommon.convertObjectToStringJson(responseEntity)));
            headers.add("Access-Control-Expose-Headers", "Content-Response");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(byteArrayResource);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @RequestMapping(value = "/report/upload-template", method = RequestMethod.POST)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get the file and save it to the server
            String fileUploadPath = System.getProperty("user.dir") + File.separator + "template";
            Files.copy(file.getInputStream(), Paths.get(fileUploadPath + "/" + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}
