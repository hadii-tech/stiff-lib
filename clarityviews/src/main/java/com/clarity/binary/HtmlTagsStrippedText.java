package com.clarity.binary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;

public class HtmlTagsStrippedText implements Text {

    private Text   text;
    OutputSettings settings = new OutputSettings();

    public HtmlTagsStrippedText(Text text) {
        this.text = text;
        this.settings.prettyPrint(false);
    }

    @Override
    public String value() {
        return Jsoup.parse(this.text.value()).text();
    }
}