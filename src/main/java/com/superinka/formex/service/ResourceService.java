package com.superinka.formex.service;

import com.superinka.formex.model.Resource;
import com.superinka.formex.payload.request.ResourceRequest;
import com.superinka.formex.payload.response.ResourceResponse;

import java.util.List;

public interface ResourceService {

    List<ResourceResponse> getByCourse(Long courseId);

    Resource create(Long courseId, ResourceRequest request);

    void delete(Long courseId, Long resourceId);
}
