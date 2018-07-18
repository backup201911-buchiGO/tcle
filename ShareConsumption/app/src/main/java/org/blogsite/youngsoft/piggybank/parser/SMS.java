/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blogsite.youngsoft.piggybank.parser;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author klee
 */
public class SMS {

    private final String contents;
    private ArrayList<String> body = new ArrayList<>();

    private String delimeter = " \n";

    public SMS(String contents) {
        this.contents = contents;
        body = new ArrayList<>();
        tokenizer();
    }

    public SMS(String contents, String delimeter) {
        this.contents = contents;
        this.delimeter = delimeter;
        body = new ArrayList<>();
        tokenizer();
    }

    private void tokenizer() {
        StringTokenizer st = new StringTokenizer(contents, delimeter);
        String t = "";
        while (st.hasMoreTokens()) {
            body.add(st.nextToken());
        }
    }

    public String getContents() {
        return contents;
    }

    public ArrayList<String> getBody() {
        return body;
    }

}
