/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2021 The YAP Development Team
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
package org.yaproxy.yap.extension.stdmenus;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.model.SiteNode;
import org.yaproxy.yap.model.Context;
import org.yaproxy.yap.view.popup.PopupMenuItemIncludeInContext;

class PopupMenuItemIncludeSiteInContext extends PopupMenuItemIncludeInContext {

    private static final long serialVersionUID = 1L;

    PopupMenuItemIncludeSiteInContext() {
        super();
    }

    PopupMenuItemIncludeSiteInContext(Context context) {
        super(context);
    }

    @Override
    public String getParentMenuName() {
        return Constant.messages.getString("context.includesite.popup");
    }

    @Override
    protected String createRegex(SiteNode sn) throws DatabaseException {
        while (sn.getParent().getParent() != null) {
            sn = sn.getParent();
        }
        return super.createRegex(sn);
    }
}