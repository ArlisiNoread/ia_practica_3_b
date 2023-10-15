package ia_practica_3_a;

import java.util.Scanner;

public class Principal {
	public static DBManager dbManager;
	public static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			Principal.dbManager = new DBManager();
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		new MenuEjecución().start();
	}

}

class MenuEjecución{
	public void start() {
		Scanner scanner = Principal.scanner;
		
		System.out.println("Bienvenido al sistema experto.");
		
		while(true) {
			System.out.println("");
			printOptions();
			System.out.print("Opción: ");
			String option = scanner.nextLine().trim();
			System.out.println("");
			switch (option) {
			case "1": {
				Principal.dbManager.starForwardSearch();
				break;
			}
			case "2": {
				Principal.dbManager.starBackwardSearch();
				break;
			}
			case "3":{
				System.out.println("Imprimiendo reglas de producción encontradas:");
				Principal.dbManager.getArrayClassRules().forEach((regla)->{System.out.println(regla);});
				break;
			}
			case "4": {
				System.out.println("Finalizando sistema.");
				return;
			}
			default:
				System.out.println("Seleccione una opción correcta.");
			}
		}
		
		
		
		
	}
	
	private void printOptions() {
		System.out.println("Seleccione la opción que desea.");
		System.out.println("1.- Iniciar búsqueda.");
		System.out.println("2.- Iniciar búsqueda inversa.");
		System.out.println("3.- Revisar reglas de producción.");
		System.out.println("4.- Salir.");
	}
}