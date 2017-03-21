package com.clarity.binary.diagram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.clarity.binary.diagram.DiagramConstants.BinaryClassAssociation;
import com.clarity.binary.extractor.BinaryClassRelationship;
import com.clarity.sourcemodel.Component;

import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Size;

/**
 * Represents a group of components that are related to each other.
 *
 */
public class RelatedComponentsGroup {

    private static final int MAX_MATCHES_PER_COMPONENT = 3;

    @NotNull
    private Map<String, Component> allComponents;

    @NotNull
    private Map<String, BinaryClassRelationship> allRelationships;

    @NotNull
    @NotEmpty
    private List<String> mainComponents;

    @Size(min = 1)
    private int desiredResultSetSize;

    /**
     *
     * @param allComponents
     *            All the components to be considered.
     * @param allRelationships
     *            All the binary relationships between the components.
     * @param mainComponent
     *            The component all other components in the group must have a
     *            relation with.
     * @param desiredResultSetSize
     *            Desired result set size of the related component group.
     */
    public RelatedComponentsGroup(final Map<String, Component> allComponents,
            final Map<String, BinaryClassRelationship> allRelationships, final Component mainComponent,
            final int desiredResultSetSize) {
        this.allComponents = allComponents;
        this.allRelationships = allRelationships;
        this.mainComponents = new ArrayList<String>();
        this.mainComponents.add(mainComponent.uniqueName());
        this.desiredResultSetSize = desiredResultSetSize;
    }

    /**
     *
     * @param allComponents
     *            All the components to be considered.
     * @param allRelationships
     *            All the binary relationships between the components to be
     *            considered.
     * @param mainComponents
     *            A list of components that must be are the basis of and must be
     *            included in the result set.
     */
    public RelatedComponentsGroup(final Map<String, Component> allComponents,
            final Map<String, BinaryClassRelationship> allRelationships, final List<String> mainComponents) {
        this.allComponents = allComponents;
        this.allRelationships = allRelationships;
        this.mainComponents = mainComponents;
        this.desiredResultSetSize = 5;
    }

    /**
     * Creates a list of components who are closely related to the given
     * components.
     */
    public Set<Component> components() {

        final Set<Component> overallRelatedGroup = new HashSet<Component>();
        for (String cmpName : mainComponents) {
            Component cmp = allComponents.get(cmpName);

            // if one of the main components is not a base component (eg: a
            // method or variable), get its parent base component.
            while (!cmp.componentType().isBaseComponent() && cmp != null) {
                cmp = allComponents.get(cmp.parentUniqueName());
            }

            final List<Component> componentRelatedGroup = new ArrayList<Component>();
            componentRelatedGroup.add(cmp);

            /**
             * Filter stage 1: form a super class hierarchy chain of the key
             * component.
             */
            final List<Component> tmpSuperComponentRelatedGroup = new ArrayList<Component>();
            tmpSuperComponentRelatedGroup.add(cmp);

            for (int j = 0; j < tmpSuperComponentRelatedGroup.size(); j++) {
                int matches = 0;
                for (final Map.Entry<String, BinaryClassRelationship> entry : allRelationships.entrySet()) {
                    /**
                     * for the component represented by position j, only collect
                     * a maximum of MAX_MATCHES_PER_COMPONENT components related
                     * to j (see below). This is because we want to keep diagram
                     * sizes to minimum.
                     */
                    if (matches >= MAX_MATCHES_PER_COMPONENT) {
                        break;
                    }

                    final BinaryClassRelationship bCR = entry.getValue();

                    if (bCR.getClassA().uniqueName().equals(tmpSuperComponentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!tmpSuperComponentRelatedGroup.contains(bCR.getClassB())) {
                            // super class, place at beginning of list.
                            tmpSuperComponentRelatedGroup.add(bCR.getClassB());
                            matches++;
                        }
                    }

                    if (bCR.getClassB().uniqueName().equals(tmpSuperComponentRelatedGroup.get(j).uniqueName())
                            && (bCR.getbSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!tmpSuperComponentRelatedGroup.contains(bCR.getClassA())) {
                            // super class, place at beginning of list.
                            tmpSuperComponentRelatedGroup.add(bCR.getClassA());
                            matches++;
                        }
                    }

                }
            }

            /**
             * Filter stage 2: form a sub class hierarchy chain of the key
             * component.
             */
            final List<Component> tmpSubComponentRelatedGroup = new ArrayList<Component>();
            tmpSubComponentRelatedGroup.add(cmp);

            for (int j = 0; j < tmpSubComponentRelatedGroup.size(); j++) {
                int matches = 0;
                for (final Map.Entry<String, BinaryClassRelationship> entry : allRelationships.entrySet()) {
                    if (matches >= MAX_MATCHES_PER_COMPONENT) {
                        break;
                    }

                    final BinaryClassRelationship bCR = entry.getValue();

                    if (bCR.getClassA().uniqueName().equals(tmpSubComponentRelatedGroup.get(j).uniqueName())
                            && (bCR.getbSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!tmpSubComponentRelatedGroup.contains(bCR.getClassB())) {
                            tmpSubComponentRelatedGroup.add(bCR.getClassB());
                            matches++;
                        }
                    }

                    if (bCR.getClassB().uniqueName().equals(tmpSubComponentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!tmpSubComponentRelatedGroup.contains(bCR.getClassA())) {
                            tmpSubComponentRelatedGroup.add(bCR.getClassA());
                            matches++;
                        }
                    }
                }
            }

            /**
             * combine the super and sub component lists so that the super
             * components are at the beginning and the sub components are at the
             * end. Note the first component of the temporary lists are the main
             * component.
             */
            componentRelatedGroup.addAll(0,
                    tmpSuperComponentRelatedGroup.subList(1, tmpSuperComponentRelatedGroup.size()));
            componentRelatedGroup.addAll(tmpSubComponentRelatedGroup.subList(1, tmpSubComponentRelatedGroup.size()));

            /**
             * Filter stage 3: Start with the beginning of the current result
             * list and look for composition/aggregation relationships.
             */
            for (int j = 0; j < componentRelatedGroup.size()
                    && componentRelatedGroup.size() < desiredResultSetSize; j++) {
                int matches = 0;
                for (final Map.Entry<String, BinaryClassRelationship> entry : allRelationships.entrySet()) {
                    if (matches >= MAX_MATCHES_PER_COMPONENT) {
                        break;
                    }
                    final BinaryClassRelationship bCR = entry.getValue();
                    if (bCR.getClassA().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.COMPOSITION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.COMPOSITION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassB())) {
                            componentRelatedGroup.add(bCR.getClassB());
                            matches++;
                        }
                    }
                    if (bCR.getClassB().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.COMPOSITION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.COMPOSITION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassA())) {
                            componentRelatedGroup.add(bCR.getClassA());
                            matches++;
                        }
                    }
                }
            }

            /**
             * Filter stage 4: get any remaining components that are involved in
             * a extension/realization relationship with the list of components
             * collected so far.
             */
            for (int j = 0; j < componentRelatedGroup.size()
                    && componentRelatedGroup.size() < desiredResultSetSize; j++) {
                int matches = 0;
                for (final Map.Entry<String, BinaryClassRelationship> entry : allRelationships.entrySet()) {
                    if (matches >= MAX_MATCHES_PER_COMPONENT) {
                        break;
                    }
                    final BinaryClassRelationship bCR = entry.getValue();
                    if (bCR.getClassA().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.REALIZATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassB())) {
                            componentRelatedGroup.add(bCR.getClassB());
                            matches++;

                        }
                    }
                    if (bCR.getClassB().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getbSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.REALIZATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.GENERALISATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.REALIZATION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassA())) {
                            componentRelatedGroup.add(bCR.getClassA());
                            matches++;
                        }
                    }
                }
            }

            /**
             * Filter stage 5: If there is space, find any remaining weak
             * relationships.
             */
            for (int j = 0; j < componentRelatedGroup.size()
                    && componentRelatedGroup.size() <= desiredResultSetSize; j++) {
                for (final Map.Entry<String, BinaryClassRelationship> entry : allRelationships.entrySet()) {

                    if (componentRelatedGroup.size() > desiredResultSetSize) {
                        break;
                    }

                    final BinaryClassRelationship bCR = entry.getValue();
                    if (bCR.getClassA().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassB())) {
                            componentRelatedGroup.add(bCR.getClassB());
                        }
                    }
                    if (bCR.getClassB().uniqueName().equals(componentRelatedGroup.get(j).uniqueName())
                            && (bCR.getaSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.AGGREGATION
                                    || bCR.getbSideAssociation() == BinaryClassAssociation.WEAK_ASSOCIATION
                                    || bCR.getaSideAssociation() == BinaryClassAssociation.AGGREGATION)) {
                        if (!componentRelatedGroup.contains(bCR.getClassA())) {
                            componentRelatedGroup.add(bCR.getClassA());
                        }
                    }
                }
            }
            // add all the components related to the current component to the
            // overall component group
            overallRelatedGroup.addAll(componentRelatedGroup);
        }
        return overallRelatedGroup;
    }
}
