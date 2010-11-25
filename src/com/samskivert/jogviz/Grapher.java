//
// $Id$

package com.samskivert.jogviz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Generates a visualization of an in-memory Java object hierarchy.
 */
public class Grapher
{
    /**
     * Creates a grapher that will visualize the graph rooted at the specified object.
     */
    public static Grapher of (Object root)
    {
        return new Grapher(root);
    }

    /**
     * Instructs the grapher to avoid expanding fields that reference an instance of the specified
     * class. Filtered classes will be annotated with ... in the resulting output.
     */
    public Grapher filter (Class<?> clazz)
    {
        _filters.add(clazz);
        return this;
    }

    /**
     * Tells the grapher about a class that should be considered a 'literal' and should have its
     * string value displayed instead of traversing into its members.
     */
    public Grapher literal (Class<?> clazz)
    {
        _literals.add(clazz);
        return this;
    }

    /**
     * Generates a graph of this grapher's root to the specified file. If the file exists, it will
     * be overwritten.
     */
    public void graph (File output)
    {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output)));
            graph(out);
            out.close();
        } catch (IOException e) {
            System.err.println("Unable to write to file '" + output + "': " + e);
        }
    }

    /**
     * Generates a graph of this grapher's root to the specified output writer.
     */
    public void graph (PrintWriter out)
    {
        try {
            println(out, 0, _root.getClass().getSimpleName());
            _seen.add(new ObjKey(_root));
            dumpObject(_root.getClass(), _root, out, 0);
        } catch (Exception e) {
            System.err.println("Graph generation failed");
            e.printStackTrace(System.err);
        }
    }

    /**
     * Generates a graph of this grapher's root to stdout.
     */
    public void graph ()
    {
        graph(new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out))));
    }

    protected Grapher (Object root)
    {
        _root = root;
    }

    protected void dumpObject (Class clazz, Object obj, PrintWriter out, int indent)
        throws Exception
    {
        Class parent = clazz.getSuperclass();
        if (parent != null) {
            dumpObject(parent, obj, out, indent);
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) ||
                !Modifier.isPublic(field.getModifiers())) { // TODO
                continue;
            }
            Object value = field.get(obj);
            ObjKey key = null;
            boolean filter = (value != null) && _filters.contains(value.getClass());
            boolean loop = (value != null) && _seen.contains(key = new ObjKey(value));
            boolean literal = isLiteral(value);
            String suffix = filter ? "(...)" : (loop ? "(<<)" : "");
            String valout = literal ? String.valueOf(value) : value.getClass().getSimpleName();
            println(out, indent, "- " + field.getName() + " -> " + valout + suffix);
            if (!filter && !loop && !literal && value != null) {
                _seen.add(key);
                dumpObject(value.getClass(), value, out, indent+2);
            }
        }
    }

    protected boolean isLiteral (Object value)
    {
        return (value == null) || value.getClass().isPrimitive() ||
            _literals.contains(value.getClass());
    }

    protected static void println (PrintWriter out, int indent, String line)
    {
        for (int ii = 0; ii < indent; ii++) {
            out.print(" ");
        }
        out.println(line);
    }

    protected static final class ObjKey
    {
        public final Object object;

        public ObjKey (Object object) {
            this.object = object;
        }

        @Override public int hashCode () {
            return object.hashCode();
        }

        @Override public boolean equals (Object other) {
            return (other instanceof ObjKey) && (((ObjKey)other).object == object);
        }
    }

    protected Object _root;
    protected Set<ObjKey> _seen = Sets.newHashSet();
    protected Set<Class<?>> _filters = Sets.newHashSet();

    protected Set<Class<?>> _literals = Sets.<Class<?>>newHashSet(
        Boolean.class, Byte.class, Short.class, Integer.class, Long.class,
        Float.class, Double.class, String.class);
}
