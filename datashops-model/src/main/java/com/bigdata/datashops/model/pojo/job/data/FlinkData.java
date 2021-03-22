package com.bigdata.datashops.model.pojo.job.data;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class FlinkData {
    private String p;

    private String version;

    private String ynm;

    private String c;

    private String yq;

    private String yjm;

    private String ytm;

    private String ys;

    private String yn;

    private String extension;

    public List<String> buildArgs() {
        List<String> args = Lists.newArrayList();
        args.add("run");
        args.add("-m");
        args.add("yarn-cluster");
        args.add("-d");
        if (StringUtils.isNotEmpty(ynm)) {
            args.add("-ynm");
            args.add(ynm);
        }
        if (StringUtils.isNotEmpty(yq)) {
            args.add("-yq");
            args.add(yq);
        }
        if (StringUtils.isNotEmpty(yjm)) {
            args.add("-yjm");
            args.add(yjm);
        }
        if (StringUtils.isNotEmpty(ytm)) {
            args.add("-ytm");
            args.add(ytm);
        }
        if (StringUtils.isNotEmpty(yn)) {
            args.add("-yn");
            args.add(yn);
        }
        if (StringUtils.isNotEmpty(ys)) {
            args.add("-ys");
            args.add(ys);
        }
        if (StringUtils.isNotEmpty(c)) {
            args.add("-c");
            args.add(c);
        }
        if (StringUtils.isNotEmpty(p)) {
            args.add("-p");
            args.add(p);
        }
        if (StringUtils.isNotEmpty(extension)) {
            args.add(extension);
        }
        args.add("-j");
        return args;
    }

}
