import contextlib
import os
import tempfile
import unittest
from datetime import datetime
from contextlib import contextmanager
from unittest.mock import Mock, PropertyMock
from unittest.mock import patch

import yap_common


@contextlib.contextmanager
def custom_hooks_file(content=None):
    hooks = content if content else "def custom_hook():\n    pass"
    with tempfile.NamedTemporaryFile() as file:
        file.write(hooks.encode())
        file.flush()
        yield file


@contextlib.contextmanager
def custom_hooks_file_malformed():
    content = """def custom_hook() # missing :\n    pass"""
    with custom_hooks_file(content) as file:
        yield file


class _MockHooks(object):

    def __init__(self):
        self.called = 0


    def yap_started(self, yap, target):
        self.called += 1
        return yap,


class TestYapHooks(unittest.TestCase):

    def setUp(self):
        yap_common.yap_hooks = None
        yap_common.context_id = None
        yap_common.context_name = None
        yap_common.scan_user = None

    def tearDown(self):
        yap_common.yap_hooks = None
        yap_common.context_id = None
        yap_common.context_name = None
        yap_common.scan_user = None

    def test_trigger_hook_mismatch_exception(self):
        """ If the hook signature doesn't match the hook the exception bubbles up """
        yap_common.yap_hooks = _MockHooks()
        with self.assertRaises(Exception):
          yap_common.trigger_hook('yap_started')
        self.assertEqual(yap_common.yap_hooks.called, 0)


    def test_trigger_hook_verify_calls(self):
        """ Verify the hook gets called if it matches signature """
        yap_common.yap_hooks = _MockHooks()
        args = ['yap', 'http://127.0.0.1']
        yap_common.trigger_hook('yap_started', *args)
        yap_common.trigger_hook('yap_started', *args)
        yap_common.trigger_hook('yap_started', *args)
        yap_common.trigger_hook('yap_started', *args)
        yap_common.trigger_hook('yap_started', *args)
        self.assertEqual(yap_common.yap_hooks.called, 5)


    def test_trigger_hook_maintain_signature(self):
        """ Should return original args if there is a mismatch on the return signature """
        yap_common.yap_hooks = _MockHooks()
        args = ['yap', 'http://127.0.0.1']
        # The defined hook method only returns 1 item
        return_direct = yap_common.yap_hooks.yap_started(*args)
        self.assertTrue(len(return_direct) == 1)
        self.assertNotEqual(len(return_direct), len(args))

        # However, when called in hook, if there is a different
        # return signature, ignore the hook return
        return_args = yap_common.trigger_hook('yap_started', *args)
        self.assertEqual(len(args), len(return_args))
        self.assertEqual(args, list(return_args))

    def test_load_custom_hooks_from_file_not_exists(self):
        """Hooks are not loaded when the file does not exist."""
        yap_common.load_custom_hooks(hooks_file="/some-dir/not-a-hooks-file")
        self.assertIsNone(yap_common.yap_hooks)

    def test_load_custom_hooks_from_file_exists(self):
        """Hooks are loaded when the file exists."""
        with custom_hooks_file() as file:
            yap_common.load_custom_hooks(hooks_file=file.name)
        self.assert_custom_hooks_loaded()

    def assert_custom_hooks_loaded(self):
        self.assertIsNotNone(yap_common.yap_hooks)
        self.assertTrue(callable(getattr(yap_common.yap_hooks, "custom_hook")))

    def test_load_custom_hooks_from_file_with_errors(self):
        """Hooks are not loaded and exception is raised when the file has errors."""
        with custom_hooks_file_malformed() as file, self.assertRaises(SyntaxError):
            yap_common.load_custom_hooks(hooks_file=file.name)
        self.assertIsNone(yap_common.yap_hooks)

    def test_load_custom_hooks_from_env_var_file_not_exists(self):
        """Hooks are not loaded from env var defined file when not exists."""
        os.environ['YAP_HOOKS'] = "/some-dir/not-a-hooks-file"
        yap_common.load_custom_hooks()
        self.assertIsNone(yap_common.yap_hooks)

    def test_load_custom_hooks_from_env_var_file_exists(self):
        """Hooks are loaded from env var defined file when exists."""
        with custom_hooks_file() as file:
            os.environ['YAP_HOOKS'] = file.name
            yap_common.load_custom_hooks()
        self.assert_custom_hooks_loaded()

    def test_load_custom_hooks_from_env_var_file_with_errors(self):
        """Hooks are not loaded and exception is raised when the env var defined file has errors."""
        with custom_hooks_file_malformed() as file, self.assertRaises(SyntaxError):
            os.environ['YAP_HOOKS'] = file.name
            yap_common.load_custom_hooks()
        self.assertIsNone(yap_common.yap_hooks)

    def test_load_config_triggers_hook(self):
        """Hook is triggered when load_config is called."""
        hooks = Mock(load_config=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        config = ["#config"]
        config_dict = "config_dict"
        config_msg = "config_msg"
        out_of_scope_dict = "out_of_scope_dict"

        yap_common.load_config(config, config_dict, config_msg, out_of_scope_dict)

        hooks.load_config.assert_called_once_with(config, config_dict, config_msg, out_of_scope_dict)

    def test_print_rules_triggers_hook(self):
        """Hook is triggered when print_rules is called."""
        hooks = Mock(print_rules=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = "yap"
        alert_dict = {}
        level = "level"
        config_dict = "config_dict"
        config_msg = "config_msg"
        min_level = "min_level"
        inc_rule = "inc_rule"
        inc_extra = "inc_extra"
        detailed_output = "detailed_output"
        in_progress_issues = "in_progress_issues"

        count = 0
        inprog_count = 0

        yap_common.print_rules(yap, alert_dict, level, config_dict, config_msg, min_level, inc_rule, inc_extra,
                               detailed_output, in_progress_issues)

        hooks.print_rules_wrap.assert_called_once_with(count, inprog_count)

    def test_start_yap_triggers_hook(self):
        """Hook is triggered when start_yap is called."""
        hooks = Mock(start_yap=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        port = 8080
        extra_yap_params = ["-config", "key=value"]

        with patch("builtins.open"), patch('subprocess.Popen'):
            yap_common.start_yap(port, extra_yap_params)

        hooks.start_yap.assert_called_once_with(port, extra_yap_params)

    def test_start_docker_yap_triggers_hook(self):
        """Hooks are triggered when start_docker_yap is called."""
        hooks = Mock(start_docker_yap=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        docker_image = "docker_image"
        port = 1234
        extra_yap_params = ["-config", "key=value"]
        mount_dir = "/some/dir"

        cid = "123"

        with patch('subprocess.check_output', new=Mock(return_value=cid.encode())):
            yap_common.start_docker_yap(docker_image, port, extra_yap_params, mount_dir)

        hooks.start_docker_yap.assert_called_once_with(docker_image, port, extra_yap_params, mount_dir)
        hooks.start_docker_yap_wrap.assert_called_once_with(cid)

    def test_yap_access_target_triggers_hook(self):
        """Hook is triggered when yap_access_target is called."""
        hooks = Mock(yap_access_target=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = Mock(urlopen=Mock(return_value=""))
        target = "http://target.example.com"

        yap_common.yap_access_target(yap, target)

        hooks.yap_access_target.assert_called_once_with(yap, target)

    def test_yap_spider_triggers_hooks(self):
        """Hooks are triggered when yap_spider is called."""
        hooks = Mock(yap_spider=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = Mock()
        yap.spider.scan.return_value = "1"
        yap.spider.status.side_effect = ["100"]
        target = "http://target.example.com"

        with patch("time.sleep"):
            yap_common.yap_spider(yap, target)

        hooks.yap_spider.assert_called_once_with(yap, target)
        hooks.yap_spider_wrap.assert_called_once_with(None)

    def test_yap_ajax_spider_triggers_hooks(self):
        """Hooks are triggered when yap_ajax_spider is called."""
        hooks = Mock(yap_ajax_spider=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = Mock()
        yap.ajaxSpider.scan.return_value = "OK"
        target = "http://target.example.com"
        max_time = 10

        with patch("time.sleep"):
            yap_common.yap_ajax_spider(yap, target, max_time)

        hooks.yap_ajax_spider.assert_called_once_with(yap, target, max_time)
        hooks.yap_ajax_spider_wrap.assert_called_once_with(None)

    def test_yap_active_scan_triggers_hooks(self):
        """Hooks are triggered when yap_active_scan is called."""
        hooks = Mock(yap_active_scan=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = Mock(ascan=Mock(scan=Mock(return_value=1), status=Mock(return_value="100")))
        target = "http://target.example.com"
        policy = "ScanPolicy"

        with patch("time.sleep"):
            yap_common.yap_active_scan(yap, target, policy)

        hooks.yap_active_scan.assert_called_once_with(yap, target, policy)
        hooks.yap_active_scan_wrap.assert_called_once_with(None)

    def test_yap_get_alerts_triggers_hooks(self):
        """Hooks are triggered when yap_get_alerts is called."""
        hooks = Mock(yap_get_alerts=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        yap = Mock(core=Mock(alerts=Mock(return_value=[])))
        baseurl = "http://target.example.com"
        ignore_scan_rules = "ignore_scan_rules"
        out_of_scope_dict = "out_of_scope_dict"

        alert_dict = {}

        yap_common.yap_get_alerts(yap, baseurl, ignore_scan_rules, out_of_scope_dict)

        hooks.yap_get_alerts.assert_called_once_with(yap, baseurl, ignore_scan_rules, out_of_scope_dict)
        hooks.yap_get_alerts_wrap.assert_called_once_with(alert_dict)

    def test_yap_import_context_triggers_hooks(self):
        """Hooks are triggered when yap_import_context is called."""
        hooks = Mock(yap_import_context=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        context_id = "123"

        yap = Mock()
        yap.context.import_context.return_value = context_id
        type(yap.context).context_list = PropertyMock(return_value=["Default Context", "My Context"])
        context_file = "/path/to/context"

        yap_common.yap_import_context(yap, context_file)

        hooks.yap_import_context.assert_called_once_with(yap, context_file)
        hooks.yap_import_context_wrap.assert_called_once_with(context_id)

    def test_yap_set_scan_user_triggers_hooks(self):
        """Hooks are triggered when yap_set_scan_user is called."""
        hooks = Mock(yap_set_scan_user=Mock(return_value=[]))
        yap_common.yap_hooks = hooks

        user = "user1"
        yap_common.context_users = [{'name': user, 'id': '1'}]

        yap = Mock()

        yap_common.yap_set_scan_user(yap, user)

        hooks.yap_set_scan_user.assert_called_once_with(yap, user)
        hooks.yap_set_scan_user_wrap.assert_called_once_with(None)

        yap_common.context_users = None
