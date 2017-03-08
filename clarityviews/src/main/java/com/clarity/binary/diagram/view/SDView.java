package com.clarity.binary.diagram.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clarity.binary.ComponentSet;
import com.clarity.binary.diagram.Diagram;
import com.clarity.binary.diagram.RelatedComponentsGroup;
import com.clarity.binary.diagram.display.DiagramClassDisplayName;
import com.clarity.binary.diagram.display.DiagramMethodDisplayName;
import com.clarity.binary.diagram.plantuml.PUMLDiagram;
import com.clarity.binary.diagram.plantuml.PUMLDiagramDesciption;
import com.clarity.binary.diagram.plantuml.StructureDiffPUMLDiagramDesciption;
import com.clarity.binary.diagram.scheme.DarkDiagramColorScheme;
import com.clarity.binary.diagram.scheme.DiagramColorScheme;
import com.clarity.binary.extractor.BinaryClassRelationship;
import com.clarity.binary.extractor.ClassRelationshipsExtractor;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;

import net.sourceforge.plantuml.svg.ComponentDisplayInfo;
import net.sourceforge.plantuml.svg.SvgGraphics;

/**
 * Generates a Structure Diff demonstrating the differences between the two
 * given code bases.
 */
public class SDView implements ClarityView, Serializable {

    private static final long serialVersionUID = -3125810981280395679L;
    private Diagram diagram;

    public SDView(DiagramColorScheme colorScheme, OOPSourceCodeModel olderModel,
            OOPSourceCodeModel newerModel, boolean callback) throws Exception {

        Map<String, BinaryClassRelationship> oldBinaryRelationships = new ClassRelationshipsExtractor<Object>()
                .generateBinaryClassRelationships(olderModel);
        Map<String, BinaryClassRelationship> newBinaryRelationships = new ClassRelationshipsExtractor<Object>()
                .generateBinaryClassRelationships(newerModel);

        // form a list of all components that exist in the newer code base but
        // not in the older code base.
        List<String> addedComponents = new ArrayList<String>();
        for (final Map.Entry<String, Component> entry : newerModel.getComponents().entrySet()) {
            if (!olderModel.getComponents().containsKey(entry.getKey())) {
                addedComponents.add(entry.getKey());
            }
        }

        // form a list of all components that do not exist in the newer code
        // base but do exist in the older code base.
        List<String> deletedComponents = new ArrayList<String>();
        for (final Map.Entry<String, Component> entry : olderModel.getComponents().entrySet()) {
            if (!newerModel.getComponents().containsKey(entry.getKey())) {
                deletedComponents.add(entry.getKey());
            }
        }

        // form a list of all binary relationships that exist in the newer code
        // base but not in the older code base.
        List<BinaryClassRelationship> addedRelationships = new ArrayList<BinaryClassRelationship>();
        for (final Map.Entry<String, BinaryClassRelationship> entry : newBinaryRelationships.entrySet()) {
            if (!oldBinaryRelationships.containsValue(entry.getValue())) {
                addedRelationships.add(entry.getValue());
            }
        }

        // form a list of all binary relationships that do not exist in the
        // newer code base but do exist in the older code base.
        List<BinaryClassRelationship> deletedRelationships = new ArrayList<BinaryClassRelationship>();
        for (final Map.Entry<String, BinaryClassRelationship> entry : oldBinaryRelationships.entrySet()) {
            if (!(newBinaryRelationships.containsValue(entry.getValue()))) {
                deletedRelationships.add(entry.getValue());
            }
        }

        if (addedComponents.isEmpty() && addedRelationships.isEmpty() && deletedComponents.isEmpty()
                && deletedRelationships.isEmpty()) {
            return;
        }

        // generate a list of components that are needed to draw a class diagram
        // for the added components
        Set<Component> keyAddedComponents = new RelatedComponentsGroup(newerModel.getComponents(),
                newBinaryRelationships, addedComponents).components();

        // generate list of components that are needed to draw a class diagram
        // for the deleted components
        Set<Component> keyDeletedComponents = new RelatedComponentsGroup(olderModel.getComponents(),
                oldBinaryRelationships, deletedComponents).components();

        // generate a list of components needed to draw the entire diff diagram
        Set<Component> diagramComponents = new ComponentSet(keyAddedComponents, keyDeletedComponents).set();

        // generate a list of binary relationships needed to draw the entire
        // diff diagram
        Set<BinaryClassRelationship> allRelationships = new HashSet<BinaryClassRelationship>();
        allRelationships.addAll(newBinaryRelationships.values());
        allRelationships.addAll(oldBinaryRelationships.values());

        // source code model representing the merging of the old and new code
        // base
        OOPSourceCodeModel mergedCodeBase = olderModel;
        mergedCodeBase.merge(newerModel);
        List<ComponentDisplayInfo> displayComponents = new ArrayList<ComponentDisplayInfo>();
        for (final Map.Entry<String, Component> entry : mergedCodeBase.getComponents().entrySet()) {
            if (addedComponents.contains(entry.getValue().uniqueName())) {
                // mark all the newly added components green
                if (entry.getValue().componentType().isBaseComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramClassDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#22df80", entry.getValue().componentType().getValue()));

                } else if (entry.getValue().componentType().isMethodComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramMethodDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#22df80", entry.getValue().componentType().getValue()));
                } else {
                    displayComponents.add(new ComponentDisplayInfo(entry.getValue().name(),
                            entry.getValue().uniqueName(), "#22df80", entry.getValue().componentType().getValue()));
                }
            } else if (deletedComponents.contains(entry.getValue().uniqueName())) {
                // mark all the deleted components red
                if (entry.getValue().componentType().isBaseComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramClassDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#F97D7D", entry.getValue().componentType().getValue()));

                } else if (entry.getValue().componentType().isMethodComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramMethodDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#F97D7D", entry.getValue().componentType().getValue()));
                } else {
                    displayComponents.add(new ComponentDisplayInfo(entry.getValue().name(),
                            entry.getValue().uniqueName(), "#F97D7D", entry.getValue().componentType().getValue()));
                }
            } else {
                // mark all the unchanged components gray
                if (entry.getValue().componentType().isBaseComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramClassDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#C5C8C6", entry.getValue().componentType().getValue()));

                } else if (entry.getValue().componentType().isMethodComponent()) {
                    displayComponents.add(new ComponentDisplayInfo(
                            new DiagramMethodDisplayName(entry.getValue().uniqueName()).value(),
                            entry.getValue().uniqueName(), "#C5C8C6", entry.getValue().componentType().getValue()));
                } else {
                    displayComponents.add(new ComponentDisplayInfo(entry.getValue().name(),
                            entry.getValue().uniqueName(), "#C5C8C6", entry.getValue().componentType().getValue()));
                }

            }
        }
        PUMLDiagramDesciption diffClarityView = new StructureDiffPUMLDiagramDesciption(diagramComponents,
                allRelationships, deletedRelationships, addedRelationships, deletedComponents, addedComponents,
                mergedCodeBase.getComponents());
        SvgGraphics.componentCallBack = callback;
        this.diagram = new PUMLDiagram(diffClarityView, colorScheme, displayComponents);
    }

    public SDView(OOPSourceCodeModel olderModel, OOPSourceCodeModel newerModel, boolean callback)
            throws Exception {

        this(new DarkDiagramColorScheme(), olderModel, newerModel, callback);
    }

    @Override
    public Diagram view() {
        return this.diagram;
    }
}