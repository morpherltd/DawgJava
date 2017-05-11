package ru.morpher.adjectivizer;

class StringExtensions
{
    static String removeLast(String s, int count)
    {
        return s.substring(0, s.length() - count);
    }

    static String last(String s, int count)
    {
        return s.substring(s.length() - count);
    }

    static Boolean endsWithAny(String s, String[] endings) {
        for (String end : endings) {
            if (s.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    static String reverse(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * Returns a new {@code String} composed of copies of the
     * {@code CharSequence elements} joined together with a copy of the
     * specified {@code delimiter}.
     *
     * <blockquote>For example,
     * <pre>{@code
     *     List<String> strings = new LinkedList<>();
     *     strings.add("Java");
     *     strings.add("is");
     *     strings.add("cool");
     *     String message = join(" ", strings);
     *     //message returned is: "Java is cool"
     *
     *     Set<String> strings = new LinkedHashSet<>();
     *     strings.add("Java");
     *     strings.add("is");
     *     strings.add("very");
     *     strings.add("cool");
     *     String message = join("-", strings);
     *     //message returned is: "Java-is-very-cool"
     * }</pre></blockquote>
     *
     * Note that if an individual element is {@code null}, then {@code "null"} is added.
     *
     * @param  delimiter a sequence of characters that is used to separate each
     *         of the {@code elements} in the resulting {@code String}
     * @param  elements an {@code Iterable} that will have its {@code elements}
     *         joined together.
     *
     * @return a new {@code String} that is composed from the {@code elements}
     *         argument
     *
     * @throws NullPointerException If {@code delimiter} or {@code elements}
     *         is {@code null}
     */
    static String join(CharSequence delimiter,
                       Iterable<? extends CharSequence> elements) {
        if(delimiter == null || elements == null){
            throw new NullPointerException();
        }

        StringBuilder sb = new StringBuilder();
        for (CharSequence element : elements) {
            if (sb.length() > 0) {
                sb.append(delimiter);
            }

            sb.append(element);
        }

        return sb.toString();
    }
}

