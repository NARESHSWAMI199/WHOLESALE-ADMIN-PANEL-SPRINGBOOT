package com.sales.admin.services;


import com.sales.entities.ServicePlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicePlanService extends  RepoContainer {

    public List<ServicePlan> getALlServicePlan(){
        return servicePlanRepository.findAll();
    }

    public ServicePlan findBySlug(String slug){
        return servicePlanRepository.findBySlug(slug);
    }

}
