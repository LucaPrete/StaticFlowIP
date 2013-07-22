import httplib
import json
import ast
import math
import os
import sys
import argparse
import time

class StaticFlowIp(object):

	def __init__(self, server):
		self.server = server


	def set(self, data):
		ret = self.rest_call(data, "POST")
		return ret[0] == 200



	def rest_call(self, data, action):
		path = "/wm/staticflowip/IP/json"
		headers = {
			"Content-type": "application/json",
			"Accept": "application/json",
			}
		body = json.dumps(data)
		conn = httplib.HTTPConnection(self.server, 8080)
		conn.request(action, path, body, headers)
		response = conn.getresponse()
		ret = (response.status, response.reason, response.read())
		print ret
		conn.close()
		return ret


if os.path.exists('./circuits.json'):
    circuitDb = open('./circuits.json','r')
    lines = circuitDb.readlines()
    circuitDb.close()
else:
    lines={}		

	
	
parser = argparse.ArgumentParser(description='Circuit Pusher')
parser.add_argument('--src', dest='srcAddress', action='store', default='0.0.0.0', help='source address: if type=ip, A.B.C.D')
parser.add_argument('--dst', dest='dstAddress', action='store', default='0.0.0.0', help='destination address: if type=ip, A.B.C.D')
args = parser.parse_args()
srcIP = args.srcAddress
dstIP = args.dstAddress
circuitDb = open('./circuits.json','a')
datetime = time.asctime()
circuitParams = {'IPSrc':args.srcAddress, 'IPDst':args.dstAddress, 'datetime':datetime}
str = json.dumps(circuitParams)
circuitDb.write(str+"\n")
circuitDb.close()
pusher = StaticFlowIp("127.0.0.1")
row = "{'IPSrc':'%s','IPDst':'%s'}" % (args.srcAddress, args.dstAddress)
pusher.set(ast.literal_eval(row))