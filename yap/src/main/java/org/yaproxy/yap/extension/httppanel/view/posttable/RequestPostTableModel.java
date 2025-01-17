/*
 *
 * Paros and its related class files.
 *
 * Paros is an HTTP/HTTPS proxy for assessing web application security.
 * Copyright (C) 2003-2004 Chinotec Technologies Company
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Clarified Artistic License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Clarified Artistic License for more details.
 *
 * You should have received a copy of the Clarified Artistic License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
// YAP: 2012/04/25 Added @Override annotation to all appropriate methods.
// YAP: 2014/05/16 Issue 81: YAP changes request data (while switching views)
// YAP: 2017/05/31 Added multi-catch in a specific handler.
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
// YAP: 2023/01/10 Tidy up logger.
package org.yaproxy.yap.extension.httppanel.view.posttable;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.AbstractTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parosproxy.paros.Constant;

public class RequestPostTableModel extends AbstractTableModel {

    private static final long serialVersionUID = -3239987476977015394L;
    private static final String[] columnNames = {
        Constant.messages.getString("http.panel.view.table.paramName"),
        Constant.messages.getString("http.panel.view.table.paramValue")
    }; // YAP: i18n
    private static final Pattern pSeparator = Pattern.compile("([^=&]+)[=]([^=&]*)");
    private Vector<String[]> listPair = new Vector<>();
    private boolean editable = true;
    private boolean isChanged = false;

    // YAP: Added logger
    private static final Logger LOGGER = LogManager.getLogger(RequestPostTableModel.class);

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public RequestPostTableModel() {
        super();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return listPair.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        String[] cell = listPair.get(row);
        return cell[col];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public synchronized void setText(String body) {
        isChanged = false;
        listPair.clear();
        String name = null;
        String value = null;
        Matcher matcher = pSeparator.matcher(body);
        // int row = 0;
        int cnt = 0;

        while (matcher.find()) {
            cnt++;
            String[] cell = new String[2];
            try {
                name = URLDecoder.decode(matcher.group(1), "8859_1");
                value = URLDecoder.decode(matcher.group(2), "8859_1");
                cell[0] = name;
                cell[1] = value;
                listPair.add(cell);
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                // YAP: Log the exception
                LOGGER.error(e.getMessage(), e);
            }
        }

        if (cnt == 0) {
            if (body.isEmpty()) {
                if (editable) {
                    // Make sure user can always add a param at the end
                    listPair.add(new String[] {"", ""});
                }
            }
        } else {
            if (editable) {
                // Make sure user can always add a param at the end
                listPair.add(new String[] {"", ""});
            }
        }

        fireTableDataChanged();
    }

    public synchronized String getText() {
        StringBuilder sb = new StringBuilder();
        boolean hasValues = false;

        for (String[] cell : listPair) {
            try {
                String name = URLEncoder.encode(cell[0], "UTF8");
                String value = URLEncoder.encode(cell[1], "UTF8");
                // YAP: Ignore if name is not set
                if (name.length() > 0) {
                    if (hasValues) {
                        sb.append('&');
                    }
                    sb.append(name);
                    sb.append('=');
                    sb.append(value);

                    hasValues = true;
                }
            } catch (UnsupportedEncodingException | IllegalArgumentException e) {
                // YAP: Log the exception
                LOGGER.error(e.getMessage(), e);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return isEditable();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        if (row < listPair.size() - 1) {
            String[] cells = listPair.get(row);
            cells[col] = (String) value;

            fireTableCellUpdated(row, col);
            isChanged = true;
        } else {
            if (!value.equals("")) {
                String[] cells = listPair.get(row);
                cells[col] = (String) value;

                String[] nCells = {"", ""};
                listPair.add(nCells);

                fireTableCellUpdated(row, col);
                isChanged = true;
            }
        }
    }

    public boolean hasChanged() {
        return isChanged;
    }
}
