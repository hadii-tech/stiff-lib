package claritybot.test;

import com.clarity.binary.diagram.Diagram;
import com.clarity.binary.diagram.DiagramSourceCodeModel;
import com.clarity.binary.diagram.scheme.LightDiagramColorScheme;
import com.clarity.binary.diagram.view.EmptySDException;
import com.clarity.binary.diagram.view.SDView;
import com.clarity.compiler.Lang;
import com.clarity.invocation.TypeDeclaration;
import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.junit.Test;

import java.io.PrintWriter;

public class SDViewTest {

    @Test
    public void manualTest() throws Exception {
        DiagramSourceCodeModel oldModel = ClarityTestUtil.getGitHubRepoModel("google",
                "go-github", "e48060a28fac52d0f1cb758bc8b87c07bac4a87d", "01fe37630d630dfecbd504faed22688d6de0aab6", Lang.GOLANG);
        DiagramSourceCodeModel newModel = ClarityTestUtil.getGitHubRepoModel(
                "anubhakushwaha", "go-github", "8c4c9a9cccc0ba7e92eb840c900eaac3766b5e42", "01fe37630d630dfecbd504faed22688d6de0aab6", Lang.GOLANG);
        Diagram view = new SDView(new LightDiagramColorScheme(), oldModel, newModel, 400).view();
        PrintWriter writer = new PrintWriter("/home/zir0/Desktop/sdTest.svg", "UTF-8");
        writer.println(view.svgText());
        writer.close();
    }

    @Test(expected = EmptySDException.class)
    public void testEmptySDException() throws Exception {
        DiagramSourceCodeModel oldModel = new DiagramSourceCodeModel(new OOPSourceCodeModel());
        DiagramSourceCodeModel newModel = new DiagramSourceCodeModel(new OOPSourceCodeModel());
        new SDView(new LightDiagramColorScheme(), oldModel, newModel, 400).view();
    }

    @Test
    public void newRelationshipTest() throws Exception {
        Component a = new Component();

        a.setPackageName("com.test");
        a.setComponentName("classA");
        a.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        a.setName("classA");
        Component aField = new Component();
        aField.setPackageName("com.test");
        aField.setComponentName("classA.aField");
        aField.setComponentType(OOPSourceModelConstants.ComponentType.FIELD);
        aField.setName("aField");
        a.insertChildComponent("com.test.classA.aField");
        Component b = new Component();
        b.setPackageName("com.test");
        b.setComponentName("classB");
        b.setComponentType(OOPSourceModelConstants.ComponentType.CLASS);
        b.setName("classB");
        OOPSourceCodeModel oldModel = new OOPSourceCodeModel();
        oldModel.insertComponent(a);
        oldModel.insertComponent(aField);
        oldModel.insertComponent(b);
        // add type declaration to new model.
        Component newAField = new Component(aField);
        newAField.insertComponentInvocation(new TypeDeclaration("com.test.classB"));
        OOPSourceCodeModel newModel = new OOPSourceCodeModel();
        newModel.insertComponent(a);
        newModel.insertComponent(newAField);
        newModel.insertComponent(b);
        // expect no Exception thrown and a valid sd is generated...
        new SDView(new LightDiagramColorScheme(), new DiagramSourceCodeModel(oldModel), new DiagramSourceCodeModel(newModel), 400).view();
    }

}
