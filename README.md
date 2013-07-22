StaticFlowIP Floodlight module
====================================================

StaticFlowIP is a Floodlight module that enables users to build bidirectional path between two couples of nodes
in the network, specifying their IP address.

The Floodlight controller implements the Openflow protocol, which specifications can be found here:
 [Openflow spec](http://www.openflow.org/documents/openflow-spec-v1.0.0.pdf)

This project depends on Floodlight, which can be found here:
 [Floodlight project on GitHub](https://github.com/floodlight/floodlight).

It has been tested with Mininet, which can be found here:
 [Mininet project on GitHub](https://github.com/mininet/mininet).


License
=======

This sofware is licensed under the Apache License, Version 2.0.

Information can be found here:
 [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).


The StaticFlowIP module
=======================

General description
-------------------

With the StaticFlowIP module a user can create a bidirectional path (the best path calculed by routing and topology
modules) between two hosts of the network, just specifying their IP. The original Floodlight circuitPusher application
saves to an external file switches informations and it calls different times the Floodlight REST API. Our python script
saves the two IP end points and it performs only one call to the REST API.

Current limitations
-------------------
Currently users can only add flows and the name is automatically decided by Floodlight. To delete the flow the user has
to manually modify the file created by the application.


Installation and configuration
==============================

The project has been developed and tested on Floodlight v0.90.

The module consists of the following components:
  * it.garr.staticflowip package
  * it.garr.ccbalancer.web package

Eclipse
-------

Using Eclipse, after the import of the Floodlight project:
  * Copy all it.garr.staticflowip.* packages in the project folder (outside net.floodlightcontroller)
  * Modify the file src/main/resources/META-INF/services/net.floodlightcontroller.core.module.IFloodlightModule
      + add it.garr.staticflowip.StaticFlowIp
  * Modify the file src/main/resources/floodlight.properties
      + add it.garr.staticflowip.StaticFlowIp

Runnable file
-------------

For production environment, a jar version of the module is downloadable from the root directory of this
GitHub repository.

Alternatively, it is possible to create your own ``staticflowip.jar`` with the compiled files from this project, plus
your custom IFloodlightModule file. For mode details follow the Floodlight documentation.

According to Floodlight command sintax, you can integrate the jar file to your Floodlight installation running the
command:
```
java -cp floodlight.jar:staticflowip.jar net.floodlightcontroller.core.Main -cf floodlight.properties
```

The parameters specified have the following meaning:
 * ``floodlight.properties`` is the file specifying the properties for the running instance of Floodlight,
   it is configred to start the StaticFlowIP module provided with this project.


REST APIs
=========

This project offers the following REST APIs:

 * ``http://controller-ip:8080/wm/staticflowip/costs``
     + it supports POST actions allowing users to set via REST API a bidirectional path between two nodes of the
       network.

REST API tutorial
---------------------

Below, an example of the REST API used to set paths can be found:

 * JSON posted to define path through http://controller-ip:8080/wm/staticflowip/costs. The following JSON is useful
   to set a path between 10.0.0.1 and 10.0.0.2.
   ```
   [
       {'IPSrc':'10.0.0.1','IPDst':'10.0.0.2'}
   ]
   ```


Utilities and examples
======================

In the scripts folder, a client side script is provided to perform typical operations and examples, like createing
a flow between two hosts:

 * ``PushIP.py`` is an example python script that creates a tunnel (a flow) between two hosts. Once installed, just
   the editing of the script is needed. The user should modify the source and the destination addresses and then
   run it.

   At the moment, with the REST API, users can only add new flows. Flows can not be deleted by REST API.
   To delete a flow the text file produced in the Floodlight folder must be manually modified.
