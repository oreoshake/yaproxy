#!/usr/bin/env python
# Zed Attack Proxy (YAP) and its related class files.
#
# YAP is an HTTP/HTTPS proxy for assessing web application security.
#
# Copyright 2017 YAP Development Team
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

# This script runs a full scan against a target URL using YAP
#
# It can either be run 'standalone', in which case depends on
# https://pypi.python.org/pypi/yaproxy and Docker, or it can be run
# inside one of the YAP docker containers. It automatically detects if it is
# running in docker so the parameters are the same.
#
# By default it will spider the target URL with no time limit, but you can change
# that via the -m parameter.
# It will then perform an active scan of all of the URLs found by the spider.
# This may take a significant amount of time.
# It will exit with codes of:
#	0:	Success
#	1:	At least 1 FAIL
#	2:	At least one WARN and no FAILs
#	3:	Any other failure
# By default all alerts found by YAP will be treated as WARNings.
# You can use the -c or -u parameters to specify a configuration file to override
# this.
# You can generate a template configuration file using the -g parameter. You will
# then need to change 'WARN' to 'FAIL', 'INFO' or 'IGNORE' for the rules you want
# to be handled differently.
# You can also add your own messages for the rules by appending them after a tab
# at the end of each line.
# By default all of the active scan rules run but you can prevent rules from
# running by supplying a configuration file with the rules set to IGNORE.

import getopt
import json
import logging
import os
import os.path
import sys
import time
from datetime import datetime
from yapv2 import YAPv2
from yap_common import *


config_dict = {}
config_msg = {}
out_of_scope_dict = {}
min_level = 0

# Scan rules that aren't really relevant, e.g. the examples rules in the alpha set
ignore_scan_rules = ['-1', '50003', '60000', '60001', '60100', '60101']

# Scan rules that are being addressed
in_progress_issues = {}

logging.basicConfig(level=logging.INFO, format='%(asctime)s %(message)s')
# Hide "Starting new HTTP connection" messages
logging.getLogger("requests").setLevel(logging.WARNING)


def usage():
    print('Usage: yap-full-scan.py -t <target> [options]')
    print('    -t target         target URL including the protocol, e.g. https://www.example.com')
    print('Options:')
    print('    -h                print this help message')
    print('    -c config_file    config file to use to INFO, IGNORE or FAIL warnings')
    print('    -u config_url     URL of config file to use to INFO, IGNORE or FAIL warnings')
    print('    -g gen_file       generate default config file(all rules set to WARN)')
    print('    -m mins           the number of minutes to spider for (defaults to no limit)')
    print('    -r report_html    file to write the full YAP HTML report')
    print('    -w report_md      file to write the full YAP Wiki(Markdown) report')
    print('    -x report_xml     file to write the full YAP XML report')
    print('    -J report_json    file to write the full YAP JSON document')
    print('    -a                include the alpha active and passive scan rules as well')
    print('    -d                show debug messages')
    print('    -P                specify listen port')
    print('    -D                delay in seconds to wait for passive scanning ')
    print('    -i                default rules not in the config file to INFO')
    print('    -I                do not return failure on warning')
    print('    -j                use the Ajax spider in addition to the traditional one')
    print('    -l level          minimum level to show: PASS, IGNORE, INFO, WARN or FAIL, use with -s to hide example URLs')
    print('    -n context_file   context file which will be loaded prior to scanning the target')
    print('    -p progress_file  progress file which specifies issues that are being addressed')
    print('    -s                short output format - dont show PASSes or example URLs')
    print('    -T                max time in minutes to wait for YAP to start and the passive scan to run')
    print('    -U user           username to use for authenticated scans - must be defined in the given context file')
    print('    -z yap_options    YAP command line options e.g. -z "-config aaa=bbb -config ccc=ddd"')
    print('    --hook            path to python file that define your custom hooks')
    print('')
    print('For more details see https://www.yaproxy.org/docs/docker/full-scan/')


def main(argv):

    global min_level
    global in_progress_issues
    cid = ''
    context_file = ''
    progress_file = ''
    config_file = ''
    config_url = ''
    mins = 0
    generate = ''
    port = 0
    detailed_output = True
    report_html = ''
    report_md = ''
    report_xml = ''
    report_json = ''
    target = ''
    yap_alpha = False
    info_unspecified = False
    ajax = False
    base_dir = ''
    yap_ip = 'localhost'
    yap_options = ''
    delay = 0
    timeout = 0
    ignore_warn = False
    hook_file = ''
    user = ''

    pass_count = 0
    warn_count = 0
    fail_count = 0
    info_count = 0
    ignore_count = 0
    warn_inprog_count = 0
    fail_inprog_count = 0
    exception_raised = False

    try:
        opts, args = getopt.getopt(argv, "t:c:u:g:m:n:r:J:w:x:l:hdaijp:sz:P:D:T:IU:", ["hook="])
    except getopt.GetoptError as exc:
        logging.warning('Invalid option ' + exc.opt + ' : ' + exc.msg)
        usage()
        sys.exit(3)

    for opt, arg in opts:
        if opt == '-h':
            usage()
            sys.exit(0)
        elif opt == '-t':
            target = arg
            logging.debug('Target: ' + target)
        elif opt == '-c':
            config_file = arg
        elif opt == '-u':
            config_url = arg
        elif opt == '-g':
            generate = arg
        elif opt == '-d':
            logging.getLogger().setLevel(logging.DEBUG)
        elif opt == '-m':
            mins = int(arg)
        elif opt == '-P':
            port = int(arg)
        elif opt == '-D':
            delay = int(arg)
        elif opt == '-n':
            context_file = arg
        elif opt == '-p':
            progress_file = arg
        elif opt == '-r':
            report_html = arg
        elif opt == '-J':
            report_json = arg
        elif opt == '-w':
            report_md = arg
        elif opt == '-x':
            report_xml = arg
        elif opt == '-a':
            yap_alpha = True
        elif opt == '-i':
            info_unspecified = True
        elif opt == '-I':
            ignore_warn = True
        elif opt == '-j':
            ajax = True
        elif opt == '-l':
            try:
                min_level = yap_conf_lvls.index(arg)
            except ValueError:
                logging.warning('Level must be one of ' + str(yap_conf_lvls))
                usage()
                sys.exit(3)
        elif opt == '-z':
            yap_options = arg
        elif opt == '-s':
            detailed_output = False
        elif opt == '-T':
            timeout = int(arg)
        elif opt == '-U':
            user = arg
        elif opt == '--hook':
            hook_file = arg

    check_yap_client_version()

    load_custom_hooks(hook_file)
    trigger_hook('cli_opts', opts)

    # Check target supplied and ok
    if len(target) == 0:
        usage()
        sys.exit(3)

    if not (target.startswith('http://') or target.startswith('https://')):
        logging.warning('Target must start with \'http://\' or \'https://\'')
        usage()
        sys.exit(3)

    if "-silent" in yap_options and yap_alpha:
        logging.warning('You cannot use the \'-a\' option with the YAP \'-silent\' option')
        sys.exit(3)

    if running_in_docker():
        base_dir = '/yap/wrk/'
        if config_file or generate or report_html or report_xml or report_json or report_md or progress_file or context_file:
            # Check directory has been mounted
            if not os.path.exists(base_dir):
                logging.warning('A file based option has been specified but the directory \'/yap/wrk\' is not mounted ')
                usage()
                sys.exit(3)

    if user and not context_file:
        logging.warning('A context file must be specified (and include the user) if the user option is selected')
        usage()
        sys.exit(3)

    # Choose a random 'ephemeral' port and check its available if it wasn't specified with -P option
    if port == 0:
        port = get_free_port()

    logging.debug('Using port: ' + str(port))

    if config_file:
        # load config file from filestore
        config_file = os.path.join(base_dir, config_file)
        with open(config_file) as f:
            try:
                load_config(f, config_dict, config_msg, out_of_scope_dict)
            except ValueError as e:
                logging.warning("Failed to load config file " + config_file + " " + str(e))
                sys.exit(3)
    elif config_url:
        # load config file from url
        try:
            config_data = urlopen(config_url).read().decode('UTF-8').splitlines()
            load_config(config_data, config_dict, config_msg, out_of_scope_dict)
        except ValueError as e:
            logging.warning("Failed to read configs from " + config_url + " " + str(e))
            sys.exit(3)
        except:
            logging.warning('Failed to read configs from ' + config_url)
            sys.exit(3)

    if progress_file:
        # load progress file from filestore
        with open(os.path.join(base_dir, progress_file)) as f:
            progress = json.load(f)
            # parse into something more useful...
            # in_prog_issues = map of vulnid -> {object with everything in}
            for issue in progress["issues"]:
                if issue["state"] == "inprogress":
                    in_progress_issues[issue["id"]] = issue

    if running_in_docker():
        try:
            params = ['-config', 'spider.maxDuration=' + str(mins)]

            if "-silent" not in yap_options:
                params.append('-addonupdate')
                # In case we're running in the stable container
                params.extend(['-addoninstall', 'pscanrulesBeta'])
                params.extend(['-addoninstall', 'ascanrulesBeta'])

                if yap_alpha:
                    params.extend(['-addoninstall', 'pscanrulesAlpha'])
                    params.extend(['-addoninstall', 'ascanrulesAlpha'])

            add_yap_options(params, yap_options)

            start_yap(port, params)

        except OSError:
            logging.warning('Failed to start YAP :(')
            sys.exit(3)

    else:
        # Not running in docker, so start one
        mount_dir = ''
        if context_file:
            mount_dir = os.path.dirname(os.path.abspath(context_file))

        params = ['-config', 'spider.maxDuration=' + str(mins)]

        if "-silent" not in yap_options:
            params.append('-addonupdate')
            # In case we're running in the stable container
            params.extend(['-addoninstall', 'pscanrulesBeta'])
            params.extend(['-addoninstall', 'ascanrulesBeta'])

            if (yap_alpha):
                params.extend(['-addoninstall', 'pscanrulesAlpha'])
                params.extend(['-addoninstall', 'ascanrulesAlpha'])

        add_yap_options(params, yap_options)

        try:
            cid = start_docker_yap('ghcr.io/yaproxy/yaproxy:weekly', port, params, mount_dir)
            yap_ip = ipaddress_for_cid(cid)
            logging.debug('Docker YAP IP Addr: ' + yap_ip)
        except OSError:
            logging.warning('Failed to start YAP in docker :(')
            sys.exit(3)

    try:
        yap = YAPv2(proxies={'http': 'http://' + yap_ip + ':' + str(port), 'https': 'http://' + yap_ip + ':' + str(port)})

        wait_for_yap_start(yap, timeout * 60)
        trigger_hook('yap_started', yap, target)

        # Make suitable performance tweaks for running in this environment
        yap_tune(yap)
        trigger_hook('yap_tuned', yap)

        if context_file:
            # handle the context file, cant use base_dir as it might not have been set up
            yap_import_context(yap, os.path.join('/yap/wrk/', context_file))
            if (user):
                yap_set_scan_user(yap, user)

        yap_access_target(yap, target)

        if target.count('/') > 2:
            # The url can include a valid path, but always reset to spider the host
            target = target[0:target.index('/', 8)+1]

        time.sleep(2)

        # Spider target
        yap_spider(yap, target)

        if (ajax):
            yap_ajax_spider(yap, target, mins)

        if (delay):
            start_scan = datetime.now()
            while ((datetime.now() - start_scan).seconds < delay):
                time.sleep(5)
                logging.debug('Delay active scan ' + str(delay -(datetime.now() - start_scan).seconds) + ' seconds')

        if target.count('/') > 2:
            # The url can include a valid path, but always reset to scan the host
            target = target[0:target.index('/', 8)+1]

        # Set up the scan policy
        scan_policy = 'Default Policy'
        if config_dict:
            # They have supplied a config file, use this to define the ascan rules
            yap.ascan.enable_all_scanners(scanpolicyname=scan_policy)
            for scanner, state in config_dict.items():
                if state == 'IGNORE':
                    # Dont bother checking the result - this will fail for pscan rules
                    yap.ascan.set_scanner_alert_threshold(id=scanner, alertthreshold='OFF', scanpolicyname=scan_policy)

        yap_active_scan(yap, target, scan_policy)

        yap_wait_for_passive_scan(yap, timeout * 60)

        # Print out a count of the number of urls
        num_urls = len(yap.core.urls())
        if num_urls == 0:
            logging.warning('No URLs found - is the target URL accessible? Local services may not be accessible from the Docker container')
        else:
            if detailed_output:
                print('Total of ' + str(num_urls) + ' URLs')

            alert_dict = yap_get_alerts(yap, target, ignore_scan_rules, out_of_scope_dict)

            all_ascan_rules = yap.ascan.scanners('Default Policy')
            all_pscan_rules = yap.pscan.scanners
            all_dict = {}
            for rule in all_pscan_rules:
                plugin_id = rule.get('id')
                if plugin_id in ignore_scan_rules:
                    continue
                all_dict[plugin_id] = rule.get('name') + ' - Passive/' + rule.get('quality')
            for rule in all_ascan_rules:
                plugin_id = rule.get('id')
                if plugin_id in ignore_scan_rules:
                    continue
                all_dict[plugin_id] = rule.get('name') + ' - Active/' + rule.get('quality')

            if generate:
                # Create the config file
                with open(os.path.join(base_dir, generate), 'w') as f:
                    f.write('# yap-full-scan rule configuration file\n')
                    f.write('# Change WARN to IGNORE to ignore rule or FAIL to fail if rule matches\n')
                    f.write('# Active scan rules set to IGNORE will not be run which will speed up the scan\n')
                    f.write('# Only the rule identifiers are used - the names are just for info\n')
                    f.write('# You can add your own messages to each rule by appending them after a tab on each line.\n')
                    for key, rule in sorted(all_dict.items()):
                        f.write(key + '\tWARN\t(' + rule + ')\n')

            # print out the passing rules
            pass_dict = {}
            for rule in all_pscan_rules:
                plugin_id = rule.get('id')
                if plugin_id in ignore_scan_rules:
                    continue
                if plugin_id not in alert_dict:
                    pass_dict[plugin_id] = rule.get('name')
            for rule in all_ascan_rules:
                plugin_id = rule.get('id')
                if plugin_id in ignore_scan_rules:
                    continue
                if plugin_id not in alert_dict and not(plugin_id in config_dict and config_dict[plugin_id] == 'IGNORE'):
                    pass_dict[plugin_id] = rule.get('name')

            if min_level == yap_conf_lvls.index("PASS") and detailed_output:
                for key, rule in sorted(pass_dict.items()):
                    print('PASS: ' + rule + ' [' + key + ']')

            pass_count = len(pass_dict)

            if detailed_output:
                # print out the ignored ascan rules(there will be no alerts for these as they were not run)
                for rule in all_ascan_rules:
                    plugin_id = rule.get('id')
                    if plugin_id in ignore_scan_rules:
                        continue
                    if plugin_id in config_dict and config_dict[plugin_id] == 'IGNORE':
                        print('SKIP: ' + rule.get('name') + ' [' + plugin_id + ']')

            # print out the ignored rules
            ignore_count, not_used = print_rules(yap, alert_dict, 'IGNORE', config_dict, config_msg, min_level,
                inc_ignore_rules, True, detailed_output, {})

            # print out the info rules
            info_count, not_used = print_rules(yap, alert_dict, 'INFO', config_dict, config_msg, min_level,
                inc_info_rules, info_unspecified, detailed_output, in_progress_issues)

            # print out the warning rules
            warn_count, warn_inprog_count = print_rules(yap, alert_dict, 'WARN', config_dict, config_msg, min_level,
                inc_warn_rules, not info_unspecified, detailed_output, in_progress_issues)

            # print out the failing rules
            fail_count, fail_inprog_count = print_rules(yap, alert_dict, 'FAIL', config_dict, config_msg, min_level,
                inc_fail_rules, True, detailed_output, in_progress_issues)

            if report_html:
                # Save the report
                write_report(os.path.join(base_dir, report_html), yap.core.htmlreport())

            if report_json:
                # Save the report
                write_report(os.path.join(base_dir, report_json), yap.core.jsonreport())

            if report_md:
                # Save the report
                write_report(os.path.join(base_dir, report_md), yap.core.mdreport())

            if report_xml:
                # Save the report
                write_report(os.path.join(base_dir, report_xml), yap.core.xmlreport())

            print('FAIL-NEW: ' + str(fail_count) + '\tFAIL-INPROG: ' + str(fail_inprog_count) +
                '\tWARN-NEW: ' + str(warn_count) + '\tWARN-INPROG: ' + str(warn_inprog_count) +
                '\tINFO: ' + str(info_count) + '\tIGNORE: ' + str(ignore_count) + '\tPASS: ' + str(pass_count))

        trigger_hook('yap_pre_shutdown', yap)
        # Stop YAP
        yap.core.shutdown()

    except UserInputException as e:
        exception_raised = True
        print("ERROR %s" % e)

    except ScanNotStartedException:
        exception_raised = True
        dump_log_file(cid)

    except IOError as e:
        exception_raised = True
        print("ERROR %s" % e)
        logging.warning('I/O error: ' + str(e))
        dump_log_file(cid)

    except:
        exception_raised = True
        print("ERROR " + str(sys.exc_info()[0]))
        logging.warning('Unexpected error: ' + str(sys.exc_info()[0]))
        dump_log_file(cid)

    if not running_in_docker():
        stop_docker(cid)

    trigger_hook('pre_exit', fail_count, warn_count, pass_count)

    if exception_raised:
        sys.exit(3)
    elif fail_count > 0:
        sys.exit(1)
    elif (not ignore_warn) and warn_count > 0:
        sys.exit(2)
    elif pass_count > 0:
        sys.exit(0)
    else:
        sys.exit(3)


if __name__ == "__main__":
    main(sys.argv[1:])
