/*
 * Created on Jun 14, 2004
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
// YAP: 2019/06/01 Normalise line endings.
// YAP: 2019/06/05 Normalise format/style.
package org.parosproxy.paros.network;

import java.io.IOException;

public class HttpMalformedHeaderException extends IOException {

    private static final long serialVersionUID = 2328568976708794693L;

    public HttpMalformedHeaderException() {
        super();
    }

    public HttpMalformedHeaderException(String msg) {
        super(msg);
    }
}
