package ia_practica_3_a;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class DBManager {
	private final String database_rules = "database_rules.txt";

	public DBManager() throws Exception {
		super();
		checkFilesIntegrity();
	}

	public void starBackwardSearch() {
		ArrayList<String> factsDB = new ArrayList<String>();
		ArrayList<Rule> rulesDB = Principal.dbManager.getArrayClassRules();

		System.out.println("");
		addStartFactsForBackWards(factsDB);

		System.out.println("");
		System.out.println("Iniciando Búsqueda Hacia Atrás.");
		System.out.println("La base de datos contiene: ");
		int cnt = 0;
		for (String fact : factsDB) {
			cnt++;
			System.out.println("\t" + cnt + ": " + fact);
		}

		System.out.println("Estoy examinando la base de datos para encontrar una regla aplicable.");
		backwardChaining(factsDB.get(0), factsDB, rulesDB);
		System.out.println("NO HAY MÁS REGLAS APLICABLES");
		System.out.println("EL RESULTADO FINAL ES");
		System.out.println(factsDB);
		

	}

	private boolean backwardChaining(String h, ArrayList<String> factsDB, ArrayList<Rule> rulesDB) {
		System.out.println("ESTOY TRATANDO DE CONFIRMAR EL OBJETIVO: " + h);

		if (matchesAssertion(factsDB, rulesDB))
			return true;
		if (!checkIfTheresRuleWithAConsequentThatMatches(h, rulesDB))
			return false;

		ArrayList<Rule> selectedRules = new ArrayList<Rule>();
		for (Rule rule : rulesDB) {
			if (consequentOfRuleMatchesH(h, rule)) {
				selectedRules.add(rule);
			}
		}

		for (Rule rule : selectedRules) {
			for (String antecedents : rule.conditions) {
				if (!isFactAlreadyInFactDatabase(antecedents, factsDB)) {
					factsDB.add(antecedents);
				}
			}
			boolean allTrue = true;
			for (String antecedents : rule.conditions) {
				if (!backwardChaining(antecedents, factsDB, rulesDB))
					allTrue = false;
				System.out.println("LA REGLA " + rule.id + " DEDUCE: " + antecedents);
			}
			if (allTrue)
				return true;
		}
		return false;
	}

	private boolean consequentOfRuleMatchesH(String h, Rule rule) {
		return rule.fact.equals(h);
	}

	private boolean matchesAssertion(ArrayList<String> factsDB, ArrayList<Rule> rulesDB) {
		String objective = factsDB.get(0);

		for (Rule rule : rulesDB) {
			if (objective.equals(rule.fact)) {
				for (String condition : rule.conditions) {
					if (!isFactAlreadyInFactDatabase(condition, factsDB)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean checkIfTheresRuleWithAConsequentThatMatches(String h, ArrayList<Rule> rulesDB) {
		for (Rule rule : rulesDB) {
			if (rule.fact.equals(h))
				return true;
		}
		return false;
	}

	public void starForwardSearch() {

		ArrayList<String> factsDB = new ArrayList<String>();
		ArrayList<Rule> rulesDB = Principal.dbManager.getArrayClassRules();

		System.out.println("");
		addStartFacts(factsDB);
		System.out.println("");
		System.out.println("Iniciando Búsqueda.");
		System.out.println("La base de datos contiene: ");
		int cnt = 0;
		for (String fact : factsDB) {
			cnt++;
			System.out.println("\t" + cnt + ": " + fact);
		}

		System.out.println("Estoy examinando la base de datos para encontrar una regla aplicable.");

		Rule tempRule = null;
		do {
			tempRule = searchRulesWithFacts(rulesDB, factsDB);
			if (tempRule != null) {
				System.out.println("Estoy aplicando la regla: " + tempRule.id);
				System.out.println("La regla " + tempRule.id + " deduce que es " + tempRule.fact);
				if (!isFactAlreadyInFactDatabase(tempRule.fact, factsDB)) {
					factsDB.add(tempRule.fact);
				}
				System.out.println("La base de datos ahora contiene: ");
				String tempStringForFacts = "";
				for (int x = 0; x < factsDB.size(); x++) {
					tempStringForFacts += " " + factsDB.get(x);
					if (x != factsDB.size() - 1) {
						tempStringForFacts += ",";
					}
				}
				System.out.println(tempStringForFacts.trim());
			}
		} while (tempRule != null);
		System.out.println("No hay más reglas aplicables.");

		if (factsDB.size() != 0) {
			System.out.println("Resultado final: " + factsDB.get(factsDB.size() - 1));
		} else {
			System.out.println("La base de datos de hechos está vacía.");
		}

	}

	private boolean isFactAlreadyInFactDatabase(String newFact, ArrayList<String> factsDB) {
		for (String fact : factsDB) {
			if (fact.equals(newFact)) {
				return true;
			}
		}

		return false;
	}

	private Rule searchRulesWithFacts(ArrayList<Rule> rulesDB, ArrayList<String> factsDB) {
		int cnt = 0;
		for (Rule rule : rulesDB) {
			// Checamos existencia de condiciones en los hechos
			if (checkConditionsInFacts(rule, factsDB)) {
				Rule retRule = rule;
				rulesDB.remove(cnt);
				return retRule;
			}
			cnt++;
		}
		return null;
	}

	private boolean checkConditionsInFacts(Rule rule, ArrayList<String> factsDB) {
		for (String condition : rule.conditions) {
			if (!conditionIsInFacts(condition, factsDB))
				return false;
		}
		return true;
	}

	private boolean conditionIsInFacts(String condition, ArrayList<String> factsDB) {
		for (String fact : factsDB) {
			if (condition.equals(fact))
				return true;
		}
		return false;
	}

	private void addStartFacts(ArrayList<String> factsDB) {
		Scanner scanner = Principal.scanner;

		System.out.println("Ingrese hechos iniciales para la búsqueda.");
		System.out.println("Escriba el hecho y presione Enter. Repita con todos los hechos.");
		System.out.println("Ingresar una cadena vacía marcará el fin del ingreso de hechos.");

		String fact = "";
		do {
			System.out.print("Hecho: ");
			fact = scanner.nextLine().trim();
			if (fact.length() != 0) {
				factsDB.add(fact);
			}
		} while (fact.length() != 0);
	}

	private void addStartFactsForBackWards(ArrayList<String> factsDB) {
		Scanner scanner = Principal.scanner;

		String objetivo = "";

		do {
			System.out.print("Ingrese su objetivo: ");
			objetivo = scanner.nextLine().trim();
			if (objetivo.isEmpty())
				System.out.println("Ingrese un objetivo válido.");
		} while (objetivo.isEmpty());

		factsDB.add(objetivo);

		System.out.println("Ingrese hechos adicionales para la búsqueda.");
		System.out.println("Escriba el hecho y presione Enter. Repita con todos los hechos.");
		System.out.println("Ingresar una cadena vacía marcará el fin del ingreso de hechos.");

		String fact = "";
		do {
			System.out.print("Hecho: ");
			fact = scanner.nextLine().trim();
			if (fact.length() != 0) {
				factsDB.add(fact);
			}
		} while (fact.length() != 0);
	}

	public ArrayList<Rule> getArrayClassRules() {
		ArrayList<String> stringRulesArray = readDBAndGetArrayStringWithAllRules();
		ArrayList<Rule> classRulesArray = new ArrayList<Rule>();

		int ruleCnt = 0;
		for (String ruleString : stringRulesArray) {
			ruleCnt++;
			StringTokenizer tokenizer = new StringTokenizer(ruleString);

			// First token IF.
			String token = tokenizer.nextToken();
			String tempString = "";
			ArrayList<String> conditons = new ArrayList<String>();
			String fact;

			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				if (token.equals("AND") || token.equals("THEN")) {
					conditons.add(tempString.trim());
					tempString = "";
					continue;
				}
				tempString += token + " ";
			}

			fact = tempString.trim();

			Rule ruleClass = new Rule(ruleCnt, conditons, fact);
			classRulesArray.add(ruleClass);
		}
		return classRulesArray;
	}

	public ArrayList<String> readDBAndGetArrayStringWithAllRules() {
		ArrayList<String> rulesArray = new ArrayList<String>();
		try {
			File myObj = new File(database_rules);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if (data.length() == 0)
					continue;
				if (data.charAt(0) == '#')
					continue;
				rulesArray.add(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return rulesArray;
	}

	// Revisa la integridad de los archivos.
	private void checkFilesIntegrity() throws Exception {
		// REVISAMOS INTEGRIDAD DEL ARCHIVO DE REGLAS
		File myObj = new File(database_rules);
		Scanner myReader = new Scanner(myObj);
		int cntLinea = 0;
		while (myReader.hasNextLine()) {
			cntLinea++;

			String data = myReader.nextLine().trim();

			if (data.length() == 0)
				continue;
			if (data.charAt(0) == '#')
				continue;

			StringTokenizer tokenizer = new StringTokenizer(data);
			String firstToken = tokenizer.nextToken();
			if (!firstToken.equals("IF"))
				throw new Exception("Error en linea " + cntLinea + ", Toda regla debe empezar con IF.");

			if (!tokenizer.hasMoreTokens())
				throw new Exception("Error en linea " + cntLinea + ", Se espera una condición posterior a IF.");

			int cntStrings = 0;
			boolean thenFlag = false;
			while (tokenizer.hasMoreTokens()) {
				String nextToken = tokenizer.nextToken();

				if (nextToken.equals("IF"))
					throw new Exception("Error en linea " + cntLinea
							+ ", Solo se permite la palabra reservada IF al inicio de la regla y una sola vez.");

				if (nextToken.equals("AND")) {

					if (thenFlag)
						throw new Exception("Error en linea " + cntLinea
								+ ", Una vez declarado THEN no se pueden agregar más ANDs.");

					if (cntStrings == 0) {
						throw new Exception("Error en linea " + cntLinea + ", Se espera una condición previo al AND.");
					}
					cntStrings = 0;
					continue;
				}

				if (nextToken.equals("THEN")) {
					if (thenFlag)
						throw new Exception("Error en linea " + cntLinea
								+ ", Solo se puede tener una palabra reservada THEN por regla.");

					thenFlag = true;

					if (cntStrings == 0) {
						throw new Exception("Error en linea " + cntLinea + ", Se espera una condición previo al THEN.");
					}
					cntStrings = 0;
					continue;
				}

				cntStrings++;
			}
			if (!thenFlag)
				throw new Exception("Error en linea " + cntLinea + ", Se espera 'THEN acción' al final de la regla.");

			if (cntStrings == 0)
				throw new Exception("Error en linea " + cntLinea + ", Se espera la acción posterior al THEN.");
		}
		myReader.close();
	}

}

class Rule {
	ArrayList<String> conditions;
	String fact;
	int id;

	public Rule(int id, ArrayList<String> conditions, String fact) {
		this.id = id;
		this.conditions = conditions;
		this.fact = fact;
	}

	@Override
	public String toString() {
		String ret = "Regla " + this.id + " - IF ";

		for (int x = 0; x < this.conditions.size(); x++) {
			ret += this.conditions.get(x) + " ";
			if (x != this.conditions.size() - 1) {
				ret += "AND ";
			}
		}
		ret += "THEN " + fact;

		return ret;
	}
}