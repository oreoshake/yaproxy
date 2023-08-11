This file documents the places the `Kb` (Knowledgebase) is used within YAP and yap-extensions. (`org.parosproxy.paros.core.scanner.Kb`)

Use of `Kb` is currently exposed through `AbstractPlugin` and `HostProcess` `getKb()` methods.

## URI Level

- `org.yaproxy.yap.extension.ascanrules.SqlInjectionScanRule` tracks
    - `sql/<rdbms_name>`
