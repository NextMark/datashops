package com.bigdata.datashops.api.controller.v1;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.common.utils.JSONUtils;
import com.bigdata.datashops.dao.data.domain.PageRequest;
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
        StringBuilder filter = new StringBuilder();
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append("path?").append(query.getName());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<SysOperation> projectList = sysOperationService.getList(pageRequest);
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
