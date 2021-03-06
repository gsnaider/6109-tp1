package simulacion;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Simulacion {

	private static final int MAX_NUMS = 1000;
	
	private static final int ULTIMO_NRO_PADRON = 5;

	private static final int PENULTIMO_NRO_PADRON = 1;
	
	private static final int INTERVALOS_HISTOGRAMA = 14;
	
	private static final double BASE_INTERVALOS = 1;
	
	private static final int MINUTOS_EN_HORA = 60;
	
	public static void main(String[] args) {
		
		Random rand = new Random();
		final double lambda130 = calcularLambda(ULTIMO_NRO_PADRON);
		final double lambda152 = calcularLambda(PENULTIMO_NRO_PADRON);
		
		System.out.println("Lambda 130: " + formatDouble(lambda130));
		System.out.println("Lambda 152: " + formatDouble(lambda152));
		
		imprimirCabecera();
		
		double promedio = 0;
		int cantT15 = 0;
		int cant130 = 0;
		double maxTe = 0;
		
		Map<Integer, Integer> frecuenciasAbsolutas = new HashMap<Integer, Integer>();
		for (int i = 0; i < INTERVALOS_HISTOGRAMA; i++ ){
			frecuenciasAbsolutas.put(i, 0);
		}
		
		for (int i = 0; i < MAX_NUMS; i++){
			double u = rand.nextDouble();
			double v = rand.nextDouble();
			
			double t130 = inversaGeneralizada(u, lambda130);
			double t152 = inversaGeneralizada(v, lambda152);
			
			double tiempoEspera = Math.min(t130, t152);
			
			if (i < 10){
				imprimirRegistro(u, v, t130, t152, tiempoEspera);
			}

			promedio += tiempoEspera;
			if (tiempoEspera > 15){
				cantT15++;
			}
			
			if (tiempoEspera == t130){
				cant130++;
			}
			
			int ultimoIntervalo = INTERVALOS_HISTOGRAMA - 1;
			if (tiempoEspera < ultimoIntervalo){
				int intervalo = (int) tiempoEspera;
				frecuenciasAbsolutas.put(intervalo, frecuenciasAbsolutas.get(intervalo) + 1);
			} else {
				frecuenciasAbsolutas.put(ultimoIntervalo, frecuenciasAbsolutas.get(ultimoIntervalo) + 1);
			}
			
			if (tiempoEspera > maxTe){
				maxTe = tiempoEspera;
			}
			
		}
		
		promedio = promedio / MAX_NUMS;
		System.out.println("");
		System.out.println("Promedio Te: " + formatDouble(promedio));
		
		System.out.println("");
		System.out.println("Cantidad de Te mayores a 15: " + cantT15);
		System.out.println("Proporcion de Te mayores a 15: " + formatDouble(cantT15 / (double)MAX_NUMS));
		
		System.out.println("");
		System.out.println("Cantidad de Te = T130: " + cant130);
		System.out.println("Proporcion de Te = T130: " + formatDouble(cant130 / (double)MAX_NUMS));
		
		
		imprimirCabeceraHistograma();
		for (Integer intervalo : frecuenciasAbsolutas.keySet()){
			int frecuenciaAbsoluta = frecuenciasAbsolutas.get(intervalo);
			double frecuenciaRelativa = frecuenciaAbsoluta / (double) MAX_NUMS;
			int ultimoIntervalo = INTERVALOS_HISTOGRAMA - 1;
			double baseIntervalo;
			boolean esUltimoIntervalo = intervalo == ultimoIntervalo;
			if (esUltimoIntervalo) {
				baseIntervalo = maxTe - ultimoIntervalo;
			} else {
				baseIntervalo = BASE_INTERVALOS;
			}
			
			double funcionHistograma;
			if (baseIntervalo > 0){
				funcionHistograma = frecuenciaRelativa / baseIntervalo;
			} else {
				funcionHistograma  = 0;
			}
			imprimirRegistroHistograma(intervalo, intervalo + baseIntervalo, esUltimoIntervalo, frecuenciaAbsoluta, frecuenciaRelativa, funcionHistograma);
		}
		
	}
	
	private static void imprimirCabeceraHistograma() {
		System.out.println("");
		System.out.println("Clase\t\tf_abs\tf_rel\tf_histograma");
	}
	
	private static void imprimirRegistroHistograma(int intervaloMin, double intervaloMax, boolean esUltimoIntervalo, int frecuenciaAbsoluta, double frecuenciaRelativa,
			double funcionHistograma) {
		
		String record = "";
		
		record += formatIntervalo(intervaloMin, intervaloMax, esUltimoIntervalo);
		if (!esUltimoIntervalo){
			record += "\t";
		}
		record += "\t" + frecuenciaAbsoluta;
		record += "\t" + formatDouble(frecuenciaRelativa);
		record += "\t" + formatDouble(funcionHistograma);
		
		System.out.println(record);
	}

	private static String formatIntervalo(int intervaloMin, double intervaloMax, boolean esUltimoIntervalo) {
		String intervalo = "[" + intervaloMin + ";";
		if (esUltimoIntervalo){
			intervalo += formatDouble(intervaloMax) + "]";
		} else {
			intervalo += (int)intervaloMax + ")";
		}
		return intervalo;
	}

	private static boolean isInteger(double num) {
		return ((num == Math.floor(num)) && !Double.isInfinite(num));
	}

	private static String formatDouble(double number){
		NumberFormat formatter = new DecimalFormat("#0.000");
		return formatter.format(number);
	}
	
	private static void imprimirCabecera() {
		System.out.println("");
		System.out.println("U\tV\tT130\tT152\tTe");
	}

	private static void imprimirRegistro(double u, double v, double t130, double t152, double tiempoEspera) {
		double[] nums = {u, v, t130, t152, tiempoEspera};
		String record = "";
		for (int i = 0; i < nums.length; i++){
			if (i > 0){
				record += "\t";
			}
			record += formatDouble(nums[i]);
		}
		System.out.println(record);
		
	}
	
	private static double inversaGeneralizada(double p, double lambda){
		return -Math.log(1 - p)/lambda;
	}

	private static double calcularLambda(int nroPadron) {
		return (nroPadron + 1)/(double)MINUTOS_EN_HORA;
	}

}
