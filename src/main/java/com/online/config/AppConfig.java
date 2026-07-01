package com.online.config;

import com.online.dao.RTOOfficerDao;
import com.online.dao.UserDao;
import com.online.dao.impl.LicenseDaoImpl;
import com.online.dao.impl.RTOOfficerDaoImpl;
import com.online.dao.impl.UserDaoImpl;
import com.online.service.LicenseService;
import com.online.service.RTOOfficerService;
import com.online.service.UserService;
import com.online.service.impl.LicenseServiceImpl;
import com.online.service.impl.RTOOfficerServiceImpl;
import com.online.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }

    @Bean
    public LicenseDaoImpl licenseDaoImpl() {
        return new LicenseDaoImpl();
    }

    // RTOOfficerDao shares the same in-memory application store as LicenseDao
    // so officer actions operate on applicant-submitted records
    @Bean
    public RTOOfficerDao rtoOfficerDao(LicenseDaoImpl licenseDaoImpl) {
        return new RTOOfficerDaoImpl(licenseDaoImpl.getApplicationStore());
    }

    @Bean
    public UserService userService(UserDao userDao) {
        return new UserServiceImpl(userDao);
    }

    @Bean
    public LicenseService licenseService(LicenseDaoImpl licenseDaoImpl) {
        return new LicenseServiceImpl(licenseDaoImpl);
    }

    @Bean
    public RTOOfficerService rtoOfficerService(RTOOfficerDao rtoOfficerDao) {
        return new RTOOfficerServiceImpl(rtoOfficerDao);
    }
}
