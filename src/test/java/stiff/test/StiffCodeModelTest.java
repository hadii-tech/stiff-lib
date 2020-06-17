package stiff.test;

import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import com.hadii.stiff.StiffCodeModel;
import com.hadii.stiff.diagram.DiagramComponent;
import com.hadii.stiff.diagram.DiagramCodeModel;
import com.hadii.stiff.parse.DiffCodeModel;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests to ensure component relations are being extracted correctly.
 */
public class StiffCodeModelTest {

    @Test
    public void testSelectedComponentsIncludeAddedComponents() {
        DiagramCodeModel oldModel = new DiagramCodeModel();
        Component newStrawBerryComponent = new Component();
        newStrawBerryComponent.setComponentName("Strawberry");
        newStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        DiagramCodeModel newModel = new DiagramCodeModel(
                new DiagramComponent(newStrawBerryComponent, null)
        );
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel))
                .coreComponents()
                .contains(new DiagramComponent("Strawberry")));
    }

    @Test
    public void testSelectedComponentsIncludeDeletedComponents() {
        DiagramCodeModel newModel = new DiagramCodeModel();
        Component oldStrawBerryComponent = new Component();
        oldStrawBerryComponent.setComponentName("Strawberry");
        oldStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        DiagramCodeModel oldModel = new DiagramCodeModel(
                new DiagramComponent(oldStrawBerryComponent, null)
        );
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel))
                .coreComponents()
                .contains(new DiagramComponent("Strawberry")));
    }

    @Test
    public void testSelectedComponentsIncludeKeyRelationContextComponents() {
        DiagramCodeModel newModel = new DiagramCodeModel();
        Component oldStrawBerryComponent = new Component();
        oldStrawBerryComponent.setComponentName("Strawberry");
        oldStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        DiagramCodeModel oldModel = new DiagramCodeModel(
                new DiagramComponent(oldStrawBerryComponent, null)
        );
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel))
                .coreComponents()
                .contains(new DiagramComponent("Strawberry")));
    }

    @Test
    public void testSelectedComponentsCoreAreBaseComponentsOnly() {
        Component oldStrawBerryComponent = new Component();
        oldStrawBerryComponent.setComponentName("Strawberry");
        oldStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        Component oldAnimalFieldComponent = new Component();
        oldAnimalFieldComponent.setComponentName("Animal.weight");
        oldAnimalFieldComponent.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);
        Component oldAnimalClassComponent = new Component();
        oldAnimalClassComponent.setComponentName("Animal");
        oldAnimalClassComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        DiagramCodeModel oldModel = new DiagramCodeModel(
                new DiagramComponent(oldAnimalFieldComponent, null),
                new DiagramComponent(oldAnimalClassComponent, null),
                new DiagramComponent(oldStrawBerryComponent, null)
        );
        DiagramCodeModel newModel = new DiagramCodeModel();
        assertEquals(new StiffCodeModel(new DiffCodeModel(oldModel, newModel)).coreComponents().size(), 2);
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel)).coreComponents().contains(
                new DiagramComponent("Animal")));
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel)).coreComponents().contains(
                new DiagramComponent("Strawberry")));
    }

    @Test
    public void testSelectedComponentsContext() {
        Component oldStrawBerryComponent = new Component();
        oldStrawBerryComponent.setComponentName("Strawberry");
        oldStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        oldStrawBerryComponent.insertComponentRef(new TypeExtensionReference("Animal"));

        Component oldAnimalClassComponent = new Component();
        oldAnimalClassComponent.setComponentName("Animal");
        oldAnimalClassComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);

        DiagramCodeModel oldModel = new DiagramCodeModel(
                new DiagramComponent(oldAnimalClassComponent, null),
                new DiagramComponent(oldStrawBerryComponent, null)
        );

        Component newStrawBerryComponent = new Component();
        newStrawBerryComponent.setComponentName("Strawberry");
        newStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        newStrawBerryComponent.insertComponentRef(new TypeExtensionReference("Animal"));

        // Animal class has new field component
        Component newAnimalFieldComponent = new Component();
        newAnimalFieldComponent.setComponentName("Animal.weight");
        newAnimalFieldComponent.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);

        Component newAnimalClassComponent = new Component();
        newAnimalClassComponent.setComponentName("Animal");
        newAnimalClassComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);

        DiagramCodeModel newModel = new DiagramCodeModel(
                new DiagramComponent(newAnimalFieldComponent, null),
                new DiagramComponent(newAnimalClassComponent, null),
                new DiagramComponent(newStrawBerryComponent, null)
        );

        // Because the Strawberry component directly references the Animal class which is a core component, it should be
        // included as a context component.
        assertEquals(new StiffCodeModel(new DiffCodeModel(oldModel, newModel)).contextComponents().size(), 1);
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel)).contextComponents().contains(
                new DiagramComponent("Strawberry")));
    }

    @Test
    public void testSelectedComponentsCoreWithSourceFilesFilter() {
        Component oldStrawBerryComponent = new Component();
        oldStrawBerryComponent.setComponentName("Strawberry");
        oldStrawBerryComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        oldStrawBerryComponent.setSourceFilePath("strawbs.java");
        Component oldAnimalClassComponent = new Component();
        oldAnimalClassComponent.setComponentName("Animal");
        oldAnimalClassComponent.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        oldAnimalClassComponent.setSourceFilePath("animal.java");
        DiagramCodeModel oldModel = new DiagramCodeModel(
                new DiagramComponent(oldAnimalClassComponent, null),
                new DiagramComponent(oldStrawBerryComponent, null)
        );
        DiagramCodeModel newModel = new DiagramCodeModel();
        assertEquals(new StiffCodeModel(new DiffCodeModel(oldModel, newModel), Arrays.asList(new String[]{"animal.java"}))
                .coreComponents().size(), 1);
        assertTrue(new StiffCodeModel(new DiffCodeModel(oldModel, newModel), Arrays.asList(new String[]{"animal.java"}))
                .coreComponents()
                .contains(new DiagramComponent("Animal")));
    }

}
