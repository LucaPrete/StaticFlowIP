package it.garr.staticflowip.web;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class StaticFlowIpWebRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/IP/json", StaticFlowIpResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/wm/staticflowip";
    }
}