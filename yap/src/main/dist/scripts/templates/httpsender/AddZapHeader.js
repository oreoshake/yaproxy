// The sendingRequest and responseReceived functions will be called for all requests/responses sent/received by YAP,
// including automated tools (e.g. active scanner, fuzzer, ...)

// Note that new HttpSender scripts will initially be disabled
// Right click the script in the Scripts tree and select "enable"

// For the latest list of 'initiator' values see the HttpSender class:
// https://github.com/yaproxy/yaproxy/blob/main/yap/src/main/java/org/parosproxy/paros/network/HttpSender.java
// 'helper' just has one method at the moment: helper.getHttpSender() which returns the HttpSender
// instance used to send the request.

// In order to facilitate identifying YAP traffic and Web Application Firewall exceptions, YAP is accompanied
// by this script which can be used to add a specific header to all traffic that passes through
// or originates from YAP. e.g.: X-YAP-Initiator: 3

function sendingRequest(msg, initiator, helper) {
	// Add a YAP identifier header to all traffic that originates with or passes through YAP
	msg.getRequestHeader().setHeader("X-YAP-Initiator", initiator);
}

function responseReceived(msg, initiator, helper) {
	// Nothing to do here
}
