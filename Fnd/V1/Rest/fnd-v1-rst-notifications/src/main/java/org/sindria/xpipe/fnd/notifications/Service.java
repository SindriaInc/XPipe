package org.sindria.xpipe.fnd.notifications;

import org.sindria.xpipe.fnd.notifications.jobs.BellJob;
import org.sindria.xpipe.fnd.notifications.jobs.EmailJob;
import org.sindria.xpipe.fnd.notifications.jobs.TelgramJob;
import org.sindria.xpipe.lib.nanoREST.services.BaseService;

import org.json.JSONObject;

import java.util.List;
import org.sindria.xpipe.lib.nanoREST.job.JobDispatcher;



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