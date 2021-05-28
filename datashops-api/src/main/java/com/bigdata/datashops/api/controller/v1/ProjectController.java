package com.bigdata.datashops.api.controller.v1;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigdata.datashops.api.common.Pagination;
import com.bigdata.datashops.api.controller.BasicController;
import com.bigdata.datashops.api.response.Result;
import com.bigdata.datashops.dao.data.domain.PageRequest;
import com.bigdata.datashops.model.dto.DtoPageQuery;
import com.bigdata.datashops.model.pojo.DataSource;
import com.bigdata.datashops.model.pojo.Project;
import com.bigdata.datashops.model.pojo.YarnQueue;

@RestController
@RequestMapping("/v1/project")
public class ProjectController extends BasicController {
    @PostMapping(value = "/addProject")
    public Result addDependency(@RequestBody Project project) {
        projectService.save(project);
        return ok();
    }

    @PostMapping(value = "/deleteProject")
    public Object deleteProject(@RequestBody Map<String, Integer> id) {
        projectService.deleteById(id.get("id"));
        return ok();
    }

    @PostMapping(value = "/getProjectList")
    public Result getProjectList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder();
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append("name?").append(query.getName());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<Project> projectList = projectService.getList(pageRequest);
        Pagination pagination = new Pagination(projectList);
        return ok(pagination);
    }

    @PostMapping(value = "/addQueue")
    public Result addQueue(@RequestBody YarnQueue yarnQueue) {
        yarnQueueService.save(yarnQueue);
        return ok();
    }

    @PostMapping(value = "/deleteQueue")
    public Object deleteQueue(@RequestBody Map<String, Integer> id) {
        yarnQueueService.deleteById(id.get("id"));
        return ok();
    }

    @PostMapping(value = "/getQueueList")
    public Result getQueueList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder();
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append("name?").append(query.getName());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<YarnQueue> projectList = yarnQueueService.getList(pageRequest);
        Pagination pagination = new Pagination(projectList);
        return ok(pagination);
    }

    @PostMapping(value = "/addDataSource")
    public Result addDataSource(@RequestBody DataSource dataSource) {
        dataSourceService.save(dataSource);
        return ok();
    }

    @PostMapping(value = "/deleteDataSource")
    public Object deleteDatasource(@RequestBody Map<String, Integer> id) {
        dataSourceService.deleteById(id.get("id"));
        return ok();
    }

    @PostMapping(value = "/getDataSourceList")
    public Result getDataSourceList(@RequestBody DtoPageQuery query) {
        StringBuilder filter = new StringBuilder();
        if (StringUtils.isNoneBlank(query.getName())) {
            filter.append("name?").append(query.getName());
        }
        PageRequest pageRequest =
                new PageRequest(query.getPageNum() - 1, query.getPageSize(), filter.toString(), Sort.Direction.DESC,
                        "createTime");
        Page<DataSource> projectList = dataSourceService.getList(pageRequest);
        Pagination pagination = new Pagination(projectList);
        return ok(pagination);
    }

    //    @PostMapping(value = "/getMasterList")
    //    public Result getMasterList() {
    //        boolean workerExist = zookeeperOperator.isExisted(ZKUtils.getMasterRegistryPath());
    //        if (!workerExist) {
    //            return ok();
    //        }
    //        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getMasterRegistryPath());
    //        List<Host> hosts = Lists.newArrayList();
    //        for (String host : hostsStr) {
    //            String[] hostInfo = host.split(Constants.SEPARATOR_UNDERLINE);
    //            Host h = new Host();
    //            h.setIp(hostInfo[0]);
    //            h.setPort(Integer.parseInt(hostInfo[1]));
    //            hosts.add(h);
    //        }
    //        return ok(hosts);
    //    }
    //
    //    @PostMapping(value = "/getWorkerList")
    //    public Result getWorkerList() {
    //        boolean workerExist = zookeeperOperator.isExisted(ZKUtils.getWorkerRegistryPath());
    //        if (!workerExist) {
    //            return ok();
    //        }
    //        List<String> hostsStr = zookeeperOperator.getChildrenKeys(ZKUtils.getWorkerRegistryPath());
    //        List<Host> hosts = Lists.newArrayList();
    //        for (String host : hostsStr) {
    //            String[] hostInfo = host.split(Constants.SEPARATOR_UNDERLINE);
    //            Host h = new Host();
    //            h.setIp(hostInfo[0]);
    //            h.setPort(Integer.parseInt(hostInfo[1]));
    //            hosts.add(h);
    //        }
    //        return ok(hosts);
    //    }
}
