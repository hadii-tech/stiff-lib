package com.clarity.binary.diagram;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLClassDiagram implements ClassDiagram {

  private static final String PLANT_UML_BEGIN_STRING = "@startuml\n";
  private static final String PLANT_UML_END_STRING = "\n@enduml";
private PlantUMLClassDiagramDesciption plantUMLClassDiagramDescription;
private ClassDiagramColorScheme colorScheme;

  public  PlantUMLClassDiagram(final PlantUMLClassDiagramDesciption plantUMLClassDescription,
		  ClassDiagramColorScheme colorScheme) {
	this.plantUMLClassDiagramDescription = plantUMLClassDescription;
	this.colorScheme = colorScheme;
}
  @Override
  public final String svgText()
				  throws InterruptedException, IOException {

	  final long startTime = new Date().getTime();	  
	  final String plantUMLString = genPlantUMLString();
	  final byte[] diagram = generateDiagram(plantUMLString);
	  final String diagramStr = new String(diagram);
	  System.out.println(" Clarity View diagram SVG text generated in " + (new Date().getTime() - startTime)
			  + " milliseconds.");
	  return diagramStr;
  }
  
  /**
   * Forms a PlantUML compliant String representing the class diagram.
   */
  private String genPlantUMLString() throws IOException {

	  final String diagramSkin =  formPlantUMLSkinString(colorScheme);
	  
	  final String source = PLANT_UML_BEGIN_STRING 
			  + diagramSkin 
			  + plantUMLClassDiagramDescription.classDesciptionString() 
			  + plantUMLClassDiagramDescription.relationsDesciptionString()
			  + PLANT_UML_END_STRING;
	  return source;
  }

  private String formPlantUMLSkinString(ClassDiagramColorScheme colorScheme) {
	  return  "skinparam defaultFontName " + colorScheme.defaultFontName() + "\n"
			  + "skinparam backgroundColor  " + colorScheme.backgroundColor() + "\n" 
			  + "skinparam classArrowFontName " + colorScheme.classArrowFontName() + "\n"
			  + "skinparam classArrowColor " + colorScheme.classArrowColor() + "\n" 
			  + "skinparam classBackgroundColor " + colorScheme.classBackgroundColor() + "\n"
			  + "skinparam classArrowFontColor " + colorScheme.classArrowFontColor() + "\n" 
			  + "skinparam classArrowFontSize " + colorScheme.classArrowFontSize() + "\n"
			  + "skinparam classFontColor " + colorScheme.classArrowFontColor() + "\n"
			  + "skinparam classFontSize " + colorScheme.classFontSize() + "\n" 
			  + "skinparam classStereotypeFontColor " + colorScheme.classStereotypeFontColor() + "\n" 
			  + "skinparam classAttributeFontColor " + colorScheme.classAttributeFontColor() + "\n"
			  + "skinparam classAttributeFontSize " + colorScheme.classAttributeFontSize() + "\n"
			  + "skinparam classFontName " + colorScheme.classFontName() + "\n"
			  + "skinparam classAttributeFontName " + colorScheme.classAttributeFontName() + "\n" 
			  + "skinparam titleFontColor " + colorScheme.titleFontColor() + "\n"  
			  + "skinparam packageBackgroundColor " + colorScheme.packageBackgroundColor() + "\n"
			  + "skinparam titleFontName " + colorScheme.titleFontName() + "\n"
			  + "skinparam packageBorderColor " + colorScheme.packageBorderColor() + "\n"
			  + "skinparam packageFontColor " + colorScheme.packageFontColor() + "\n" 
			  + "skinparam packageFontName " + colorScheme.packageFontName() + "\n"
			  + "skinparam packageFontStyle " + colorScheme.packageFontStyle() + "\n"
			  + "skinparam packageFontSize " + colorScheme.packageFontSize() + "\n"
			  + "skinparam classBorderColor " + colorScheme.classBorderColor() + "\n";
  }

  /**
   * Invokes PlantUML to draw the class diagram based on the source string
   * representing a PlantUML compliant class diagram description.
   */
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
