package org.sindria.xpipe.fnd.v1.notifications;

import org.sindria.xpipe.fnd.v1.notifications.job.BellJob;
import org.sindria.xpipe.fnd.v1.notifications.job.EmailJob;
import org.sindria.xpipe.fnd.v1.notifications.job.TelgramJob;

import org.sindria.xpipe.core.lib.nanorest.service.BaseService;

import org.json.JSONObject;

import org.sindria.xpipe.core.lib.nanorest.job.JobDispatcher;



public class Service  {


    protected final JobDispatcher jobDispatcher;

    public Service() {
        this.jobDispatcher = new JobDispatcher();
    }


    public void handle(Payload payload) {

        int selectedChannel = Strategy.selectChannel(payload.getChannel());

        //TODO: change the job name dinamically with uuid bell-<uuid>

        switch (selectedChannel) {
            case 0:
                this.jobDispatcher.submitJob(new BellJob("Bell Job", 2, payload));
                break;
            case 1:
                this.jobDispatcher.submitJob(new TelgramJob("Telegram Job", 2, payload));
                break;
            case 2:
                this.jobDispatcher.submitJob(new EmailJob("Email Job", 2, payload));
                break;
            default:
                this.jobDispatcher.submitJob(new BellJob("Bell Job", 2, payload));
                break;

        }
    }

}