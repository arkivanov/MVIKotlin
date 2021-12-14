function contentScript() {
  const port = chrome.runtime.connect("johehgbnhfknbbdndfcablclpopcoaee", { name: "web-client" })
  const clientId = Date.now().toString();

  window.addEventListener("message", (event) => {
    if ((event.source != window) || !event.data.receiverId || (event.data.receiverId != clientId)) {
      return;
    }

    port.postMessage(event.data.payload)
  });

  port.onMessage.addListener((message, _) => {
    window.postMessage({ senderId: clientId, receiverId: "server", type: "proto", payload: message }, "*");
  });

  window.postMessage({ senderId: clientId, receiverId: "server", type: "connect" }, "*");

  return 0
}
