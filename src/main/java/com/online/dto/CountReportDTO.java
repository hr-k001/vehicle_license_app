package com.online.dto;

// US-018: Application Reports
public class CountReportDTO {

    private long totalApplications;
    private long approvedCount;
    private long rejectedCount;
    private long pendingCount;
    private long learnerLicenseCount;
    private long drivingLicenseCount;

    public CountReportDTO() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalApplications, approvedCount, rejectedCount, pendingCount, learnerLicenseCount, drivingLicenseCount;
        public Builder totalApplications(long v) { this.totalApplications = v; return this; }
        public Builder approvedCount(long v) { this.approvedCount = v; return this; }
        public Builder rejectedCount(long v) { this.rejectedCount = v; return this; }
        public Builder pendingCount(long v) { this.pendingCount = v; return this; }
        public Builder learnerLicenseCount(long v) { this.learnerLicenseCount = v; return this; }
        public Builder drivingLicenseCount(long v) { this.drivingLicenseCount = v; return this; }
        public CountReportDTO build() {
            CountReportDTO d = new CountReportDTO();
            d.totalApplications = totalApplications;
            d.approvedCount = approvedCount;
            d.rejectedCount = rejectedCount;
            d.pendingCount = pendingCount;
            d.learnerLicenseCount = learnerLicenseCount;
            d.drivingLicenseCount = drivingLicenseCount;
            return d;
        }
    }

    public long getTotalApplications() { return totalApplications; }
    public long getApprovedCount() { return approvedCount; }
    public long getRejectedCount() { return rejectedCount; }
    public long getPendingCount() { return pendingCount; }
    public long getLearnerLicenseCount() { return learnerLicenseCount; }
    public long getDrivingLicenseCount() { return drivingLicenseCount; }
}
