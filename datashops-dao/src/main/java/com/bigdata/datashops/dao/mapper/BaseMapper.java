package com.bigdata.datashops.dao.mapper;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


public interface BaseMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
