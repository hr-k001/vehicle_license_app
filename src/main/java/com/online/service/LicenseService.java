package com.capgemini.service;

import com.capgemini.model.Application;
import com.capgemini.model.ApplicationStatus;

public interface LicenseService {

    String applyForLL(Application application);

    ApplicationStatus viewLLStatus(String applicationNumber);
}
