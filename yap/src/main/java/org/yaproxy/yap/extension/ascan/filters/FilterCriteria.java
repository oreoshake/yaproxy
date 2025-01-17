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
package org.yaproxy.yap.extension.ascan.filters;

/**
 * Processing of FilterCriteria is first Include then Exclude so in case a request matches Include
 * and Exclude both the criteria for eg:- say request tags are there in include and exclude both
 * then the include is given preference and that request will be included and will not be filtered
 * out.
 *
 * @author KSASAN preetkaran20@gmail.com
 */
public enum FilterCriteria {

    /** Include if any value match */
    INCLUDE,

    /** Exclude if any value match */
    EXCLUDE
}
