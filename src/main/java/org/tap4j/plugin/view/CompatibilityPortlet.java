package org.tap4j.plugin.view;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.plugins.view.dashboard.DashboardPortlet;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.plugins.view.dashboard.Messages;
import org.acegisecurity.AccessDeniedException;

/**
 * Job statistics - number of jobs with given compatibility status
 *
 * @author Jakub Podlešák
 */
public class CompatibilityPortlet extends DashboardPortlet {

    @DataBoundConstructor
    public CompatibilityPortlet(String name) {
        super(name);
    }

    /**
     * Heath status of the builds (use enum not class for it)
     */
    public enum CompatibilityStatus {

        COMPATIBLE("compatible", "the package is fully compatible"),
        INCOMPATIBLE("incompatible", "incompatible package"),
        COMPATIBILITY_UNKNOWN("n/a", "status unknown");

        private String status;
        private String description;

        CompatibilityStatus(String status, String description) {
            this.status = status;
            this.description = description;
        }

        public static CompatibilityStatus getHealthStatus(Job job) {
            //return job.getFirstBuild() != null ? HEALTH_OVER_80 : HEALTH_UNKNOWN;
            return COMPATIBILITY_UNKNOWN;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }
    }

    private Job<?, ?> getCounterpartJob(Job<?, ?> job) throws AccessDeniedException {
        return ViewHelper.getCounterpartJob(job, "graaljs-graalvm", "nodejs-orig");
    }

    /**
     * Project statistics - number of projects with given health status
     */
    public Map<CompatibilityStatus, Integer> getJobStat(List<TopLevelItem> jobs) {
        SortedMap<CompatibilityStatus, Integer> colStatJobs = new TreeMap<CompatibilityStatus, Integer>();
        for (CompatibilityStatus status : CompatibilityStatus.values()) {
            colStatJobs.put(status, 0);
        }
        // Job and build statistics
        for (TopLevelItem job : jobs) {
            if (job instanceof Job) {
                CompatibilityStatus status = CompatibilityStatus.getHealthStatus(((Job) job));
                colStatJobs.put(status, colStatJobs.get(status) + 1);
            }
        }
        return colStatJobs;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<DashboardPortlet> {

        @Override
        public String getDisplayName() {
            return Messages.Dashboard_JobStatistics();
        }
    }
}
