JOGVIZ
------

This is a library for visualizing Java object graphs (i.e. in-memory data
structures). I wrote it when I was trying to figure out what the AST looked
like when working on a javac annotation processing plugin.

There may well be more sophisticated tools for visualizing (and probably
browsing) Java object graphs, but this library allows for quick and dirty
visualization.

To use it, simply add jogviz.jar to your classpath and invoke:

    Grapher.of(obj).graph();

Currently it just prints output to stdout, but some day maybe I'll add fancy
graphviz output or something like that.

You can also tell the `Grapher` to avoid recursing into certain types using the
`literal` method, for example:

    Grapher.of(obj).literal(Name.class).literal(Type.class).graph();

Another useful method is `filter`. Take a look at the `Grapher` source for
enlightening details on its use. It's pretty simple.
