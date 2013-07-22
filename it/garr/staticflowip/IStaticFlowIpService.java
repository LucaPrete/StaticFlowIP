package it.garr.staticflowip;


import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;

/**
 * Implementation of the Floodlight StaticFlowIp service.
 *
 * @author Luca Prete <luca.prete@garr.it>
 * @author Andrea Biancini <andrea.biancini@garr.it>
 * @author Fabio Farina <fabio.farina@garr.it>
 * @author Simone Visconti<simone.visconti.89@gmail.com>
 * 
 */

public interface IStaticFlowIpService extends IFloodlightService {

	public void callToStatic(IDevice source, IDevice dest, String IPSrc, String IPDst);
}