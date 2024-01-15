function contentScript() {
  const prefix = "MVIKotlinTimeTravel"
  const port = chrome.runtime.connect("pbgejnlmihnnnckigjbcphlfafkipljo", { name: "web-client" })
  const clientId = Date.now().toString();

  window.addEventListener("message", (event) => {
    if ((event.source !== window) || !event.data.startsWith(prefix)) {
      return;
    }

    let parts = event.data.split(":")
    let receiverId = parts[2]

    if (receiverId !== clientId) {
      return
    }

    let payload = parts[4]
    port.postMessage(payload)
  });

  port.onMessage.addListener((payload, _) => {
    window.postMessage(`${prefix}:${clientId}:server:proto:${payload}`, "*")
  });

  window.postMessage(`${prefix}:${clientId}:server:connect`, "*")

  return 0
}
