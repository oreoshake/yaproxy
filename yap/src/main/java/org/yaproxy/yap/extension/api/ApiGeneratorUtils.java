/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The YAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaproxy.yap.extension.api;

import java.util.ArrayList;
import java.util.List;
import org.parosproxy.paros.core.scanner.ScannerParam;
import org.yaproxy.yap.extension.alert.AlertAPI;
import org.yaproxy.yap.extension.anticsrf.AntiCsrfAPI;
import org.yaproxy.yap.extension.anticsrf.AntiCsrfParam;
import org.yaproxy.yap.extension.ascan.ActiveScanAPI;
import org.yaproxy.yap.extension.authentication.AuthenticationAPI;
import org.yaproxy.yap.extension.authorization.AuthorizationAPI;
import org.yaproxy.yap.extension.autoupdate.AutoUpdateAPI;
import org.yaproxy.yap.extension.autoupdate.OptionsParamCheckForUpdates;
import org.yaproxy.yap.extension.brk.BreakAPI;
import org.yaproxy.yap.extension.forceduser.ForcedUserAPI;
import org.yaproxy.yap.extension.httpsessions.HttpSessionsAPI;
import org.yaproxy.yap.extension.params.ParamsAPI;
import org.yaproxy.yap.extension.pscan.PassiveScanAPI;
import org.yaproxy.yap.extension.ruleconfig.RuleConfigAPI;
import org.yaproxy.yap.extension.script.ScriptAPI;
import org.yaproxy.yap.extension.search.SearchAPI;
import org.yaproxy.yap.extension.sessions.SessionManagementAPI;
import org.yaproxy.yap.extension.stats.StatsAPI;
import org.yaproxy.yap.extension.stats.StatsParam;
import org.yaproxy.yap.extension.users.UsersAPI;

/**
 * Utility class for the API generators
 *
 * @author simon
 */
public class ApiGeneratorUtils {

    /**
     * Return all of the available ApiImplementors. If you implement a new ApiImplementor then you
     * must add it to this class.
     *
     * @return all of the available ApiImplementors.
     */
    public static List<ApiImplementor> getAllImplementors() {
        List<ApiImplementor> imps = new ArrayList<>();

        ApiImplementor api;

        imps.add(new AlertAPI(null));

        api = new AntiCsrfAPI(null);
        api.addApiOptions(new AntiCsrfParam());
        imps.add(api);

        imps.add(new PassiveScanAPI(null));
        imps.add(new SearchAPI(null));

        api = new AutoUpdateAPI(null);
        api.addApiOptions(new OptionsParamCheckForUpdates());
        imps.add(api);

        api = new CoreAPI();
        imps.add(api);

        imps.add(new ParamsAPI(null));

        api = new ActiveScanAPI(null);
        api.addApiOptions(new ScannerParam());
        imps.add(api);

        imps.add(new ContextAPI());

        imps.add(new HttpSessionsAPI(null));

        imps.add(new BreakAPI(null));

        imps.add(new AuthenticationAPI(null));

        imps.add(new AuthorizationAPI());

        imps.add(new RuleConfigAPI(null));

        imps.add(new SessionManagementAPI(null));

        imps.add(new UsersAPI(null));

        imps.add(new ForcedUserAPI(null));

        imps.add(new ScriptAPI(null));

        api = new StatsAPI(null);
        api.addApiOptions(new StatsParam());
        imps.add(api);

        return imps;
    }
}
