package com.online.config;

import com.online.dao.RTOOfficerDao;
import com.online.dao.UserDao;
import com.online.dao.impl.LicenseDaoImpl;
import com.online.dao.impl.RTOOfficerDaoImpl;
import com.online.dao.impl.UserDaoImpl;
import com.online.model.User;
import com.online.repository.LicenseRepository;
import com.online.repository.ApplicantRepository;
import com.online.service.*;
import com.online.service.impl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDao userDao() {
        UserDaoImpl dao = new UserDaoImpl();
        // Pre-seed default RTO officer account
        dao.createUser(new User("rto@vlp.com", "rto123"));
        return dao;
    }

    @Bean
    public LicenseDaoImpl licenseDaoImpl() {
        return new LicenseDaoImpl();
    }

    @Bean
    public RTOOfficerDao rtoOfficerDao(LicenseDaoImpl licenseDaoImpl) {
        return new RTOOfficerDaoImpl(licenseDaoImpl.getApplicationStore());
    }

    @Bean
    public UserService userService(UserDao userDao) {
        return new UserServiceImpl(userDao);
    }

    @Bean
    public LicenseService licenseService(
            LicenseDaoImpl licenseDaoImpl,
            @Qualifier("licenseRepository") LicenseRepository licenseRepository,
            ApplicantRepository applicantRepository) {
        return new LicenseServiceImpl(licenseDaoImpl, licenseRepository, applicantRepository);
    }

    @Bean
    public RTOOfficerService rtoOfficerService(RTOOfficerDao rtoOfficerDao, LicenseService licenseService) {
        return new RTOOfficerServiceImpl(rtoOfficerDao, licenseService);
    }

    // US-018: Application Reports
    @Bean
    public ReportService reportService(LicenseDaoImpl licenseDaoImpl) {
        return new ReportServiceImpl(licenseDaoImpl);
    }
}
