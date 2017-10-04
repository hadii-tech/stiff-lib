package claritybot.test;

import org.junit.Test;

import com.clarity.binary.diagram.DiagramConstants.BinaryClassAssociation;
import com.clarity.binary.extractor.BinaryClassRelationship;
import com.clarity.binary.extractor.ExternalClassLink;
import com.clarity.sourcemodel.Component;

public class BinaryClassRelationshipTest {

    @Test
    public void equalBinaryClassRelationshipsTest() {
        Component cmpA = new Component();
        cmpA.setName("classA");
        cmpA.setComponentName("classA");
        Component cmpB = new Component();
        cmpB.setName("classB");
        cmpB.setComponentName("classB");
        ExternalClassLink link = new ExternalClassLink(cmpA, cmpB, null, null, null,
                BinaryClassAssociation.COMPOSITION);
        BinaryClassRelationship relA = new BinaryClassRelationship(link);
        BinaryClassRelationship relB = new BinaryClassRelationship(link);
        assert (relA.hashCode() == relB.hashCode());
    }

    @Test
    public void unEqualBinaryClassRelationshipsByName() {
        Component cmpA = new Component();
        cmpA.setName("classA");
        cmpA.setComponentName("classA");
        Component cmpB = new Component();
        cmpB.setName("classB");
        cmpB.setComponentName("classB");
        Component cmpC = new Component();
        cmpC.setName("classC");
        cmpC.setComponentName("classC");
        ExternalClassLink link = new ExternalClassLink(cmpB, cmpA, null, null, null,
                BinaryClassAssociation.COMPOSITION);
        BinaryClassRelationship relA = new BinaryClassRelationship(link);
        ExternalClassLink link2 = new ExternalClassLink(cmpA, cmpC, null, null, null,
                BinaryClassAssociation.COMPOSITION);
        BinaryClassRelationship relB = new BinaryClassRelationship(link2);
        assert (relA.hashCode() != relB.hashCode());
    }

    @Test
    public void unequalBinaryClassRelationshipsByAssociation() {
        Component cmpA = new Component();
        cmpA.setName("classA");
        cmpA.setComponentName("classA");
        Component cmpB = new Component();
        cmpB.setName("classB");
        cmpB.setComponentName("classB");
        ExternalClassLink link = new ExternalClassLink(cmpB, cmpA, null, null, null,
                BinaryClassAssociation.COMPOSITION);
        BinaryClassRelationship relA = new BinaryClassRelationship(link);
        ExternalClassLink link2 = new ExternalClassLink(cmpB, cmpA, null, null, null,
                BinaryClassAssociation.AGGREGATION);
        BinaryClassRelationship relB = new BinaryClassRelationship(link2);
        assert (relA.hashCode() != relB.hashCode());
    }
}
