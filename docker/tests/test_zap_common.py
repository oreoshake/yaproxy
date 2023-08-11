import unittest
from datetime import datetime
from unittest.mock import Mock, PropertyMock, patch

import yap_common
import yapv2

class TestYapCommon(unittest.TestCase):

    def setUp(self):
        yap_common.context_id = None
        yap_common.context_name = None
        yap_common.context_users = None
        yap_common.scan_user = None

    def tearDown(self):
        yap_common.context_id = None
        yap_common.context_name = None
        yap_common.context_users = None
        yap_common.scan_user = None

    def test_load_config(self):
        pass


    def test_is_in_scope(self):
        pass


    def yap_get_alerts(self):
        pass


    def test_yap_spider(self):
        """Spider is started and waits until finished."""
        scan_id = 1
        yap = Mock()
        yap.spider.scan.return_value = scan_id
        yap.spider.status.side_effect = ["0", "50", "100"]
        target = "http://target.example.com"

        with patch("time.sleep"):
            yap_common.yap_spider(yap, target)

        yap.spider.scan.assert_called_once_with(target, contextname=None)
        yap.spider.status.assert_called_with(scan_id)
        self.assertEqual(3, yap.spider.status.call_count)

    def test_yap_spider_raises_exception_if_not_started(self):
        """Spider raises exception if not started."""
        yap = Mock()
        yap.spider.scan.return_value = "url_not_in_context"
        target = "http://target.example.com"

        with self.assertRaises(yap_common.ScanNotStartedException):
            yap_common.yap_spider(yap, target)

        yap.spider.scan.assert_called_once_with(target, contextname=None)
        yap.spider.status.assert_not_called()

    def test_yap_ajax_spider(self):
        """AJAX Spider is started and waits until finished."""
        yap = Mock()
        yap.ajaxSpider.scan.return_value = "OK"
        status = PropertyMock(side_effect=Mock(side_effect=["running", "running", "stopped"]))
        type(yap.ajaxSpider).status = status
        number_of_results = PropertyMock(return_value=10)
        type(yap.ajaxSpider).number_of_results = number_of_results
        target = "http://target.example.com"
        max_time = None

        with patch("time.sleep"):
            yap_common.yap_ajax_spider(yap, target, max_time)

        yap.ajaxSpider.scan.assert_called_once_with(target, contextname=None)
        status.assert_called_with()
        self.assertEqual(3, status.call_count)
        number_of_results.assert_called_with()
        self.assertEqual(2, number_of_results.call_count)

    def test_yap_ajax_spider_raises_exception_if_not_started(self):
        """AJAX Spider raises exception if not started."""
        yap = Mock()
        yap.ajaxSpider.scan.return_value = "url_not_in_context"
        status = PropertyMock()
        type(yap.ajaxSpider).status = status
        target = "http://target.example.com"
        max_time = None

        with self.assertRaises(yap_common.ScanNotStartedException):
            yap_common.yap_ajax_spider(yap, target, max_time)

        yap.ajaxSpider.scan.assert_called_once_with(target, contextname=None)
        status.assert_not_called()

    def test_yap_ajax_spider_with_max_time(self):
        """AJAX Spider is started with specified maximum time."""
        yap = Mock()
        yap.ajaxSpider.scan.return_value = "OK"
        yap.ajaxSpider.status = PropertyMock(side_effect=Mock(side_effect=["stopped"]))
        target = "http://target.example.com"
        max_time = 10

        with patch("time.sleep"):
            yap_common.yap_ajax_spider(yap, target, max_time)

        yap.ajaxSpider.set_option_max_duration.assert_called_once_with(str(max_time))

    def test_yap_active_scan(self):
        """Active Scan is started and waits until finished."""
        scan_id = 1
        yap = Mock()
        yap.ascan.scan.return_value = scan_id
        yap.ascan.status.side_effect = ["0", "50", "100"]
        target = "http://target.example.com"
        scan_policy_name = "MyScanPolicy.policy"

        with patch("time.sleep"):
            yap_common.yap_active_scan(yap, target, scan_policy_name)

        yap.ascan.scan.assert_called_once_with(target, recurse=True, scanpolicyname=scan_policy_name, contextid=None)
        yap.ascan.status.assert_called_with(scan_id)
        self.assertEqual(3, yap.ascan.status.call_count)

    def test_yap_active_scan_raises_exception_if_not_started(self):
        """Active Scan raises exception if not started."""
        yap = Mock()
        yap.ascan.scan.return_value = "url_not_found"
        target = "http://target.example.com"
        scan_policy_name = "MyScanPolicy.policy"

        with self.assertRaises(yap_common.ScanNotStartedException):
            yap_common.yap_active_scan(yap, target, scan_policy_name)

        yap.ascan.scan.assert_called_once_with(target, recurse=True, scanpolicyname=scan_policy_name, contextid=None)
        yap.ascan.status.assert_not_called()

    def test_yap_wait_for_passive_scan(self):
        """Waits for the passive scan to finish."""
        yap = Mock()
        records_to_scan = PropertyMock(side_effect=Mock(side_effect=["15", "10", "5", "0"]))
        type(yap.pscan).records_to_scan = records_to_scan
        timeout_in_secs = None

        with patch("time.sleep"):
            yap_common.yap_wait_for_passive_scan(yap, timeout_in_secs)

        records_to_scan.assert_called_with()
        self.assertEqual(4, records_to_scan.call_count)

    def test_yap_wait_for_passive_scan_until_timeout(self):
        """Waits for the passive scan to finish until timeout."""
        yap = Mock()
        records_to_scan = PropertyMock(return_value="10")
        type(yap.pscan).records_to_scan = records_to_scan
        timeout_in_secs = 10

        with patch("time.sleep"):
            yap_common.yap_wait_for_passive_scan(yap, timeout_in_secs)

        records_to_scan.assert_called_with()
        self.assertGreater(records_to_scan.call_count, 5)

    def test_yap_import_context(self):
        """Context is imported."""
        context_id = "1"
        yap = Mock()
        yap.context.import_context.return_value = context_id
        context_file = "MyContext.context"
        context_name = "My Context"
        type(yap.context).context_list = PropertyMock(return_value=["Default Context", context_name])

        imported_context_id = yap_common.yap_import_context(yap, context_file)

        yap.context.import_context.assert_called_once_with(context_file)
        self.assertEqual(context_id, imported_context_id)
        self.assertEqual(context_id, yap_common.context_id)
        self.assertEqual(context_name, yap_common.context_name)

    def test_yap_import_context_returns_none_if_not_imported(self):
        """Context not imported returns none."""
        context_id = "does_not_exist"
        yap = Mock()
        yap.context.import_context.return_value = context_id
        context_file = "MyContext.context"

        imported_context_id = yap_common.yap_import_context(yap, context_file)

        yap.context.import_context.assert_called_once_with(context_file)
        self.assertIsNone(imported_context_id)
        self.assertIsNone(yap_common.context_id)
        self.assertIsNone(yap_common.context_name)

    def test_yap_import_context_sets_users(self):
        """Context is imported."""
        context_id = "1"
        yap = Mock()
        yap.context.import_context.return_value = context_id

        context_file = "MyContext.context"
        context_name = "My Context"
        context_users = [{'name': 'user1', 'id': '1'}]
        type(yap.context).context_list = PropertyMock(return_value=["Default Context", context_name])
        yap.users.users_list.return_value = context_users

        imported_context_id = yap_common.yap_import_context(yap, context_file)

        yap.context.import_context.assert_called_once_with(context_file)
        self.assertEqual(context_id, imported_context_id)
        self.assertEqual(context_id, yap_common.context_id)
        self.assertEqual(context_name, yap_common.context_name)
        self.assertEqual(context_users, yap_common.context_users)

    def test_yap_spider_uses_imported_context(self):
        """Spider uses imported context."""
        context_name = "My Context"
        yap_common.context_name = context_name

        yap = Mock()
        scan_id = 1
        yap.spider.scan.return_value = scan_id
        yap.spider.status.side_effect = ["100"]
        target = "http://target.example.com"

        with patch("time.sleep"):
            yap_common.yap_spider(yap, target)

        yap.spider.scan.assert_called_once_with(target, contextname=context_name)

    def test_yap_spider_uses_user(self):
        """Spider uses specified user."""
        context_id = 11
        yap_common.context_id = context_id

        user = "user1"
        user_id = "12"
        yap_common.scan_user = {'name': user, 'id': user_id}

        yap = Mock()
        scan_id = 1
        yap.spider.scan_as_user.return_value = scan_id
        yap.spider.status.side_effect = ["100"]
        target = "http://target.example.com"

        with patch("time.sleep"):
            yap_common.yap_spider(yap, target)

        yap.spider.scan_as_user.assert_called_once_with(context_id, user_id)

    def test_yap_ajax_spider_uses_imported_context(self):
        """AJAX Spider uses imported context."""
        context_name = "My Context"
        yap_common.context_name = context_name

        yap = Mock()
        yap.ajaxSpider.scan.return_value = "OK"
        type(yap.ajaxSpider).status = PropertyMock(side_effect=Mock(side_effect=["stopped"]))
        target = "http://target.example.com"
        max_time = None

        with patch("time.sleep"):
            yap_common.yap_ajax_spider(yap, target, max_time)

        yap.ajaxSpider.scan.assert_called_once_with(target, contextname=context_name)

    def test_yap_ajax_spider_uses_user(self):
        """AJAX Spider uses specified user."""
        context_name = "My Context"
        yap_common.context_name = context_name

        user_name = "user1"
        user_id = "12"
        yap_common.scan_user = {'name': user_name, 'id': user_id}

        yap = Mock()
        yap.ajaxSpider.scan_as_user.return_value = "OK"
        type(yap.ajaxSpider).status = PropertyMock(side_effect=Mock(side_effect=["stopped"]))
        target = "http://target.example.com"
        max_time = None

        with patch("time.sleep"):
            yap_common.yap_ajax_spider(yap, target, max_time)

        yap.ajaxSpider.scan_as_user.assert_called_once_with(context_name, user_name, target)

    def test_yap_active_scan_uses_imported_context(self):
        """Active Scan uses imported context."""
        context_id = "1"
        yap_common.context_id = context_id

        yap = Mock()
        yap.ascan.scan.return_value = 1
        yap.ascan.status.side_effect = ["100"]
        target = "http://target.example.com"
        scan_policy_name = "MyScanPolicy.policy"

        with patch("time.sleep"):
            yap_common.yap_active_scan(yap, target, scan_policy_name)

        yap.ascan.scan.assert_called_once_with(target, recurse=True, scanpolicyname=scan_policy_name,
                                               contextid=context_id)

    def test_yap_active_scan_uses_user(self):
        """Active Scan uses specified user."""
        context_id = "1"
        yap_common.context_id = context_id

        user_id = "12"
        yap_common.scan_user = {'name': 'user1', 'id': user_id}

        yap = Mock()
        yap.ascan.scan_as_user.return_value = 1
        yap.ascan.status.side_effect = ["100"]
        target = "http://target.example.com"
        scan_policy_name = "MyScanPolicy.policy"

        with patch("time.sleep"):
            yap_common.yap_active_scan(yap, target, scan_policy_name)

        yap.ascan.scan_as_user.assert_called_once_with(target, recurse=True, scanpolicyname=scan_policy_name,
                                               contextid=context_id, userid=user_id)

    def test_yap_tune(self):
        """Tune makes expected API calls."""
        yap = Mock()

        yap.pscan.disable_all_tags.return_value = "OK"
        yap.pscan.set_max_alerts_per_rule.return_value = "OK"

        yap_common.yap_tune(yap)

        yap.pscan.disable_all_tags.assert_called_once_with()
        yap.pscan.set_max_alerts_per_rule.assert_called_once_with(10)

    def test_yap_set_scan_user_with_one_user(self):
        """Scan_user is set."""
        context_id = "1"
        yap = Mock()

        user_name = 'user1'
        user = {'name': user_name, 'id': '1'}
        yap_common.context_users = [user]

        yap_common.yap_set_scan_user(yap, user_name)

        self.assertEqual(user, yap_common.scan_user)

    def test_yap_set_scan_user_with_multiple_users(self):
        """Scan_user is set."""
        context_id = "1"
        yap = Mock()

        user_name = 'user2'
        user = {'name': user_name, 'id': '1'}
        yap_common.context_users = [{'name': 'user1', 'id': '1'}, user]

        yap_common.yap_set_scan_user(yap, user_name)

        self.assertEqual(user, yap_common.scan_user)

    def test_yap_set_scan_user_with_no_users(self):
        """Exception is raised."""
        context_id = "1"
        yap = Mock()
        yap_common.context_users = []

        with self.assertRaises(yap_common.UserInputException):
            yap_common.yap_set_scan_user(yap, 'user1')

        self.assertEqual(None, yap_common.scan_user)

    def test_yap_set_scan_user_with_bad_user(self):
        """Exception is raised."""
        context_id = "1"
        yap = Mock()

        user_name = 'user1'
        user = {'name': 'user2', 'id': '1'}
        yap_common.context_users = [user]

        with self.assertRaises(yap_common.UserInputException):
            yap_common.yap_set_scan_user(yap, user_name)

        self.assertEqual(None, yap_common.scan_user)
