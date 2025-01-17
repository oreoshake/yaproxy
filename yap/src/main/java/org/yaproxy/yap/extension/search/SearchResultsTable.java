/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The YAP Development Team
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
package org.yaproxy.yap.extension.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.HistoryReference;
import org.yaproxy.yap.extension.search.SearchResultsTableModel.SearchResultTableEntry;
import org.yaproxy.yap.view.table.HistoryReferencesTable;

public class SearchResultsTable extends HistoryReferencesTable {

    private static final long serialVersionUID = -2227731336800996073L;

    public SearchResultsTable(SearchResultsTableModel resultsModel) {
        super(resultsModel, false);

        setAutoScrollOnNewValues(false);

        getColumnExt(Constant.messages.getString("view.href.table.header.hrefid"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.timestamp.request"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.timestamp.response"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.code")).setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.reason"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.rtt")).setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.size.requestheader"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.size.requestbody"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.size.responseheader"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.size.responsebody"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.highestalert"))
                .setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.note")).setVisible(false);
        getColumnExt(Constant.messages.getString("view.href.table.header.tags")).setVisible(false);
    }

    public SearchResult getSelectedSearchResult() {
        final int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            SearchResultTableEntry entry =
                    (SearchResultTableEntry)
                            getModel().getEntry(convertRowIndexToModel(selectedRow));
            if (entry != null) {
                return entry.getSearchResult();
            }
            return null;
        }
        return null;
    }

    @Override
    public List<HistoryReference> getSelectedHistoryReferences() {
        final int[] selectedRows = getSelectedRows();
        if (selectedRows.length == 0) {
            return Collections.emptyList();
        }

        ArrayList<HistoryReference> uniqueHistoryReferences = new ArrayList<>(selectedRows.length);
        SortedSet<Integer> historyReferenceIdsAdded = new TreeSet<>();
        for (int selectedRow : selectedRows) {
            HistoryReference historyReference = getHistoryReferenceAtViewRow(selectedRow);
            if (historyReference != null) {
                Integer id = historyReference.getHistoryId();
                if (!historyReferenceIdsAdded.contains(id)) {
                    historyReferenceIdsAdded.add(id);
                    uniqueHistoryReferences.add(historyReference);
                }
            }
        }
        uniqueHistoryReferences.trimToSize();
        return uniqueHistoryReferences;
    }
}
