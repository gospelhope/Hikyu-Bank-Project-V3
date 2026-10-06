import { request, sendJSON } from "./client.js";

export const alertsAPI = {
  list() {
    return request("/alerts");
  },

  create(input) {
    return sendJSON("/alerts", "POST", input);
  },

  toggle(id, enabled) {
    return sendJSON(`/alerts/${encodeURIComponent(id)}`, "PATCH", { enabled });
  },
};
