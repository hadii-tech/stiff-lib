package com.hadii.striff.diagram.plantuml;

import com.hadii.striff.diagram.DiagramComponent;
import com.hadii.striff.diagram.scheme.DiagramColorScheme;
import com.hadii.striff.parse.DiffCodeModel;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Set;

public class PUMLDiagram {

    private final String classDiagramDescription;
    private final int size;
    private String svgText;

    public PUMLDiagram(final DiffCodeModel mergedModel, final DiagramColorScheme colorScheme,
                       final Set<DiagramComponent> diagramComponents) throws IOException, PUMLDrawException {
        this.classDiagramDescription = new PUMLClassDiagramCode(
                mergedModel, colorScheme, diagramComponents).code();
        this.size = diagramComponents.size();
        generateSVGText();
    }

    private void generateSVGText() throws PUMLDrawException, IOException {
        if (classDiagramDescription.isEmpty()) {
            this.svgText = "";
        } else {
            final long startTime = new Date().getTime();
            final String plantUMLString = genPlantUMLString();
            final byte[] diagram = generateDiagram(plantUMLString);
            final String diagramStr = new String(diagram);
            System.out.println(
                    "Striff Diagram SVG text generated in " + (new Date().getTime() - startTime) + " milliseconds.");
            this.svgText = diagramStr;
            if (svgText.contains("Syntax Error") || svgText.contains("An error has")) {
                throw new PUMLDrawException("A PUML syntax error occurred while generating this diagram!");
            }
        }
    }

    public final String svgText() {
        return this.svgText;
    }

    public final int size() {
        return this.size;
    }

    /**
     * Returns a PlantUML compliant String representing the class diagram.
     */
    private String genPlantUMLString() {
        return this.classDiagramDescription;
    }

    /**
     * Invokes PlantUML to draw the class diagram based on the source string
     * representing a PlantUML compliant class diagram code.
     */
    private byte[] generateDiagram(String source) throws IOException, PUMLDrawException {
        final SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        } catch (final Exception e) {
            throw new PUMLDrawException("Error occurred while generating diagram!", e);
        } finally {
            os.close();
        }
        // SvgGraphics.displayComponents.clear();
        return os.toByteArray();
    }
}
