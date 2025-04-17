By default your `config/better-welcome-messages` should look something like this:

```toml
# ...
welcomeMessageText = [
    "Welcome to <rb><url:'https://modrinth.com/project/betterwelcomemessages'>Better Welcome Messages! v%server:mod_version better-welcome-messages%</url></rb> %player:displayname%",
    "If you are not an administrator then don't worry, otherwise change the <font:uniform>main.toml</font> file in <font:uniform>config/better-welcome-messages</font>",
    "❤.",
    "alternatively install it on your client and change it via the config screen.",
    "If there are any issues, or you have feedback you can submit it over <green><url:'https://github.com/NinekoTheCat/BetterWelcomeMessages/issues'>here</url></green>."
]
strategy = "HASH_CHANGED"
checkForUpdates = true
```

## `welcomeMessageText`

This variable controls the text that will be displayed when the player joins the game.
Each 'element' in the list is a new line in the message that gets sent to the player. and each of these will
be parsed according to
[Placeholder API's Simplified Text Format](https://placeholders.pb4.eu/user/text-format/).

It also supports anything on these lists:

- https://placeholders.pb4.eu/user/default-placeholders/#player
- https://placeholders.pb4.eu/user/default-placeholders/#server

## `strategy`

One of `ONCE` or `HASH_CHANGED`.
`HASH_CHANGED`: This means that every time you change the content of the welcome message the player will receive the
updated message.
`ONCE`: Once the player gets a welcome message regardless of if you update the content or not they won't get it.
You can freely swap between them as it still keeps track of which version the player saw regardless of setting.

## `checkForUpdates`

Whenever or not the mod should check for updates on startup.
