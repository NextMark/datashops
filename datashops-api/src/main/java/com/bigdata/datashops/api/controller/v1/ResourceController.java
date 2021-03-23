package com.bigdata.datashops.api.controller.v1;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.utils.AliyunUtils;
import com.bigdata.datashops.model.pojo.hadoop.ResourceInfo;

@RestController
@RequestMapping("/v1/res")
public class ResourceController extends BasicController {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @RequestMapping(value = "/uploadJar", method = RequestMethod.POST)
    public Object create(@RequestParam("jobId") int jobId, @RequestParam("file") MultipartFile file) {
        String path = null;
        if (!file.isEmpty()) {
            try {
                InputStream is = file.getInputStream();
                path = AliyunUtils.putByteFile(is, String.format("ds/%s/%s", jobId, file.getOriginalFilename()));
            } catch (Exception e) {
                LOG.error("Fail read upload file", e);
            }
        } else {
            LOG.warn("No file in post");
        }
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setJobId(jobId);
        resourceInfo.setSize(file.getSize());
        resourceInfo.setName(file.getOriginalFilename());
        resourceInfo.setUrl(path);
        resourceInfoService.save(resourceInfo);
        return ok(resourceInfo);
    }
}
