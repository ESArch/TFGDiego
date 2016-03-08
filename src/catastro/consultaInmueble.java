package catastro;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class consultaInmueble {

	public static void main(String [] args)
	{
		String RC="";
		String[] parcel=null;
		String[][] coordinates={{"725500.400543468887918","4372705.106547094881535","LONJA"},
								{"725459.399893339956179","4372610.107194671407342","MERCADO CENTRAL"},
								{"725762.401831573224626","4372925.104410943575203","MONUMENTOS DE LA PLAZA DE LA VIRGEN"},
								{"725625.394661780679598","4371868.110010265372694","ESTACION DEL NORTE"},
								{"725777.930779632763006","4372849.758384290151298","CATEDRAL"},
		                        {"725690.404039961867966","4373241.103233434259892","TORRES DE SERRANOS"},
		                        {"727734.39187064988073","4370759.101072469726205","CIUDAD ARTES Y CIENCIAS"},
		                        {"725426.405622537597083","4373407.103372258134186","MUSEO BENLLIURE"},
		                        {"725261.396756500587799","4372175.11033939756453","MUVIM"}};
		
		for (int i=0; i<coordinates.length; i++) {
			System.out.println(coordinates[i][2]);
			try {
				RC = accesoCatastro.readServiceRC(coordinates[i][0], coordinates[i][1]);  
				parcel = accesoCatastro.readServiceParcel(RC);
				System.out.println(coordinates[i][2] + " -- RC: " + RC + " -- Number of coordinates:" + parcel.length);

				List<String> parcelList = Arrays.asList(parcel);
				for(String s : parcelList)
					System.out.println(s);

				/*for (int j=0; j<parcel.length; j++)
					System.out.print(parcel[j]+ " ");*/
			} catch (Exception e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
