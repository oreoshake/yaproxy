/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2019 The YAP Development Team
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
package org.yaproxy.yap.extension.ascan.filters.impl;

import java.util.List;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.HistoryReference;
import org.yaproxy.yap.extension.ascan.filters.FilterResult;
import org.yaproxy.yap.model.StructuralNode;

/**
 * ScanFilter implementation for filtering based on Tags associated with the Message.
 *
 * @author KSASAN preetkaran20@gmail.com
 * @since 2.9.0
 */
public class TagScanFilter extends AbstractGenericScanFilter<String, String> {

    private static final String FILTER_TYPE = "scan.filter.filterType.Tag";

    @Override
    public FilterResult isFiltered(StructuralNode node) {
        HistoryReference href = node.getHistoryReference();

        List<String> nodeTags = href.getTags();
        return this.isFiltered(nodeTags);
    }

    @Override
    public String getFilterType() {
        return Constant.messages.getString(FILTER_TYPE);
    }
}
