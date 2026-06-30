package com.capgemini.dao;

import com.capgemini.model.Application;

public interface LicenseDao {

    String createLLRequest(Application application);

    Application getApplicationById(String applicationNumber);
}
