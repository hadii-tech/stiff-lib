package com.clarity.binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SimplifiedJavaDocText implements Text {

    private Text text;
    private String[] ignoreAnnotations = new String[] {"@version", "@since", "@return", "@throws", "@code",
            "@abstract", "@access", "@alias", "@author", "@async", "@augments", "@borrows", "@callback", "@class",
            "@param" };

    public SimplifiedJavaDocText(Text text) {
        this.text = text;
    }

    @Override
    public String value() {
        String textValue = this.text.value();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<String> docLines = new ArrayList(Arrays.asList(textValue.split("\\r?\\n")));
        for (int i = 0; i < docLines.size(); i++) {
            if (StringUtils.indexOfAny(docLines.get(i), ignoreAnnotations) != -1) {
                docLines.remove(i);
                while (i < docLines.size() && !docLines.get(i).replaceAll("[/\\*-\\.,]", "").trim().startsWith("@")
                        && !docLines.get(i).trim().isEmpty()) {
                    docLines.remove(i);
                }
                i--;
                continue;
            } else if (docLines.get(i).contains("{@link ")) {
                docLines.set(i, docLines.get(i).replaceAll("\\{\\@link ", ""));
                while (i < docLines.size() && !docLines.get(i).contains("}")) {
                    i++;
                }
                docLines.set(i, docLines.get(i).replaceAll("}", ""));
            } else if (docLines.get(i).contains("{@linkplain")) {
                docLines.set(i, docLines.get(i).replaceAll("\\{\\@linkplain ", ""));
                while (i < docLines.size() && !docLines.get(i).contains("}")) {
                    i++;
                }
                docLines.set(i, docLines.get(i).replaceAll("}", ""));

            } else if (docLines.get(i).contains("@see")) {
                docLines.set(i, docLines.get(i).replaceAll("@see ", "See "));
            }
        }
        return String.join("\n", docLines);
    }
}