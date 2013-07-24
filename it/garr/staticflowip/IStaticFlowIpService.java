/**
 * Copyright (C) 2013 Luca Prete, Simone Visconti, Andrea Biancini, Fabio Farina - www.garr.it - Consortium GARR
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Implementation of the Floodlight StaticFlowIp service
 * 
 * @author Luca Prete <luca.prete@garr.it>
 * @author Andrea Biancini <andrea.biancini@garr.it>
 * @author Fabio Farina <fabio.farina@garr.it>
 * @author Simone Visconti<simone.visconti.89@gmail.com>
 * 
 * @version 0.90
 * @see it.garr.staticflowip.IStaticFlowIp
 * @see it.garr.staticflowip.web.StaticFlowIpResource
 * @see it.garr.staticflowip.web.StaticFlowIpWebRoutable
 * 
 */

package it.garr.staticflowip;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDevice;

public interface IStaticFlowIpService extends IFloodlightService {

	public void callToStatic(IDevice source, IDevice dest, String IPSrc, String IPDst);
}