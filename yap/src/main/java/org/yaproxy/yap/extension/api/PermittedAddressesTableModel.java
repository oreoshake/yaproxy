/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2017 The YAP Development Team
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
import org.parosproxy.paros.Constant;
import org.yaproxy.yap.network.DomainMatcher;
import org.yaproxy.yap.view.AbstractMultipleOptionsTableModel;

@SuppressWarnings("serial")
public class PermittedAddressesTableModel extends AbstractMultipleOptionsTableModel<DomainMatcher> {

    private static final long serialVersionUID = -5411351965957264957L;

    private static final String[] COLUMN_NAMES = {
        Constant.messages.getString("api.options.addr.table.header.enabled"),
        Constant.messages.getString("api.options.addr.table.header.regex"),
        Constant.messages.getString("api.options.addr.table.header.value")
    };

    private static final int COLUMN_COUNT = COLUMN_NAMES.length;

    private List<DomainMatcher> addresses = new ArrayList<>(5);

    public PermittedAddressesTableModel() {
        super();
    }

    @Override
    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public int getRowCount() {
        return addresses.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return getElement(rowIndex).isEnabled();
            case 1:
                return getElement(rowIndex).isRegex();
            case 2:
                return getElement(rowIndex).getValue();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && aValue instanceof Boolean) {
            addresses.get(rowIndex).setEnabled((Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (c == 0 || c == 1) {
            return Boolean.class;
        }
        return String.class;
    }

    public List<DomainMatcher> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<DomainMatcher> addrs) {
        this.addresses = new ArrayList<>(addrs.size());

        for (DomainMatcher addr : addrs) {
            this.addresses.add(new DomainMatcher(addr));
        }

        fireTableDataChanged();
    }

    @Override
    public List<DomainMatcher> getElements() {
        return addresses;
    }
}
