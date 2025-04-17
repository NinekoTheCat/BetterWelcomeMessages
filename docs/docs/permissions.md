# Permissions

This mod supports permissions via the
[Fabric Permissions API](https://github.com/lucko/fabric-permissions-api/blob/master/README.md).
!!! info 
    One of the most popular mods for managing permissions is [Luck Perms](https://luckperms.net/)

## List of permissions

| Permission                                | Description                                                                                                       | Default Requirements |
|:------------------------------------------|:------------------------------------------------------------------------------------------------------------------|:--------------------:|
| `better-welcome-messages.see`             | allows seeing the welcome message again via `/see`                                                                |     op level `2`     |
| `better-welcome-messages.has_sent`        | lets the permission holder check if a welcome message had been sent to a specific player before via `/has_sent`   |     op level `2`     |
| `better-welcome-messages.config`          | allows modifying the welcome message via the GUI                                                                  |     op level `4`     |
| `better-welcome-messages.receive.message` | allows the player to get sent a welcome message                                                                   |        always        |
| `better-welcome-messages.receive.update`  | allows the player to get sent an update notification whenever a new version of this mod is available for download |     op level `4`     |