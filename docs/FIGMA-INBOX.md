# Figma UI Reference

The retained Web UI is based on the existing design:
https://www.figma.com/design/qHd626nmXLZRuxvIESJx4b

Existing screens include desktop and narrow Overview/Transactions, alert inbox,
empty state, alert testing, duplicate account messaging and the expanded menu.
The profile uses the server session name and uppercase initials (Chengyang Lee / CL).

This architecture refactor did not edit Figma. It reorganized source files and
connected the existing UI to Java REST endpoints. Backend behavior is documented
in API.md. Alert testing now creates server-owned in-memory notifications.

Use Figma for future presentation changes and review them for keyboard access,
responsive layout, visible focus and non-overlapping navigation controls.
