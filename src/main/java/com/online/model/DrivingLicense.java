package com.online.model;

import java.util.Date;

public class DrivingLicense {

    private String drivingLicenseNumber;
    private Date dateOfIssue;
    private Date validTill;

    public DrivingLicense() {
    }

    public DrivingLicense(String drivingLicenseNumber, Date dateOfIssue, Date validTill) {
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.dateOfIssue = dateOfIssue;
        this.validTill = validTill;
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public Date getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }
}
