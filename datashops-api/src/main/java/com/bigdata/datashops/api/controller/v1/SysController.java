package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.rpc.OSInfo;
import com.bigdata.datashops.model.pojo.user.SysOperation;
import com.bigdata.datashops.server.utils.ZKUtils;
import com.google.common.collect.Lists;

@RestController
@RequestMapping("/v1/sys")
public class SysController extends BasicController {
    @PostMapping(value = "/getSysOperations")
    public Object getSysOperations(@RequestBody DtoPageQuery query) {
        IPage<SysOperation> projectList =
                sysOperationService.findList(query.getPageNum(), query.getPageSize(), query.getName());
        Pagination pagination = new Pagination(projectList);
        return ok(pagination);

    }

    @PostMapping(value = "/getServerInfo")
    public Object getServerInfo() {
        try {
            List<String> masters = zookeeperOperator.getChildrenKeys(ZKUtils.getMasterRegistryPath());
            List<String> workers = zookeeperOperator.getChildrenKeys(ZKUtils.getWorkerRegistryPath());
            List<OSInfo> nodes = Lists.newArrayList();

            for (String node : masters) {
                String value = zookeeperOperator.get(ZKUtils.getMasterRegistryPath() + "/" + node);
                OSInfo osInfo = JSONUtils.parseObject(value, OSInfo.class);
                nodes.add(osInfo);
            }
            for (String node : workers) {
                String value = zookeeperOperator.get(ZKUtils.getWorkerRegistryPath() + "/" + node);
                OSInfo osInfo = JSONUtils.parseObject(value, OSInfo.class);
                nodes.add(osInfo);
            }
            return ok(nodes);
        } catch (Exception e) {
            return ok();
        }
    }
}
