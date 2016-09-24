package com.clarity.rest.core.component.diagram;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.clarity.invocation.ComponentInvocation;
import com.clarity.rest.core.component.diagram.DiagramConstants.BinaryClassAssociation;
import com.clarity.rest.extractor.BinaryClassRelationship;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import com.clarity.sourcemodel.OOPSourceModelConstants.AccessModifiers;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentType;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 *
 * @author Muntazir Fadhel
 *
 */
public class PlantUMLClassDiagramGenerator implements DiagramGenerator {

    private static final String PLANT_UML_BEGIN_STRING = "@startuml\n";
    private static final String PLANT_UML_END_STRING = "\n@enduml";

    /**
     * Creates a list of components who are directly related to the given
     * component in the code base.
     *
     * @param componentList
     *            list of all the components in the code base
     * @param diagramSize
     *            average requested number of components in each diagram
     * @param component
     *            component to generate diagrams for
     * @param binaryRelationships
     *            list of the class relationships in the code base
     * @return list of component groupings representing individual diagrams
     */
    private static ArrayList<Component> groupComponentsIntoDiagrams(final Map<String, Component> componentList,
            final Map<String, BinaryClassRelationship> binaryRelationships, final Component component,
            final int diagramSize) {

        final ArrayList<Component> diagramGroup = new ArrayList<Component>();
        diagramGroup.add(component);
        int i = 0;

        while (i < diagramGroup.size()) {
            for (final ComponentInvocation extension : diagramGroup.get(i)
                    .componentInvocations(ComponentInvocations.EXTENSION)) {
                if (componentList.containsKey(extension.invokedComponent())) {
                    diagramGroup.add(componentList.get(extension.invokedComponent()));
                }
            }
            for (final ComponentInvocation implementation : diagramGroup.get(i)
                    .componentInvocations(ComponentInvocations.IMPLEMENTATION)) {
                if (componentList.containsKey(implementation.invokedComponent())) {
                    diagramGroup.add(componentList.get(implementation.invokedComponent()));
                }
            }
            i++;
        }
        for (int j = diagramGroup.size() - 1; j > 0; j--) {
            for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {
                final BinaryClassRelationship bCR = entry.getValue();
                if (bCR.getClassA().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.COMPOSITION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.COMPOSITION)) {
                    if (!diagramGroup.contains(bCR.getClassB())) {
                        diagramGroup.add(0, bCR.getClassB());
                    }
                }
                if (bCR.getClassB().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.COMPOSITION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.COMPOSITION)) {
                    if (!diagramGroup.contains(bCR.getClassA())) {
                        diagramGroup.add(0, bCR.getClassA());
                    }
                }
            }
        }
        i = 0;
        while (i < diagramGroup.size() && diagramGroup.size() <= diagramSize) {

            for (final ComponentInvocation superClass : diagramGroup.get(i)
                    .componentInvocations(ComponentInvocations.EXTENSION)) {
                if (!diagramGroup.contains(componentList.get(superClass.invokedComponent()))
                        && componentList.containsKey(superClass.invokedComponent())) {
                    diagramGroup.add(componentList.get(superClass.invokedComponent()));
                }
            }
            for (final ComponentInvocation implementClass : diagramGroup.get(i)
                    .componentInvocations(ComponentInvocations.IMPLEMENTATION)) {
                if (!diagramGroup.contains(componentList.get(implementClass.invokedComponent()))
                        && componentList.containsKey(implementClass.invokedComponent())) {
                    diagramGroup.add(componentList.get(implementClass.invokedComponent()));
                }
            }
            i++;
        }
        for (int j = diagramGroup.size() - 1; j > 0; j--) {
            for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {
                if (diagramGroup.size() >= diagramSize) {
                    break;
                }
                final BinaryClassRelationship bCR = entry.getValue();
                if (bCR.getClassA().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                    if (!diagramGroup.contains(bCR.getClassB())) {
                        diagramGroup.add(0, bCR.getClassB());
                    }
                }
                if (bCR.getClassB().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                    if (!diagramGroup.contains(bCR.getClassA())) {
                        diagramGroup.add(0, bCR.getClassA());
                    }
                }
            }
        }
        for (int j = diagramGroup.size() - 1; j > 0; j--) {
            for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {
                if (diagramGroup.size() >= diagramSize) {
                    break;
                }
                final BinaryClassRelationship bCR = entry.getValue();
                if (bCR.getClassA().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                || bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                    if (!diagramGroup.contains(bCR.getClassB())) {
                        diagramGroup.add(bCR.getClassB());
                    }
                }
                if (bCR.getClassB().uniqueName().equals(diagramGroup.get(j).uniqueName())
                        && (bCR.getaSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION
                                || bCR.getbSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                || bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                    if (!diagramGroup.contains(bCR.getClassA())) {
                        diagramGroup.add(bCR.getClassA());
                    }
                }
            }
        }
        for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {
            final BinaryClassRelationship relationship = entry.getValue();
            final Component cmpA = relationship.getClassA();
            final Component cmpB = relationship.getClassB();
            if (cmpA.uniqueName().equals(component.uniqueName()) && !cmpB.uniqueName().equals(cmpA.uniqueName())
                    && ((relationship.getbSideAssociation() == BinaryClassAssociation.GENERALISATION)
                            || (relationship.getbSideAssociation() == BinaryClassAssociation.REALIZATION)
                            || (relationship.getbSideAssociation() == BinaryClassAssociation.COMPOSITION))) {
                diagramGroup.add(cmpB);
            } else if (cmpB.uniqueName().equals(component.uniqueName()) && !cmpA.uniqueName().equals(cmpB.uniqueName())
                    && ((relationship.getaSideAssociation() == BinaryClassAssociation.GENERALISATION)
                            || (relationship.getaSideAssociation() == BinaryClassAssociation.COMPOSITION)
                            || (relationship.getaSideAssociation() == BinaryClassAssociation.REALIZATION))) {
                if (!diagramGroup.contains(cmpA)) {
                    diagramGroup.add(cmpA);
                }
            }
        }
        for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {
            if (diagramGroup.size() >= diagramSize) {
                break;
            }
            final BinaryClassRelationship relationship = entry.getValue();
            final Component cmpA = relationship.getClassA();
            final Component cmpB = relationship.getClassB();
            if (cmpA.uniqueName().equals(component.uniqueName()) && !cmpB.uniqueName().equals(cmpA.uniqueName())
                    && !diagramGroup.contains(cmpB)) {
                diagramGroup.add(cmpB);
            } else if (cmpB.uniqueName().equals(component.uniqueName()) && !cmpB.uniqueName().equals(cmpA.uniqueName())
                    && !diagramGroup.contains(cmpA)) {
                diagramGroup.add(cmpA);
            }
            if (diagramGroup.size() >= diagramSize) {
                return diagramGroup;
            }
        }
        return diagramGroup;
    }

    /**
     * Generates a plantUML String describing the details of the classes
     * contained in the 'activeComponents' list.
     *
     * @param activeComponents
     *            The components under consideration for the current diagram.
     * @param componentList
     *            list of all the components in the code base
     * @return PlantUML text description of the classes for the current diagram.
     */
    private static String genClassDetails(final ArrayList<Component> activeComponents,
            final Map<String, Component> componentList) {
        final StringBuilder tempStrBuilder = new StringBuilder();
        for (final Component component : activeComponents) {
            // insert package name
            tempStrBuilder.append("package " + component.packageName() + " {\n");
            // determine if we have base component type...
            if (component.componentType().isBaseComponent()) {
                if (component.modifiers().contains(
                        // if class is abstract...
                        OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.ABSTRACT))) {
                    tempStrBuilder.append(
                            OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.ABSTRACT) + " ");
                }
                // add component type name (eg: class, interface, etc...)
                tempStrBuilder
                        .append(OOPSourceModelConstants.getJavaComponentTypes().get(component.componentType()) + " ");
                // add the actual component short name
                tempStrBuilder.append(component.uniqueName());
                // add class generics if exist
                if (component.declarationTypeSnippet() != null) {
                    tempStrBuilder.append(component.declarationTypeSnippet());
                }
                // open the brackets
                tempStrBuilder.append(" {\n");
                // if abstract class or interface, add java doc
                if (component.componentType() == ComponentType.INTERFACE_COMPONENT
                        || component.modifiers().contains("abstract")) {
                    if (component.comment() != null && !component.comment().isEmpty()) {
                        if (component.comment().length() < 500) {
                            tempStrBuilder.append(component.comment() + "\n");
                        } else {
                            tempStrBuilder.append(component.comment().substring(0, 500) + "..." + "\n");
                        }
                    }
                }
                for (final String classChildCmpName : component.children()) {
                    final Component childCmp = componentList.get(classChildCmpName);
                    // only care about children of interface or abstract
                    // classes, or special children
                    // of regular classes
                    if ((component.componentType() == ComponentType.INTERFACE_COMPONENT
                            || component.modifiers().contains("abstract"))
                            || (childCmp.componentType() == ComponentType.METHOD_COMPONENT
                                    && diagramaticallyRelevantMethod(childCmp))) {
                        // start entering the fields and methods...
                        if ((childCmp != null) && !childCmp.componentType().isBaseComponent()) {
                            if (childCmp.modifiers().contains(
                                    OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.PUBLIC))) {
                                tempStrBuilder.append(AccessModifiers.PUBLIC.getUMLClassDigramSymbol() + " ");
                            } else if (childCmp.modifiers().contains(
                                    OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.PRIVATE))) {
                                tempStrBuilder.append(AccessModifiers.PRIVATE.getUMLClassDigramSymbol() + " ");
                            } else if (childCmp.modifiers().contains(OOPSourceModelConstants.getJavaAccessModifierMap()
                                    .get(AccessModifiers.PROTECTED))) {
                                tempStrBuilder.append(AccessModifiers.PROTECTED.getUMLClassDigramSymbol() + " ");
                            } else {
                                tempStrBuilder.append(AccessModifiers.NONE.getUMLClassDigramSymbol() + " ");
                            }
                            // if the field/method is abstract or static, add
                            // the {abstract}/{static} prefix..
                            if (childCmp.modifiers().contains(
                                    OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.ABSTRACT))) {
                                tempStrBuilder.append("{");
                                tempStrBuilder.append(OOPSourceModelConstants.getJavaAccessModifierMap()
                                        .get(AccessModifiers.ABSTRACT));
                                tempStrBuilder.append("} ");
                            }
                            if (childCmp.modifiers().contains(
                                    OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.STATIC))) {
                                tempStrBuilder.append("{");
                                tempStrBuilder.append(
                                        OOPSourceModelConstants.getJavaAccessModifierMap().get(AccessModifiers.STATIC));
                                tempStrBuilder.append("} ");
                            }
                            // Add the component name
                            tempStrBuilder.append(childCmp.name());
                            // if the child component has child components
                            // itself, add those in within brackets
                            // eg) when child component is a method, and its
                            // child components are method parameter
                            // components..)
                            if (!childCmp.children().isEmpty() || childCmp.componentType().isMethodComponent()) {
                                String methodStr = "";
                                tempStrBuilder.append("(");
                                for (final String methodChildCmpName : childCmp.children()) {
                                    final Component methodChildCmp = componentList.get(methodChildCmpName);
                                    if (methodChildCmp == null || methodChildCmp.declarationTypeSnippet() == null) {
                                        continue;
                                    } else if (methodChildCmp
                                            .componentType() == ComponentType.METHOD_PARAMETER_COMPONENT
                                            || methodChildCmp
                                                    .componentType() == ComponentType.CONSTRUCTOR_PARAMETER_COMPONENT) {
                                        methodStr += methodChildCmp.declarationTypeSnippet() + ", ";
                                    }
                                }
                                // removes trailing comma
                                methodStr = methodStr.trim();
                                methodStr = methodStr.replaceAll(",$", "");
                                tempStrBuilder.append(methodStr);
                                // insert closing method bracket
                                tempStrBuilder.append(")");
                            }

                            if (childCmp.componentType() == ComponentType.ENUM_CONSTANT_COMPONENT) {
                                break;
                            } else if (childCmp.declarationTypeSnippet() == null
                                    && (childCmp.componentType() == ComponentType.METHOD_COMPONENT
                                            || childCmp.componentType() == ComponentType.CONSTRUCTOR_COMPONENT)) {
                                // add the return/ field type
                                tempStrBuilder.append(" : ");
                                tempStrBuilder.append("void" + "\n");
                            } else {
                                // add the return/ field type
                                tempStrBuilder.append(" : ");
                                if (!childCmp.value().contains(".")) {
                                    tempStrBuilder.append(childCmp.value() + "\n");
                                } else {
                                    tempStrBuilder.append(
                                            childCmp.value().substring(childCmp.value().lastIndexOf(".") + 1) + "\n");
                                }
                            }
                        }
                    }
                }
                tempStrBuilder.append("}\n");
                // close package declaration
                tempStrBuilder.append("}\n");
            }

        }
        return tempStrBuilder.toString();
    }

    /**
     * Determine if the given method should be included in the diagram or not.
     */
    private static boolean diagramaticallyRelevantMethod(Component methodComponent) {

        // no getters or setters
        if (methodComponent.name().startsWith("get") || methodComponent.name().startsWith("set")) {
            return false;
        }
        // no overridden methods
        for (final ComponentInvocation invocation : methodComponent
                .componentInvocations(ComponentInvocations.ANNOTATION)) {
            if (invocation.invokedComponent().equals("Override")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a PlantUML string representing all the inter-class
     * relationships between the given activeComponents.
     *
     * @param activeComponents
     *            Components from which to search for class relationships.
     * @param binaryRelationships
     *            list of all the binary class relationships in the code base
     * @return PlantUML text description of the relations between the given
     *         class components.
     */
    private static String addClassRelations(final ArrayList<Component> activeComponents,
            final Map<String, BinaryClassRelationship> binaryRelationships) {

        final StringBuilder tempStrBuilder = new StringBuilder();

        for (final Map.Entry<String, BinaryClassRelationship> entry : binaryRelationships.entrySet()) {

            // [0] = first class name, [1] = second class name
            final String[] relationshipClassNames = entry.getKey()
                    .split(BinaryClassRelationship.getClassNameSplitter());
            final BinaryClassRelationship relationship = entry.getValue();

            if ((relationshipClassNames[0] != null)
                    && ((relationshipClassNames[1] != null) && activeComponents.contains(relationship.getClassA())
                            && activeComponents.contains(relationship.getClassB())
                            && relationship.getClassA().componentType().isBaseComponent()
                            && relationship.getClassB().componentType().isBaseComponent() && (relationship != null))) {
                final BinaryClassAssociation classAAssociation = relationship.getaSideAssociation();
                final BinaryClassAssociation classBAssociation = relationship.getbSideAssociation();
                // start building our string for side class A
                // insert class A short name
                tempStrBuilder.append(relationship.getClassA().uniqueName() + " ");
                // insert class B multiplicity
                if (!relationship.getbSideMultiplicity().getValue().isEmpty()) {
                    tempStrBuilder.append("\"" + relationship.getbSideMultiplicity().getValue() + "\" ");
                }
                // insert class B association type
                tempStrBuilder.append(classBAssociation.getBackwardLinkEndingType());
                if (classAAssociation.getStrength() > classBAssociation.getStrength()) {
                    tempStrBuilder.append(classAAssociation.getyumlLinkType());
                } else {
                    tempStrBuilder.append(classBAssociation.getyumlLinkType());
                }
                // insert class A association type
                tempStrBuilder.append(classAAssociation.getForwardLinkEndingType());
                // insert class A multiplicity
                if (!relationship.getaSideMultiplicity().getValue().isEmpty()) {
                    tempStrBuilder.append(" \"" + relationship.getaSideMultiplicity().getValue() + "\" ");
                }
                // insert class B name
                tempStrBuilder.append(" " + relationship.getClassB().uniqueName());
                tempStrBuilder.append("\n");
            }
        }

        return tempStrBuilder.toString();
    }

    /**
     * Generates a single diagram.
     *
     * @param componentGroup
     *            list of component groupings that form each diagram
     * @return String containing SVG diagram
     * @param binaryRelationships
     *            list of all the binary class relationships in the code base\
     * @param componentList
     *            list of all the components in the code base
     * @throws IOException
     *             Exception
     * @throws InterruptedException
     *             Exception
     */
    private byte[] genPlantUMLDiagrams(final ArrayList<Component> componentGroup,
            final Map<String, BinaryClassRelationship> binaryRelationships, final Map<String, Component> componentList)
            throws IOException, InterruptedException {

        final String classDefinitions = genClassDetails(componentGroup, componentList);
        final String classRelations = addClassRelations(componentGroup, binaryRelationships);
        final String plantUMLString = (generatePlantUMLString(classDefinitions, classRelations));
        final byte[] diagram = generateDiagram(plantUMLString);
        return diagram;
    }

    @Override
    public final String generateDiagram(final Component component,
            final Map<String, BinaryClassRelationship> binaryRelationships, final Map<String, Component> componentList,
            final int diagramSize) throws InterruptedException, IOException {

        final long startTime = new Date().getTime();
        final ArrayList<Component> componentGroups = groupComponentsIntoDiagrams(componentList, binaryRelationships,
                component, diagramSize);
        final byte[] diagramString = genPlantUMLDiagrams(componentGroups, binaryRelationships, componentList);
        final String diagram = new String(diagramString);
        System.out
                .println(" Clarity View diagram generated in " + (new Date().getTime() - startTime) + " milliseconds.");
        return diagram;
    }

    /**
     *
     * @param theme
     *            color theme of the diagram
     * @param classDefinitions
     *            PlantUML String containing class definitions
     * @param classRelations
     *            PlantUML String containing class relationships
     * @return PlantUML string representing the finished diagram
     * @throws IOException
     *             Exception
     */
    private static String generatePlantUMLString(final String classDefinitions, final String classRelations)
            throws IOException {

        final String diagramSkin = DiagramConstants.PLANT_UML_DARK_THEME_STRING;

        final String source = PLANT_UML_BEGIN_STRING + diagramSkin + classDefinitions + classRelations
                + PLANT_UML_END_STRING;
        return source;
    }

    public byte[] generateDiagram(String source) {
        final SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            os.close();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return os.toByteArray();
    }
}
