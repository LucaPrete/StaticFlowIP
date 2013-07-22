package it.garr.staticflowip;

import it.garr.staticflowip.web.StaticFlowIpWebRoutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.storage.IStorageSourceService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;

import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Floodlight StaticFlowIp service.
 *
 * @author Luca Prete <luca.prete@garr.it>
 * @author Andrea Biancini <andrea.biancini@garr.it>
 * @author Fabio Farina <fabio.farina@garr.it>
 * @author Simone Visconti<simone.visconti.89@gmail.com>
 * 
 */

public class StaticFlowIp implements IFloodlightModule, IStaticFlowIpService{
	
	protected static Logger logger = LoggerFactory.getLogger(StaticFlowIp.class);
	
	protected  String srcSwitch;
	protected String srcPort;
	protected String dstSwitch;
	protected String dstPort;
	protected String[] tokens;
	protected String PRIORITY ="30000";
	protected IRestApiService restApi = null;
	protected IFloodlightProviderService floodlightProvider = null;
	protected ITopologyService topology;
    protected IStorageSourceService storageSource;
    protected IRoutingService routingEngine;

	
	// Modules that listen to our updates
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IStaticFlowIpService.class);
	    return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
	    m.put(IStaticFlowIpService.class, this);
	    return m;
	}
	
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    l.add(ITopologyService.class);
        l.add(IRoutingService.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		this.floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		this.topology = context.getServiceImpl(ITopologyService.class);
        this.storageSource = context.getServiceImpl(IStorageSourceService.class);
        this.routingEngine = context.getServiceImpl(IRoutingService.class);
        this.restApi = context.getServiceImpl(IRestApiService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		if (restApi != null) restApi.addRestletRoutable(new StaticFlowIpWebRoutable());
	}
	
	 public void setStorageSource(IStorageSourceService storageSource) {
	        this.storageSource = storageSource;
	 }

	
	
	//Find a route between src and dst, than create flow
	public void callToStatic(IDevice source, IDevice dest, String IPSrc, String IPDst)
	{
		Map<String, Object> RowValues = new HashMap<>();
        Map<String, Object> RevRowValues = new HashMap<>();
        NodePortTuple field = null;
        List<NodePortTuple> switchPorts = null;
        srcSwitch=((String.valueOf(source).split("[ ]+"))[6].split("[=]"))[1].split("[,]")[0];
        srcPort=((String.valueOf(source).split("[ ]+"))[7].split("[=]"))[1].split("[,]")[0];
        dstSwitch=((String.valueOf(dest).split("[ ]+"))[6].split("[=]"))[1].split("[,]")[0];
        dstPort=((String.valueOf(dest).split("[ ]+"))[7].split("[=]"))[1].split("[,]")[0];
//		topology.staticIpRoute(((String.valueOf(source).split("[ ]+"))[6].split("[=]"))[1].split("[,]")[0],
//								((String.valueOf(source).split("[ ]+"))[7].split("[=]"))[1].split("[,]")[0],
//								((String.valueOf(dest).split("[ ]+"))[6].split("[=]"))[1].split("[,]")[0], 
//								((String.valueOf(dest).split("[ ]+"))[7].split("[=]"))[1].split("[,]")[0],
//								IPSrc, IPDst);
        String name = IPSrc + ":" + IPDst;
        String revname = IPDst+ ":" + IPSrc;
        RowValues.put("nw_src", IPSrc);
        RowValues.put("nw_dst", IPDst);
        RevRowValues.put("nw_dst", IPSrc);
        RevRowValues.put("nw_src", IPDst);
        RowValues.put("active", "true");
        RevRowValues.put("active", "true");
    	RowValues.put("priority", PRIORITY);
    	RevRowValues.put("priority", PRIORITY);
        Route route = routingEngine.getRoute(Long.valueOf(srcSwitch), Short.valueOf(srcPort), Long.valueOf(dstSwitch),Short.valueOf(dstPort));
        try
        {
        	switchPorts = route.getPath();
        }
        catch (Exception e){
        	logger.error("no switch");
        	}
        if (switchPorts == null) return;
        for (int counter = 0; counter<switchPorts.size() ; counter++)
        {
        	field = switchPorts.get(counter);
        	RowValues.put("name", field.getNodeId() + ":" + name + ".f");
        	RevRowValues.put("name", field.getNodeId() + ":" + revname + ".f");
        	RowValues.put("switch_id", HexString.toHexString(field.getNodeId()));
        	RevRowValues.put("switch_id", HexString.toHexString(field.getNodeId()));
        	RowValues.put("in_port", field.getPortId());
        	RevRowValues.put("actions", "output="+field.getPortId());
        	RowValues.put("dl_type", "0x800");
        	RevRowValues.put("dl_type", "0x800");
        	field = switchPorts.get(++counter);
        	RowValues.put("actions", "output="+field.getPortId());
        	RevRowValues.put("in_port", field.getPortId());
        	storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, RowValues);
        	storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, RevRowValues);
        	field = switchPorts.get(--counter);
        	RowValues.put("name", field.getNodeId() + ":" + name + ".farp");
        	RevRowValues.put("name", field.getNodeId() + ":" + revname + ".farp");
        	RowValues.put("dl_type", "0x806");
        	RevRowValues.put("dl_type", "0x806");
        	storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, RowValues);
        	storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, RevRowValues);
        	counter++;
        }
	}
}