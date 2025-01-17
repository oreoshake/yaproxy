/*
 * Zed Attack Proxy (YAP) and its related class files.
 *
 * YAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2013 The YAP Development Team
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
package org.yaproxy.yap.extension.script;

public interface ScriptEventListener {

    void refreshScript(ScriptWrapper script);

    void scriptAdded(ScriptWrapper script, boolean display);

    void scriptRemoved(ScriptWrapper script);

    void preInvoke(ScriptWrapper script);

    void scriptChanged(ScriptWrapper script);

    void scriptError(ScriptWrapper script);

    void scriptSaved(ScriptWrapper script);

    void templateAdded(ScriptWrapper script, boolean display);

    void templateRemoved(ScriptWrapper script);
}
