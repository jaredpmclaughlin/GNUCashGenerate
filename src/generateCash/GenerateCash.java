package generateCash;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateCash {
	
	static int spacing = 1;
	static BufferedWriter writer;
	static Double amount = -1.0;
	static String description = "Automatically generated entry.";
	
	static String start;
	static String end;
	static String account;
	static StringBuilder accBuild = new StringBuilder(50);
	static String[] transfer;
	static Integer[] percents;
	static StringBuilder tranBuild = new StringBuilder(50);
	static String filename;
	
	static Double low = -1.0;
	static Double high = -1.0;
	
	static Double step = -1.0; // step size for linear, can also be next increment for others
	static int steps = -1; 
	static int days = 0;
	
	static boolean random = false;
	static boolean linear = false;
	static boolean constant = false;
	
	
	
	private static void openFile(String filename) throws IOException {
		writer = new BufferedWriter(new FileWriter(filename));
	}
	
	private static void writeDeposit(String date, String account, String description, String amount) throws IOException {
//		System.out.println(date + " " + account + " " + description + " " + amount);		
		writer.write(date + "," + account + "," + description + ",$" + amount + ",\n");
	}
	
	private static void writeWithdrawl(String date, String account, String description, String amount) throws IOException {
//		System.out.println(date + " " + account + " " + description + " " + amount);		
		writer.write(date + "," + account + "," + description + ",,$" + amount + "\n");
	}
		
	private static void closeFile() throws IOException{
		writer.close();
	}

	private static void generateEntries(String startDate, String endDate, String account, String[] transfer) throws IOException{
		Date start = new Date(startDate);
		Date end = new Date(endDate);
		Double nAmount;
		Double sAmount;
		
		days = start.daysBetween(end);
		steps = days / spacing;
		
		while(start.lessThan(end)) {
			nAmount = nextAmount();
			writeDeposit(start.toString(), account, description, String.format("%.2f",nAmount));
			for(int n = 0; n<transfer.length; n++) {
				sAmount = nAmount * ((double)(percents[n])/100);
				writeWithdrawl(start.toString(), transfer[n], description,String.format("%.2f",sAmount));
			}
			for(int i = 0; i<spacing; i++) {
				start.nextDate();
			}
		}
	}

	private static void generateMonthly(String startDate, String endDate, String account, String[] transfer) throws IOException{
		Date start = new Date(startDate);
		Date end = new Date(endDate);
		Double nAmount;
		Double sAmount;
		
		while(start.lessThan(end)) {
			nAmount = nextAmount();
			writeDeposit(start.toString(), account, description, String.format("%.2f", nAmount));
			for(int n = 0; n<transfer.length; n++) {
				sAmount = nAmount * ((double)(percents[n])/100);
				writeWithdrawl(start.toString(), transfer[n],description, String.format("%.2f", sAmount));
			}
			start.nextMonth();
		}
	}

	
	private static Double nextAmount() {
				
	 // no set amount, and no linear step
	 if(random) return nextRandom();
	 // not quite right because other stuff uses the step... so.. hmmm
	 if(linear) return nextLinear();
	 
	 return amount;
	}
	
	private static Double nextLinear() {
		if(step == -1.0) {
			step = (high-low)/steps;
			amount = low;
		}else {
			amount += step;
		}
		
		return amount; // placeholder
	}
	
	private static Double nextRandom() {
		Double retval = 0.0;
		Random rand = new Random();
		retval = high+low;
		retval = retval*rand.nextDouble();
		retval = retval*100;
		retval = ((double) Math.round(retval))/100;
		return retval;		
	}
	
	public GenerateCash() {
		// TODO Auto-generated constructor stub
	}

	public static void printHelp() {
		System.out.println("GenerateCash ");
		System.out.println("-s <date>\t(required)\tFirst date of generated entries. ex: -s 11/21/2001");
		System.out.println("-e <date>\t(required)\tLast date of generated entries. ex: -e 12/31/2001");
		System.out.println("-i <integer>\t\t\tNumber of days between entries. Defaults to 1. ex: -i 7");
		System.out.println("-a <string>\t(required)\tAccount to create entries for. ex: -a 'Expenses:Insurance'");
		System.out.println("-t <string>\t(required)\tBalancing account for entries. ex: -t 'Assets:Petty Cash'");
		System.out.println("\t\tor");
		System.out.println("-t<integer> <string>\t(required)\tBalancing account for entries. ex: -t50 'Assets:Petty Cash'");		
		System.out.println("\twhere the integer represents the percentage of the transaction associated with this account");
		System.out.println("\tin the case that the transcation is split between multiple accounts. This means that there");
		System.out.println("\tcan be multiple -t entries, but the percentages should add up to 100.");
		System.out.println("-f <filename>\t(required)\tFilename to output entries to. ex: output.csv");
		System.out.println("\nOne of the following is required: \n");
		System.out.println("-d <number>\t(option 1)\tAll entries are the same (constant), and the values is d. ex: -d 50");
		System.out.println("-r <number>:<number>\t(option 2)\tRandom number between these two. ex: -r 10:100");
		System.out.println("-r <number>:<number> -m <method>\t(option 3)\tGenerate numbers in the range with the given method.");
//		System.out.println("\tValid methods are linear, random, exponential, s-curve. Random is the same as option 2.");
		System.out.println("\tValid methods are linear and random. Random is the same as option 2.");

	}
	
	public static int parseArgs(String[] args) {
		int count = args.length;
		int splits = 0;

		//scan for the amount of transfer accounts, first
		for(int i = 0; i< count-1; i++) {
			if(args[i].charAt(0) == '-' && args[i].charAt(1)=='t' && args[i].length() > 2) {
//				System.out.print(args[i].charAt(2));
				if(args[i].charAt(2) > 47 && args[i].charAt(2) < 58) splits++;
//				System.out.print(args[i].charAt(2));
			}
		}
		
		if(splits == 0 ) splits =1;
		System.out.println("Splits" + splits);
		percents = new Integer[splits]; // room for split percentages
		transfer = new String[splits]; // make room for all the split accounts
		splits = 0;
		
		for(int i = 0; i< count -1; i+=2) {
				//System.out.println(args[i]);
				if (args[i].charAt(0) == '-') {
					
					switch(args[i].charAt(1)) {
						case 115: // -s start
							start = args[i+1];
							break;
						case 101: // -e end
							end = args[i+1];
							break;
						case 105: // -i increment
							spacing = Integer.parseInt(args[i+1]);
							break;
						case 97: // -a account
							// make this parse strings out
							while(i<count-1 && (args[i+1].charAt(0) != '-')) {
								accBuild.append(args[i+1]+" ");
								i++;
							}
							
							i--; // don't break for loop
							//trim the trailing space
							account = accBuild.substring(0, accBuild.length()-1); 
							break;
						case 116: // -t transfer account
							if(transfer.length == 1) {
								while(i<count-1 && (args[i+1].charAt(0) != '-')) {
									tranBuild.append(args[i+1]+" "); // this allows spaces in account names
									i++;
								}
								i--; // don't break for loop
								//trim the trailing space
								percents[0] = 100;
								transfer[0] = tranBuild.substring(0, tranBuild.length()-1); 
								splits++;
							}else {
								// get the percent of the split first
								percents[splits] = 0;
								for(int n = 2; n<args[i].length() && args[i].charAt(n) >47 && args[i].charAt(n)<58; n++) {
									percents[splits] = (percents[splits]*10)+Character.getNumericValue(args[i].charAt(n));
								}
								while(i<count-1 && (args[i+1].charAt(0) != '-')){
									tranBuild.append(args[i+1]+" ");
									i++;
								}
								i--;
								transfer[splits] = tranBuild.substring(0, tranBuild.length()-1);
								tranBuild.setLength(0); // clear out for next account
								splits++;
							}
							
							break;
						case 102: // -f file
							filename = args[i+1];
							break;
						case 100: // -d dollars
							amount = Double.parseDouble(args[i+1]);
							constant = true;
							break;
						case 114: // -r range
							int index = args[i+1].indexOf(":");
							if(index > 0) {
								low = Double.parseDouble(args[i+1].substring(0, index));
								high = Double.parseDouble(args[i+1].substring(index+1));
							}else {
								System.out.println("Error in range (-r ) argument.");
								return -1;
							}
							random = true;
							break;
						case 109: // -m method
							if(args[i+1].compareTo("linear")==0) {
								random = false;
								linear = true;
							}
							if(args[i+1].compareTo("random")==0) {
								random = true;
								linear = false;
							}
							break;
							// need to get the method here, and set random to false in the process
						default: // unidentified arg
							return -1;
					} // switch
					
				} else return -1; // malformed arg
			} // for loop
		//} // else
		
		if(start == null) {
			System.out.println("Missing start date.");
			return 1;
		}
		
		if(end == null) {
			System.out.println("Missing end date.");
			return 1;
		}
		
		if(account == null) {
			System.out.println("Missing account name.");
			return 1;
		}
		
		if(transfer == null) {
			System.out.println("Missing transfer account.");
			return 1;
		}
		
		if(filename == null) {
			System.out.println("Missing file name.");
			return 1;
		}
		
		if( amount == -1 && (low == -1 && high == -1)) {
			System.out.println("Missing amount for transactions.");
			return 1;
		}
		
		if (amount != -1 && (low != -1 && high != -1)) {
			System.out.println("Too many options for transaction amount.");
			return 1;
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		int arg_res = 0;
		
		
		if (args.length == 1 && args[0].charAt(1) == 104) {
			printHelp();
			return;
		}
		
		arg_res = parseArgs(args);
		if ( arg_res != 0) {
			System.out.println("Invalid arguments.");
			return;
		}
		
		if (arg_res == 2) return;

		try {
			openFile(filename);
			if ( spacing != 30) generateEntries(start, end, account, transfer);
			else generateMonthly(start, end, account, transfer);
			closeFile();
		}
		
		catch (IOException e) {
			System.out.println("FILE I/O ERROR.");
		}
		
	}

}
