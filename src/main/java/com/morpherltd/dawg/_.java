package com.morpherltd.dawg;

/**
 * Used as a replacement of the "out" parameters in C#.
 *
 * From Stackoverflow:
 * http://stackoverflow.com/questions/430479/how-do-i-use-an-equivalent-to-c-reference-parameters-in-java/431152#431152
 */
class _<E> {
    E ref;
    public _( E e ){
        ref = e;
    }
    public E g() { return ref; }
    public void s( E e ){ this.ref = e; }

    public String toString() {
        return ref.toString();
    }
}
