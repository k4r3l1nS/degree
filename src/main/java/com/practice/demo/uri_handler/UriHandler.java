package com.practice.demo.uri_handler;

import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;

public class UriHandler {

    public static List<Pair<String, String>> parse(String uri) {

        List<Pair<String, String>> list = new ArrayList<>();

        int index = 0;
        String currentUri = "";
        StringBuilder key = new StringBuilder();

        list.add(new Pair("home", "/"));

        while (index < uri.length()) {

            char charAtIndex = uri.charAt(index);

            if (charAtIndex == '/' && index > 0) {

                list.add(new Pair(key.toString(), currentUri));
                key = new StringBuilder();
            }

            currentUri += charAtIndex;

            if (charAtIndex != '/') {
                key.append(charAtIndex);
            }

            ++index;
        }

        list.add(new Pair<>(key.toString(), currentUri));

        return list;
    }
}
