package com.capgemini.dao;

import com.capgemini.model.Application;
import com.capgemini.model.DrivingLicense;

public interface RTOOfficerDao {

    Application getApplicationById(String applicationNumber);

    String updateApplicationById(String applicationNumber, Application application);

    DrivingLicense createLearnerLicense(String applicationNumber);
}
