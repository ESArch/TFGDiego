package catastro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;          
import org.jdom2.JDOMException;    


public class accesoCatastro {
	
	public static String readServiceRC(String X, String Y) throws Exception
	{
		String addr="http://ovc.catastro.meh.es/ovcservweb/OVCSWLocalizacionRC/OVCCoordenadas.asmx/Consulta_RCCOOR?SRS=EPSG:4326&Coordenada_X=" + X + "&Coordenada_Y=" + Y;
		URL url = new URL(addr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//conn.setRequestProperty("SRS", "EPSG:25830");
		//conn.setRequestProperty("Coordenada_X", X);
		//conn.setRequestProperty("Coordenada_Y", Y);
		conn.setRequestMethod("GET");
		//conn.connect();
		
		int responseCode = conn.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		String RC="";
		if (responseCode==200)
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			try {
				RC=getRC(in);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				throw e;
			}
		}
		
		conn.disconnect();
		return RC;
	}
	
	
	/*Formato de salida

<consulta_coordenadas>
<control>
<cucoor>N�MERO DE ITEMS EN LA LISTA COORDENADAS</cucoor>
<cuerr>N�MERO DE ITEMS EN LA LISTA DE ERRORES</cuerr>
</control>
<coordenadas>LISTA DE COORDENADAS
<coord>COORDENADA
<pc>REFERENCIA CATASTRAL
<pc1>POSICIONES 1-7 DE LA REFERENCIA CATASTRAL (RC) DEL INMUEBLE</pc1>
<pc2>POSICIONES 8-14 DE LA RC DEL INMUEBLE</pc1>
</pc>
<geo>
<xcen>COORDENADA X SOLICITADA</xcen>
<ycen>COORDENADA Y SOLICITADA</ycen>
<srs>SISTEMA DE REFERENCIA (POR EJEMPLO EPSG:23030)</srs>
</geo>
<ldt>DIRECCI�N (CALLE, N�MERO, MUNICIPIO O POL�GONO, PARCELA Y MUNICIPIO) DE LA PARCELA</ldt>
</coord>
</coordenadas>
</consulta_coordenadas> */
	
	private static String getRC(BufferedReader in) throws Exception
	{
	    //Se crea un SAXBuilder para poder parsear el archivo
	    SAXBuilder builder = new SAXBuilder();
	    String RC="";
	    try
	    {
	        //Se crea el documento a traves del archivo
	        Document document = (Document) builder.build( in );
	 
	        //Se obtiene la raiz 
	        Element rootNode = document.getRootElement();
	 
	        //Se obtiene la lista de hijos de la raiz 
	        List<Element> nodes = rootNode.getChildren();
	    
	        //Procesar control
	        Element control = (Element)nodes.get(0);
	        List<Element> control_children = control.getChildren();
	        int num_resultados = Integer.parseInt(((Element) control_children.get(0)).getValue());
	        int num_errores = Integer.parseInt(((Element) control_children.get(1)).getValue());
	        if (num_resultados!=1 || num_errores!=0)
	        	throw new Exception ("Invalid response");
	        
	        //Procesar RC
	        Element coordenadas = (Element)nodes.get(1);
	        List<Element> coor_children = coordenadas.getChildren().get(0).getChildren().get(0).getChildren();
	        /*for (int i=0; i<coor_children.size(); i++) {
	        	Element node = (Element)coor_children.get(i);
	        	System.out.println("Element " + i + ":" + node.getName() + "--" + node.getValue());
	        }*/
	        RC=((Element) coor_children.get(0)).getValue() + ((Element) coor_children.get(1)).getValue();
	        //System.out.println(RC);
	        
	        
	    }catch ( IOException io ) {
	        //System.out.println( io.getMessage() );
	    	throw io;
	    }catch ( JDOMException jdomex ) {
	        //System.out.println( jdomex.getMessage() );
	    	throw jdomex;
	    }
	    return RC;
	}
	
	
	public static String[] readServiceParcel(String RC) throws Exception
	{
		String addr="http://ovc.catastro.meh.es/INSPIRE/wfsBU.aspx?service=wfs&version=2&request=getfeature&STOREDQUERIE_ID=GETBUILDINGBYPARCEL&37824724104173&refcat=" + RC;
		URL url = new URL(addr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//conn.setRequestProperty("SRS", "EPSG:25830");
		//conn.setRequestProperty("Coordenada_X", X);
		//conn.setRequestProperty("Coordenada_Y", Y);
		conn.setRequestMethod("GET");
		//conn.connect();
		
		int responseCode = conn.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		String[] parcel=null;
		if (responseCode==200)
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			try {
				parcel=getParcel(in);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				throw e;
			}
		}
		
		conn.disconnect();
		return parcel;
	}
	
	/*Formato de salida

<gml:FeatureCollection xmlns:ad="urn:x-inspire:specification:gmlas:Addresses:3.0" xmlns:base="urn:x-inspire:specification:gmlas:BaseTypes:3.2" xmlns:bu-base="http://inspire.jrc.ec.europa.eu/schemas/bu-base/3.0" xmlns:bu-core2d="http://inspire.jrc.ec.europa.eu/schemas/bu-core2d/2.0" xmlns:bu-ext2d="http://inspire.jrc.ec.europa.eu/schemas/bu-ext2d/2.0" xmlns:cp="urn:x-inspire:specification:gmlas:CadastralParcels:3.0" xmlns:el-bas="http://inspire.jrc.ec.europa.eu/schemas/el-bas/2.0" xmlns:el-cov="http://inspire.jrc.ec.europa.eu/schemas/el-cov/2.0" xmlns:el-tin="http://inspire.jrc.ec.europa.eu/schemas/el-tin/2.0" xmlns:el-vec="http://inspire.jrc.ec.europa.eu/schemas/el-vec/2.0" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:gmlcov="http://www.opengis.net/gmlcov/1.0" xmlns:gn="urn:x-inspire:specification:gmlas:GeographicalNames:3.0" xmlns:gsr="http://www.isotc211.org/2005/gsr" xmlns:gss="http://www.isotc211.org/2005/gss" xmlns:gts="http://www.isotc211.org/2005/gts" xmlns:swe="http://www.opengis.net/swe/2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" gml:id="ES.SDGC.BU" xsi:schemaLocation="http://inspire.jrc.ec.europa.eu/schemas/bu-ext2d/2.0 http://inspire.ec.europa.eu/draft-schemas/bu-ext2d/2.0/BuildingExtended2D.xsd">
<gml:featureMember>
<bu-ext2d:Building gml:id="ES.SDGC.BU.5629101YJ2752H">
<gml:boundedBy>...</gml:boundedBy>
<bu-core2d:beginLifespanVersion>2001-11-19T00:00:00</bu-core2d:beginLifespanVersion>
<bu-core2d:conditionOfConstruction>functional</bu-core2d:conditionOfConstruction>
<bu-core2d:dateOfConstruction>...</bu-core2d:dateOfConstruction>
<bu-core2d:endLifespanVersion xsi:nil="true" nilReason="other:unpopulated"/>
<bu-core2d:externalReference>...</bu-core2d:externalReference>
<bu-core2d:inspireId>...</bu-core2d:inspireId>
<bu-core2d:addresses xlink:href="http://ovc.catastro.meh.es/INSPIRE/wfsAD.aspx?service=wfs&version=2&request=GetFeature&STOREDQUERIE_ID=GetadByRefcat&refcat=5629101YJ2752H&srsname=EPSG::25830"/>
<bu-core2d:cadastralParcels xlink:href="http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&version=2&request=GetFeature&STOREDQUERIE_ID=GetParcel&refcat=5629101YJ2752H&srsname=EPSG::25830"/>
<bu-ext2d:geometry>
<bu-core2d:BuildingGeometry>
<bu-core2d:geometry>
<gml:Surface gml:id="Surface_ES.SDGC.BU.5629101YJ2752H" srsName="urn:ogc:def:crs:EPSG::25830">
<gml:patches>
<gml:PolygonPatch>
<gml:exterior>
<gml:LinearRing>
<gml:posList srsDimension="2" count="26">
725487.02 4372704.77 725492.52 4372698.65 725507.58 4372712.22 725489.48 4372732.21 725496.07 4372738.05 725514.08 4372718.13 725519.35 4372712.3 725519.94 4372712.31 725525.06 4372706.66 725525.05 4372706.07 725530.37 4372700.2 725518.07 4372688.97 725514.1 4372685.36 725501.8 4372674.18 725501.75 4372674.18 725496.14 4372680.18 725495.86 4372680.29 725490.9 4372685.74 725490.89 4372686.1 725486.33 4372691.14 725479.91 4372698.25 725479.87 4372698.3 725466.87 4372711.83 725466.85 4372712.09 725473.99 4372718.47 725487.02 4372704.77
</gml:posList>
</gml:LinearRing>
</gml:exterior>
</gml:PolygonPatch>
</gml:patches>
</gml:Surface>
</bu-core2d:geometry>
<bu-core2d:horizontalGeometryEstimatedAccuracy uom="m">0.1</bu-core2d:horizontalGeometryEstimatedAccuracy>
<bu-core2d:horizontalGeometryReference>footPrint</bu-core2d:horizontalGeometryReference>
<bu-core2d:referenceGeometry>true</bu-core2d:referenceGeometry>
</bu-core2d:BuildingGeometry>
</bu-ext2d:geometry> */
	
	private static String[] getParcel(BufferedReader in) throws Exception
	{
	    //Se crea un SAXBuilder para poder parsear el archivo
	    SAXBuilder builder = new SAXBuilder();
	    String[] parcel=null;
	    try
	    {
	        //Se crea el documento a traves del archivo
	        Document document = (Document) builder.build( in );
	 
	        //Se obtiene la raiz 
	        Element rootNode = document.getRootElement();
	 
	        //Se obtiene la lista de hijos de la raiz 
	        List<Element> nodes = rootNode.getChildren();
	        
	        //Procesar geometria
	        Element control = (Element)nodes.get(0);
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);
	        nodes = control.getChildren();
	        control = (Element)nodes.get(9);  //Geometry
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //Building_geometry
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //Surface
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //patches
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //PolygonPatch
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //exterior
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //LinearRing
	        nodes = control.getChildren();
	        control = (Element)nodes.get(0);  //posList
	        nodes = control.getChildren();
	        Element node = (Element)nodes.get(0);
	        String coor_string=node.getValue();
        	parcel=coor_string.split(" ");
        	//System.out.println(coor_string);        
	        
	    }catch ( IOException io ) {
	        //System.out.println( io.getMessage() );
	    	throw io;
	    }catch ( JDOMException jdomex ) {
	        //System.out.println( jdomex.getMessage() );
	    	throw jdomex;
	    }
	    return parcel;
	}
	
}