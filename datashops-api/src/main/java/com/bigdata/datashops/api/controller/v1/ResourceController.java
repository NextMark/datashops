package com.bigdata.datashops.api.controller.v1;

import static com.bigdata.datashops.common.Constants.RESOURCE_UPLOAD_PATH;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.common.Constants;
import com.bigdata.datashops.common.utils.FileUtils;
import com.bigdata.datashops.common.utils.HadoopUtils;
import com.bigdata.datashops.common.utils.PropertyUtils;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.enums.ResourceType;
import com.bigdata.datashops.model.pojo.hadoop.ResourceFile;

@RestController
@RequestMapping("/v1/res")
public class ResourceController extends BasicController {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @RequestMapping(value = "/uploadJar", method = RequestMethod.POST)
    public Object uploadJar(@RequestParam("jobId") int jobId, @RequestParam("type") int type,
                            @RequestParam("name") String name, @RequestParam("file") MultipartFile file)
            throws IOException {
        //        String path = null;
        //        if (!file.isEmpty()) {
        //            try {
        //                InputStream is = file.getInputStream();
        //                path = AliyunUtils.putByteFile(is, String.format("ds/%s/%s", jobId, file
        //                .getOriginalFilename()));
        //            } catch (Exception e) {
        //                LOG.error("Fail read upload file", e);
        //            }
        //        } else {
        //            LOG.warn("No file in post");
        //        }
        String localPath = FileUtils.writeToLocal(file);
        String prefix = type == ResourceType.SPARK_JAR.getCode() ? "spark" : "flink";
        String hdfsPath =
                String.format("/%s/%s/%s/%s", PropertyUtils.getString(Constants.RESOURCE_UPLOAD_PATH), prefix, jobId,
                        file.getOriginalFilename());
        HadoopUtils.getInstance().copyLocalToHdfs(localPath, hdfsPath, false, false);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setType(type);
        resourceFile.setJobId(jobId);
        resourceFile.setSize(file.getSize());
        resourceFile.setName(name);
        resourceFile.setUrl("hdfs:/" + hdfsPath);
        resourceFileService.save(resourceFile);
        return ok(resourceFile);
    }

    @PostMapping(value = "/getResourceFileList")
    public Result getResourceFileList(@RequestBody DtoPageQuery query) {
        IPage<ResourceFile> res =
                resourceFileService.findList(query.getPageNum(), query.getPageSize(), query.getName());
        Pagination pagination = new Pagination(res);
        return ok(pagination);
    }

    @PostMapping(value = "/deleteResourceFile")
    public Object deleteProject(@RequestBody Map<String, Integer> id) {
        resourceFileService.deleteById(id.get("id"));
        return ok();
    }

    @RequestMapping(value = "/addResourceFile", method = RequestMethod.POST)
    public Object addResourceFile(@RequestParam("type") int type, @RequestParam("name") String name,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        String localPath = FileUtils.writeToLocal(file);
        String hdfsPath = String.format("%s/common/%s/%s/%s", PropertyUtils.getString(RESOURCE_UPLOAD_PATH), type,
                RandomStringUtils.randomNumeric(8), file.getOriginalFilename());
        HadoopUtils.getInstance().copyLocalToHdfs(localPath, hdfsPath, false, false);
        ResourceFile resourceFile = new ResourceFile();
        resourceFile.setType(type);
        resourceFile.setSize(file.getSize());
        resourceFile.setName(name);
        resourceFile.setUrl(PropertyUtils.getString(Constants.FS_DEFAULTFS) + hdfsPath);
        resourceFileService.save(resourceFile);
        return ok(resourceFile);
    }

}
