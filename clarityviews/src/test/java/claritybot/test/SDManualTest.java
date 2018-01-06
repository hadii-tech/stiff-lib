package claritybot.test;

import com.clarity.binary.diagram.Diagram;
import com.clarity.binary.diagram.SignedSDDiagram;
import com.clarity.binary.diagram.scheme.LightDiagramColorScheme;
import com.clarity.binary.diagram.view.SDView;
import com.clarity.compiler.Lang;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import org.junit.Test;

import java.io.PrintWriter;

public class SDManualTest {

    @Test
    public void structureDiffManualTest() throws Exception {
        OOPSourceCodeModel oldModel = ClarityTestUtil.getGitHubRepoModel("junit-team", "junit4", "master", "d812a6ef9729a381a374907a96bfe74bdf60173e", Lang.JAVA);
        OOPSourceCodeModel newModel = ClarityTestUtil.getGitHubRepoModel("cesar1000", "junit4", "default_runner", "d812a6ef9729a381a374907a96bfe74bdf60173e", Lang.JAVA);
        Diagram view = new SignedSDDiagram(new SDView(new LightDiagramColorScheme(), oldModel, newModel, 400).view(), "https://clarity-bot.com");
        PrintWriter writer = new PrintWriter("/home/zir0/Desktop/sdTest.svg", "UTF-8");
        writer.println(view.svgText());
        writer.close();
    }
}
