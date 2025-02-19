import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import java.util.Scanner; 

class AccountGenerator {
	
	public List<Object> rawDataVertifier(String rawData) {
		boolean status = false;
		String reason = "";
		int subjectCode = 0;
		int branchCode = 0;
		int weighting = 100;
		List<Integer> subjectArray = Arrays.asList(10,20,30,40,50,51,66,77,88);
		if(rawData.length() != 11) {
			reason = "起始帳號長度錯誤";
		}else if(Pattern.matches("^\\d{11}$", rawData) == false) {
			reason = "起始帳號須為數字";
		}else {
			subjectCode = ((int) rawData.charAt(3) - '0') * 10 + ((int) rawData.charAt(4) - '0');
			for(int i = 0; i < 3; i += 1) {
				branchCode += ((int) rawData.charAt(i) - '0') * weighting;
				weighting = weighting / 10;
			}
			if(subjectArray.contains(subjectCode) == false) {
				reason = "科目別錯誤";
			}else if((branchCode >= 1 & branchCode <= 99 & branchCode != 19) == false) {
				reason = "分行別錯誤";
			}else {
				status = true;
				reason = "起始帳號正確";
			}
		}
		return Arrays.asList(status, reason);
	}
	
	public List<Object> limitedAccountQuantity(int accountQuantity) {
		int mininum = 1;
		int maxinum = 9999;
		boolean status = true;
		String reason = "";
		if (mininum >= accountQuantity & accountQuantity >= maxinum) {
			reason = "帳號數錯誤";
			status = false;
		}
		return Arrays.asList(status, reason);
	}
	
	public List<String> generator(String rowAccountData, int accountQuantity) {
		int[] rowAccountArray = {0,0,0,0,0,0,0,0,0,0,0};
		int[] weightingOfVertifiedNo = {5,4,3,2,8,7,6,5,4,3,2};
		long rowAccount = 100000000000l;
		long weightingOfAccountNo = 10000000000l;
		List<String> generatedAccountList = new ArrayList<>();
		int startIndex = 0;
		for(int i = 0 ; i < 11 ; i += 1) {
			rowAccount += ((int) (rowAccountData.charAt(i) - '0')) * weightingOfAccountNo;
			weightingOfAccountNo = weightingOfAccountNo / 10;
		}
		startIndex = Pattern.matches("^((00[1-7](30|40|66)\\d{6})|(\\d{3}20\\d{6}))$", rowAccountData) ? 5 : 0;
		for(int i = 0 ; i < accountQuantity ; i += 1) {
			weightingOfAccountNo = 1l;
			long accountNo = rowAccount + i;
			for(int index = 10 ; index >= 0 ; index -= 1){
				rowAccountArray[index] = (int) (accountNo / weightingOfAccountNo % 10);
				weightingOfAccountNo = weightingOfAccountNo * 10;
			}
			int checkCode = 0;
			for(int index = startIndex ; index < 11 ; index += 1){
				checkCode += rowAccountArray[index] * weightingOfVertifiedNo[index];
			}
			checkCode = (11 - (checkCode % 11)) % 10;
			String accountNumber = "";
			for(int eachNumber : rowAccountArray){
				accountNumber = accountNumber + Character.toString((char) (eachNumber + '0'));
			}
			accountNumber = accountNumber + checkCode;
			generatedAccountList.add(accountNumber);
			if (Pattern.matches("^\\d{5}9{6}\\d$", accountNumber)){
				break; // To prevent the function generate the invalid account number which has unexpected subject code.
			}
		}
		return generatedAccountList;
	}
	
	public void save(List<String> accountList, String filePath) {
		try {
            String accountData = "";
			for(String account : accountList) {
				accountData += account + "\n";
			}
            FileWriter writer = new FileWriter(filePath);
            writer.write(accountData);
            writer.close();
            System.out.println("Successfully wrote text to file.");

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
	}
}

public class Main2
{
	public static void main(String[] args) {
	    AccountGenerator accountGenerator = new AccountGenerator();
		while(true){
			Scanner scanner = new Scanner(System.in);
			System.out.print("起始帳號 : ");
			String startAccountPrefix = scanner.nextLine();
			List<Object> rawVertifierResult = accountGenerator.rawDataVertifier(startAccountPrefix);
			if((boolean) rawVertifierResult.get(0) == false) {
				System.out.println((String) rawVertifierResult.get(1));
				break;
			}
			System.out.print("帳號數 : ");
			int accountQuantity = scanner.nextInt();
			List<Object> rawQuantityResult = accountGenerator.limitedAccountQuantity(accountQuantity);
			if((boolean) rawQuantityResult.get(0) == false) {
				System.out.println((String) rawQuantityResult.get(1));
				break;
			}
			List<String> generatedAccountNoList = accountGenerator.generator(startAccountPrefix, accountQuantity);
			for(String generatedAccountNo : generatedAccountNoList){
				System.out.println(generatedAccountNo);
			}
			accountGenerator.save(generatedAccountNoList, "output.txt");
		}
	}
}