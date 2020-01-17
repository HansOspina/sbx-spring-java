package com.sbxcloud.java.spring.starter.sbxcore;


import com.sbxcloud.java.spring.starter.sbxcore.dao.SbxCoreRepository;
import com.sbxcloud.java.spring.starter.sbxcore.domain.SbxResponse;
import com.sbxcloud.java.spring.starter.sbxcore.querybuilder.QueryBuilder;
import com.sbxcloud.java.spring.starter.sbxcore.util.SBXModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SbxCore {

    public static Environment environment = new Environment();
    private static HashMap<String, String> urls = new HashMap<>();

    private static Logger LOG = LoggerFactory.getLogger(SbxCore.class);


    private final SbxCoreRepository sbxCoreRepository;

    static {
        urls.put("updatePassword", "/user/v1/password");
        urls.put("login", "/user/v1/login");
        urls.put("register", "/user/v1/register");
        urls.put("validate", "/user/v1/validate");
        urls.put("row", "/data/v1/row");
        urls.put("find", "/data/v1/row/find");
        urls.put("update", "/data/v1/row/update");
        urls.put("delete", "/data/v1/row/delete");
        urls.put("downloadFile", "/content/v1/download");
        urls.put("uploadFile", "/content/v1/upload");
        urls.put("addFolder", "/content/v1/folder");
        urls.put("folderList", "/content/v1/folder");
        urls.put("sendMail", "/email/v1/send");
        urls.put("paymentCustomer", "/payment/v1/customer");
        urls.put("paymentCard", "/payment/v1/card'");
        urls.put("paymentToken", "/payment/v1/token");
        urls.put("password", "/user/v1/password/request");
        urls.put("cloudScriptRun", "/cloudscript/v1/run");
    }

    public static String getUrl(String key) {
        return SbxCore.environment.getBaseUrl() + SbxCore.urls.get(key);
    }

    public SbxCore(Integer domain, String appKey, SbxCoreRepository sbxCoreRepository) {

        environment.setDomain(domain);
        environment.setAppKey(appKey);
      this.sbxCoreRepository = sbxCoreRepository;
    }


    public <T> Find<T> find(Class<T> clazz, String token) {
        return new Find<>(clazz, sbxCoreRepository, token);
    }


    @SuppressWarnings("unchecked")
    public <T> Mono<SbxResponse<T>> upsert(List<T> data, String token) {
        Class<T> clazz = ((Class<T>) ((ParameterizedType) data.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        String model = clazz.getAnnotation(SBXModel.class).value();

        String body = new QueryBuilder().setModel(model).setDomain(SbxCore.environment.getDomain()).addObjectArray(data).compile();
        return sbxCoreRepository.upsert(clazz, body, token);
    }

    public <T> Mono<SbxResponse<T>>  upsert(T object, String token) {
      return upsert(Collections.singletonList(object), token);
    }

}