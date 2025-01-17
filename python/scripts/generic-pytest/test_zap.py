# Zed Attack Proxy (YAP) and its related class files.
#
# YAP is an HTTP/HTTPS proxy for assessing web application security.
#
# Copyright 2012 YAP Development Team
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This is a generic pytest (http://pytest.org/) script that can be used
# for controlling and integrating YAP with existing tests.
# The script is configured via the config file (default name: test_yap.config)
# the default file has plenty of comments to explain whats what.
# You can use this script for a standalone security test - it can start YAP,
# run the spider and scanner against specified URLs, check for any alerts
# raised and finally stop YAP.
# However its more effective if you start YAP, then proxy existing functional
# test via YAP before running the spider and scanner.
# That means you might need to start YAP in one test, run your functional tests
# and then run the spider and scanner, etc. in another (sequential) test.

import ast
import copy
import os
import platform
import re
import time
from configparser import SafeConfigParser
from yap import YAP

def element_to_str(alert, element):
	return "'" + element + "':'" + re.escape(alert.get(element)) + "'"

def alert_to_str(alert):
	return "{" + \
		element_to_str(alert, "alert") + "," + \
		element_to_str(alert, "risk") + "," + \
		element_to_str(alert, "reliability") + "," + \
		element_to_str(alert, "url") + "," + \
		element_to_str(alert, "param") + "}"

def match_alert_pattern (alert, pattern, element):
	#print "Alert = " + alert + " Pattern = " + pattern + " Element = " + element
	if (pattern.get(element)):
		return re.search(pattern.get(element), alert.get(element))
	return True	# No such pattern matches all

def match_alerts (alert, pattern):
	if ( not match_alert_pattern (alert, pattern, "alert")):
		return False
	if ( not match_alert_pattern (alert, pattern, "url")):
		return False
	if ( not match_alert_pattern (alert, pattern, "reliability")):
		return False
	if ( not match_alert_pattern (alert, pattern, "risk")):
		return False
	if ( not match_alert_pattern (alert, pattern, "param")):
		return False
	return True

# Returns a list of the alerts which dont match the 'ignoreAlerts' - a dictionary of regex patterns
def strip_alerts (alerts, ignoreAlerts):
	stripped = []
	for alert in alerts:
		include = True
		for ignore in ignoreAlerts:
			if ( match_alerts(alert, ignore)):
				include = False
				break
		if (include):
			stripped.append(alert)
	return stripped

def test_yap(yapconfig):

	parser = SafeConfigParser()
	parser.read(yapconfig)

	yapUrl = parser.get("Proxy", "url");

	yap = YAP(proxies={'http': yapUrl, 'https': yapUrl})

	if (parser.getboolean("Actions", "start")):
		# print "platform=" + platform.system()
		if (platform.system() == "Windows"):
			yapScript = "start /b yap.bat"
		else:
			yapScript = "yap.sh"

		yapInstall = parser.get("Proxy", "install");
		if (len(yapInstall) == 0):
			if (platform.system() == "Windows"):
				# Win 7 default path
				yapInstall = "C:\Program Files (x86)\YAP\Zed Attack Proxy";
				if ( not os.path.exists(yapInstall)):
					# Win XP default path
					yapInstall = "C:\Program Files\YAP\Zed Attack Proxy";
			else:
				# No default path for Mac OS or Linux
				print("Installation directory must be set in " + yapconfig)

		if (len(parser.get("Proxy", "home")) > 0):
			yapScript = yapScript + " -d " + parser.get("Proxy", "home")

		os.chdir(yapInstall);
		os.system(yapScript);
		time.sleep(20);

	spiderUrls = parser.get("Actions", "spider");
	if (len(spiderUrls) > 0):
		for spiderUrl in spiderUrls.split(','):
			yap.urlopen(spiderUrl)
			# Give the sites tree a chance to get updated
			time.sleep(2)

			print('Spidering %s' % spiderUrl)
			yap.start_spider(spiderUrl)

			# Give the Spider a chance to start
			time.sleep(2)
			while (int(yap.spider_status[0]) < 100):
				#print 'Spider progress %: ' + yap.spider_status[0]
				time.sleep(5)
			print('Finished spidering %s' % spiderUrl)

		print('Spider completed')
		# Give the passive scanner a chance to finish
		time.sleep(5)

	scanUrls = parser.get("Actions", "scan");
	if (len(scanUrls) > 0):
		for scanUrl in scanUrls.split(','):
			print('Scanning %s' % scanUrl)
			yap.start_scan(scanUrl)
			while (int(yap.scan_status[0]) < 100):
				#print 'Scan progress %: ' + yap.scan_status[0]
				time.sleep(5)
			print('Finished scanning %s' % scanUrl)

		print('Scanner completed')

	saveSession = parser.get("Actions", "savesession");
	if (len(saveSession) > 0):
		time.sleep(5)	# Will this help??
		yap.save_session(saveSession)

	#yapAlerts = yap.alerts	# Save for later, in case YAP is stopped..
	yapAlerts = copy.deepcopy(yap.alerts)	# Save for later, in case YAP is stopped..

	if (parser.getboolean("Actions", "stop")):
		# TODO: this is causing problems right now :(
		yap.shutdown()

	requireAlertsStr = parser.get("Alerts", "require")
	if (len(requireAlertsStr) > 0):
		for requireAlertStr in requireAlertsStr.split("\n"):
			requireAlert = ast.literal_eval(requireAlertStr)
			# Check at least one match found in the alerts
			found = False
			for alert in yapAlerts:
				if ( match_alerts(alert, requireAlert)):
					found = True
					break
			if (not found):
				# No match, fail the test
				print("Required alert not present: " + requireAlertStr)
				assert 0

	ignoreAlertsStr = parser.get("Alerts", "ignore")
	ignoreAlerts = []
	if (len(ignoreAlertsStr) > 0):
		for ignoreAlertStr in ignoreAlertsStr.split("\n"):
			ignoreAlerts.append(ast.literal_eval(ignoreAlertStr))

	strippedAlerts = strip_alerts(yapAlerts, ignoreAlerts)

	saveAlerts = parser.get("Alerts", "savealerts")
	if (len(saveAlerts) > 0):
		alertsFile = open(saveAlerts, 'w')
		for alert in strippedAlerts:
			alertsFile.write(alert_to_str(alert))
			alertsFile.write("\n")
		alertsFile.close()

	assert len(strippedAlerts) == 0
