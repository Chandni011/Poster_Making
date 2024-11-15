package com.deificdigital.poster_making.responses;

import com.deificdigital.poster_making.models.AllPlans;

import java.util.List;

public class PremiumResponse {
    private int status;
    private List<AllPlans> all_plans;

    public List<AllPlans> getAllPlans() {
        return all_plans;
    }
}