package com.bigdata.datashops.dao.data.service;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

@SuppressWarnings("unchecked")
public abstract class AbstractPagingAndSortingService<T, ID extends Serializable> implements PagingAndSortingService<T, ID> {

    private static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");
    private static final Pattern UTC_DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}Z");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat UTC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected Class<T> clazz;

    @Autowired
    private PagingAndSortingRepository<T, ID> repository;

    protected AbstractPagingAndSortingService() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type clazz1 = pType.getActualTypeArguments()[0];
            if (clazz1 instanceof Class) {
                this.clazz = (Class<T>) clazz1;
            }
        }
    }

    @Override
    public <S extends T> S save(S entity) {
        return repository.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return repository.saveAll(entities);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * 将字符串值转换为实体相关字段对应类型的值
     * @param fieldName
     * @param strVal
     * @return
     */
    protected Object suitValue(String fieldName, String strVal) {
        Class<?> tmpClazz = clazz;
        for (; ; ) {
            try {
                Field field = tmpClazz.getDeclaredField(fieldName);
                if ("java.util.Date".equals(field.getType().getName())) {
                    if (DATE_PATTERN.matcher(strVal).find()) {
                        return DATE_FORMAT.parse(strVal);
                    }
                    if (UTC_DATE_PATTERN.matcher(strVal).find()) {
                        return UTC_DATE_FORMAT.parse(strVal);
                    }
                }
                Constructor constructor = field.getType().getConstructor(String.class);
                return constructor.newInstance(strVal);
            } catch (Exception e) {
                if (e instanceof NoSuchFieldException) {
                    tmpClazz = tmpClazz.getSuperclass();
                    if (tmpClazz == null || tmpClazz == Object.class) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                } else {
                    if (e.getCause() != null) {
                        throw new IllegalArgumentException(e.getCause().getMessage());
                    }
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }
    }

    protected void sort(List<Map<String, Object>> list, String orderField, Sort.Direction orderType) {
        list.sort((a, b) -> {
            String valA = "";
            String valB = "";
            try {
                valA = String.valueOf(a.get(orderField));
                valB = String.valueOf(b.get(orderField));
            } catch (Exception e) {
                //TODO
            }
            return orderType == Sort.Direction.DESC ? -valA.compareTo(valB) : valA.compareTo(valB);
        });
    }

}
