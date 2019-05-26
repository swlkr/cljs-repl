# cljs-repl package

The world's worst clojurescript prepl client.

![screenshot](https://i.imgur.com/r16GVHH.gif)

Not really for use, more for learning.

But if you're really curious, start a repl like this:

```bash
clj -J-Dclojure.server.node="{:port 5555 :accept cljs.server.node/prepl}" -m cljs.main --repl-env node
```

and connect to it from atom like this: Shift+Cmd+P -> cljs-repl:connection -> enter.
