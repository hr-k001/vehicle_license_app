package com.online.config;

import com.online.dao.RTOOfficerDao;
import com.online.dao.UserDao;
import com.online.dao.impl.LicenseDaoImpl;
import com.online.dao.impl.RTOOfficerDaoImpl;
import com.online.dao.impl.UserDaoImpl;
import com.online.model.User;
import com.online.repository.LicenseRepository;
import com.online.repository.ApplicantRepository;
import com.online.repository.ApplicationRepository;
import com.online.repository.UserRepository;
import com.online.service.*;
import com.online.service.impl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserDao userDao(UserRepository userRepository) {
        UserDaoImpl dao = new UserDaoImpl(userRepository);
        // Pre-seed default RTO officer account
        dao.createUser(new User("rto@vlp.com", "rto123", "rto"));
        return dao;
    }

    @Bean
    public LicenseDaoImpl licenseDaoImpl(ApplicationRepository applicationRepository, ApplicantRepository applicantRepository) {
        return new LicenseDaoImpl(applicationRepository, applicantRepository);
    }

    @Bean
    public RTOOfficerDao rtoOfficerDao(LicenseDaoImpl licenseDaoImpl, ApplicationRepository applicationRepository) {
        return new RTOOfficerDaoImpl(licenseDaoImpl.getApplicationStore(), applicationRepository);
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
