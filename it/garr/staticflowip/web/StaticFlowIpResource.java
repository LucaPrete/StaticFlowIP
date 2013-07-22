package it.garr.staticflowip.web;


import it.garr.staticflowip.IStaticFlowIpService;

import java.io.IOException;

import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFlowIpResource extends ServerResource {
	
	private String IPSrc;
	private String IPDst;
	protected Logger logger = LoggerFactory.getLogger(StaticFlowIpResource.class);	
	
    @Post
    public String handlePost(String fmJson) throws IOException {
    	IStaticFlowIpService service = (IStaticFlowIpService) getContext().getAttributes().get(IStaticFlowIpService.class.getCanonicalName());
        try {
            jsonToStringIP(fmJson);
        } catch (IOException e) {
        	logger.error("Errore nella conversione"); 
            return "{\"status\" : \"Error!!!!Could not parse IP, see log for details.\"}";
        }
            IDevice source = deviceSearch(IPSrc);
            IDevice dest = deviceSearch(IPDst);
            service.callToStatic(source, dest, IPSrc, IPDst); 
        return ("{\"status\" : WORK \"}");
    }


    private IDevice deviceSearch(String IP){
    	IDeviceService deviceManager = 
                (IDeviceService)getContext().getAttributes().
                    get(IDeviceService.class.getCanonicalName());
    	for (IDevice D : deviceManager.getAllDevices())
        {
        	if(D.toString().contains(IP))
        	{
        		return D;
        	}
    }
    	return null;
    }
    
	private void jsonToStringIP(String fmJson) throws IOException {
        MappingJsonFactory f = new MappingJsonFactory();
        JsonParser jp;
        try {
            jp = f.createJsonParser(fmJson);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
        jp.nextToken();
        if (jp.getText() != "{") {
        	
            throw new IOException("Expected START_ARRAY");
        }
        jp.nextToken();
        if (jp.getText() == "IPSrc")
        {
        	jp.nextToken();
        	IPSrc = jp.getText();
        }else {throw new IOException("Expected IPDst");}
        jp.nextToken();
        if (jp.getText() == "IPDst")
        {
        	jp.nextToken();
        	IPDst= jp.getText();
        }else throw new IOException("Expected IPDst");
        return;
	}	
}
