//
// $Id$

package com.samskivert.astviz;

/**
 * An example app on which we demonstrate the AST visualizer.
 */
public class ExampleApp
{
    public static class Tester
    {
        public int compute (int value) {
            return value * 2 + 5;
        }

        @Override
        public String toString () {
            return "Tester";
        }
    }

    public static void main (String[] args)
    {
        Tester tester = new Tester();

        int age = 25;
        Object value = tester.compute(age);
        System.out.println("Value " + value);

        String name = "Phineas P. Gage";
        String text = append(name);
        System.out.println("Text " + text);
    }

    protected static String append (String value)
    {
        return value + " was.";
    }
}
